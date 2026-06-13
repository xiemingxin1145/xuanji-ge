package com.aning.xuanxue.core.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

/**
 * 玄机阁基础音效管理器。
 *
 * v1.6：支持本机开关保存；当前用系统 ToneGenerator 做轻量提示音，
 * 后续可替换为 SoundPool + res/raw 音效素材。
 *
 * v2.2：补充阴阳录探测/取证需要的轻量音效枚举，先不引入音频素材，保证 APK 仍可纯源码构建。
 */
object XuanSound {
    enum class Effect {
        Click,
        Open,
        Success,
        Warning,
        Draw,
        Compass,
        AiReply,
        Bell,
        Wind,
        Seal,
        GhostNear
    }

    private const val PREFS = "xuan_sound_prefs"
    private const val KEY_ENABLED = "sound_enabled"

    private var memoryEnabled: Boolean = true
    private var tone: ToneGenerator? = null

    fun setEnabled(context: Context, value: Boolean) {
        memoryEnabled = value
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, value)
            .apply()
    }

    fun isEnabled(context: Context): Boolean {
        val saved = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, memoryEnabled)
        memoryEnabled = saved
        return saved
    }

    fun play(context: Context, effect: Effect) {
        if (!isEnabled(context)) return
        try {
            val generator = tone ?: ToneGenerator(AudioManager.STREAM_MUSIC, 24).also { tone = it }
            val toneType = when (effect) {
                Effect.Click -> ToneGenerator.TONE_PROP_BEEP
                Effect.Open -> ToneGenerator.TONE_PROP_ACK
                Effect.Success -> ToneGenerator.TONE_PROP_PROMPT
                Effect.Warning -> ToneGenerator.TONE_PROP_NACK
                Effect.Draw -> ToneGenerator.TONE_PROP_BEEP2
                Effect.Compass -> ToneGenerator.TONE_PROP_BEEP
                Effect.AiReply -> ToneGenerator.TONE_PROP_ACK
                Effect.Bell -> ToneGenerator.TONE_PROP_BEEP2
                Effect.Wind -> ToneGenerator.TONE_PROP_NACK
                Effect.Seal -> ToneGenerator.TONE_PROP_PROMPT
                Effect.GhostNear -> ToneGenerator.TONE_PROP_NACK
            }
            val duration = when (effect) {
                Effect.Click -> 36
                Effect.Open -> 48
                Effect.Success -> 72
                Effect.Warning -> 90
                Effect.Draw -> 64
                Effect.Compass -> 32
                Effect.AiReply -> 60
                Effect.Bell -> 42
                Effect.Wind -> 120
                Effect.Seal -> 110
                Effect.GhostNear -> 150
            }
            generator.startTone(toneType, duration)
        } catch (_: Throwable) {
            // 音频不可用时静默失败，不能影响主功能。
        }
    }

    fun release() {
        try {
            tone?.release()
        } catch (_: Throwable) {
        } finally {
            tone = null
        }
    }
}
