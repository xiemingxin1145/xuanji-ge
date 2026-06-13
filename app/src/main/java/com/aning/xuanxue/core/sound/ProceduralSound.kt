package com.aning.xuanxue.core.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random

/**
 * 玄机阁 · 程序合成音效引擎
 *
 * 不依赖任何 .mp3/.wav 资源文件——全部用 AudioTrack 实时合成 PCM。
 * 对恐怖+玄学题材收益极高、成本极低（应 GPT 第三优先级方案）。
 *
 * 音效清单：
 *   阴风   COLD_WIND    低频噪声 + 缓慢音量起伏
 *   风铃   WIND_CHIME   高频短促叮声（多泛音衰减）
 *   磬声   QING_BELL    金属感正弦 + 长衰减
 *   符燃   TALISMAN     白噪声爆裂 + 快速衰减
 *   近身   HEARTBEAT    低频双击心跳 + 呼吸
 *   起卦   DIVINE       木鱼般的短闷响
 *
 * 这些是合成参数，不是录音；改一个数就能调音色。
 */

object ProceduralSound {

    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    enum class Sfx { COLD_WIND, WIND_CHIME, QING_BELL, TALISMAN, HEARTBEAT, DIVINE }

    /** 播放一次性音效 */
    fun play(sfx: Sfx, volume: Float = 0.7f) {
        scope.launch {
            val samples = when (sfx) {
                Sfx.WIND_CHIME -> genWindChime()
                Sfx.QING_BELL  -> genQingBell()
                Sfx.TALISMAN   -> genTalismanBurn()
                Sfx.HEARTBEAT  -> genHeartbeat()
                Sfx.DIVINE     -> genDivineKnock()
                Sfx.COLD_WIND  -> genColdWind(2.0)
            }
            playPcm(samples, volume)
        }
    }

    // ── 持续环境音（阴风 loop），返回可停止的 Job ──
    fun startAmbientWind(volume: Float = 0.35f): Job = scope.launch {
        val track = makeTrack(streamMode = true)
        track.play()
        val buf = ShortArray(SAMPLE_RATE / 4)
        var phase = 0.0
        var lfo = 0.0
        try {
            while (isActive) {
                for (i in buf.indices) {
                    // 棕噪声近似（低频偏重）
                    val white = Random.nextDouble(-1.0, 1.0)
                    phase = phase * 0.96 + white * 0.04
                    // 缓慢音量起伏（LFO 0.1Hz）
                    lfo += 0.1 * 2 * PI / SAMPLE_RATE
                    val env = 0.5 + 0.5 * sin(lfo)
                    val s = phase * env * volume
                    buf[i] = (s * Short.MAX_VALUE).toInt()
                        .coerceIn(-32768, 32767).toShort()
                }
                track.write(buf, 0, buf.size)
            }
        } finally {
            track.stop(); track.release()
        }
    }

    // ─────────────────────────────────────────
    // 合成器实现
    // ─────────────────────────────────────────

    private fun genColdWind(seconds: Double): ShortArray {
        val n = (SAMPLE_RATE * seconds).toInt()
        val out = ShortArray(n)
        var brown = 0.0
        for (i in 0 until n) {
            val white = Random.nextDouble(-1.0, 1.0)
            brown = brown * 0.97 + white * 0.03
            val env = sin(PI * i / n)          // 淡入淡出
            out[i] = (brown * env * 0.9 * Short.MAX_VALUE).toInt()
                .coerceIn(-32768, 32767).toShort()
        }
        return out
    }

    private fun genWindChime(): ShortArray {
        // 三枚铜铃，不同基频，错开起音
        val dur = 1.4
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        val bells = listOf(1320.0 to 0.0, 1760.0 to 0.12, 2090.0 to 0.26)
        for ((freq, delay) in bells) {
            val start = (delay * SAMPLE_RATE).toInt()
            for (i in start until n) {
                val t = (i - start).toDouble() / SAMPLE_RATE
                val decay = exp(-3.5 * t)
                // 基频 + 二泛音，金属感
                val s = sin(2 * PI * freq * t) * 0.7 +
                        sin(2 * PI * freq * 2.76 * t) * 0.3
                out[i] += s * decay * 0.33
            }
        }
        return toShorts(out)
    }

    private fun genQingBell(): ShortArray {
        // 磬：基频低、衰减长、含敲击瞬态
        val dur = 2.6
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        val f0 = 523.0
        val partials = listOf(1.0 to 1.0, 2.7 to 0.5, 5.4 to 0.25, 8.9 to 0.1)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            var s = 0.0
            for ((mult, amp) in partials) {
                s += sin(2 * PI * f0 * mult * t) * amp * exp(-1.4 * mult * t)
            }
            // 起音瞬态
            val attack = if (t < 0.005) t / 0.005 else 1.0
            out[i] = s * attack * 0.4
        }
        return toShorts(out)
    }

    private fun genTalismanBurn(): ShortArray {
        // 符纸燃烧：白噪声 + 几次爆裂
        val dur = 0.9
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val crackle = if (Random.nextDouble() < 0.004) Random.nextDouble(-1.0, 1.0) else 0.0
            val hiss = Random.nextDouble(-1.0, 1.0) * 0.25
            out[i] = (hiss + crackle) * exp(-2.5 * t) * 0.8
        }
        return toShorts(out)
    }

    private fun genHeartbeat(): ShortArray {
        // 双击低频心跳 "咚-咚"
        val dur = 1.0
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        fun thump(center: Double, amp: Double) {
            val c = (center * SAMPLE_RATE).toInt()
            val len = SAMPLE_RATE / 8
            for (j in 0 until len) {
                val idx = c + j
                if (idx in 0 until n) {
                    val t = j.toDouble() / SAMPLE_RATE
                    out[idx] += sin(2 * PI * 55.0 * t) * exp(-22 * t) * amp
                }
            }
        }
        thump(0.05, 0.9)
        thump(0.28, 0.6)
        return toShorts(out)
    }

    private fun genDivineKnock(): ShortArray {
        // 木鱼/起卦的短闷响
        val dur = 0.35
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val s = sin(2 * PI * 180.0 * t) * exp(-30 * t) +
                    Random.nextDouble(-1.0, 1.0) * 0.15 * exp(-60 * t)
            out[i] = s * 0.6
        }
        return toShorts(out)
    }

    // ─────────────────────────────────────────
    private fun toShorts(d: DoubleArray): ShortArray =
        ShortArray(d.size) { (d[it].coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort() }

    private fun makeTrack(streamMode: Boolean): AudioTrack {
        val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBuf, SAMPLE_RATE))
            .setTransferMode(
                if (streamMode) AudioTrack.MODE_STREAM else AudioTrack.MODE_STATIC
            )
            .build()
    }

    private fun playPcm(samples: ShortArray, volume: Float) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(samples, 0, samples.size)
        track.setVolume(volume)
        track.play()
        // 播完自动释放
        scope.launch {
            delay((samples.size * 1000L / SAMPLE_RATE) + 200)
            runCatching { track.stop(); track.release() }
        }
    }
}
