package com.aning.xuanxue.core.sound

import android.content.Context
import android.media.*
import android.os.Build
import kotlinx.coroutines.*

/**
 * 最后一个道士 · 音效系统
 * 使用 SoundPool（短音效）+ MediaPlayer（背景氛围）
 * 音频文件放置于 res/raw/ 目录，不存在时静默降级
 */
object SoundManager {

    // ─── 音效枚举 ──────────────────────────────────
    enum class Sfx(val resName: String, val desc: String) {
        GHOST_APPEAR(  "sfx_ghost_appear",   "鬼魂显现"),
        GHOST_CAUGHT(  "sfx_ghost_caught",   "封印成功"),
        GHOST_FLED(    "sfx_ghost_fled",     "鬼魂逃脱"),
        SEAL_CHARGE(   "sfx_seal_charge",    "符咒充能"),
        TALISMAN_DRAW( "sfx_talisman_draw",  "画符"),
        CASE_OPEN(     "sfx_case_open",      "开启卷宗"),
        CASE_SOLVED(   "sfx_case_solved",    "案件告破"),
        COMPASS_SPIN(  "sfx_compass_spin",   "罗盘转动"),
        ICHING_SHAKE(  "sfx_iching_shake",   "起卦摇签"),
        REALM_UP(      "sfx_realm_up",       "境界突破"),
        UI_CLICK(      "sfx_ui_click",       "界面点击"),
        UI_BACK(       "sfx_ui_back",        "返回"),
        RESONANCE_EPIC("sfx_resonance_epic", "玄机爆发"),
        THUNDER(       "sfx_thunder",        "惊雷"),
        BELL(          "sfx_bell",           "铜铃"),
    }

    enum class Bgm(val resName: String, val desc: String) {
        MAIN_MENU(   "bgm_main_menu",    "主菜单·道韵"),
        CASE_NORMAL( "bgm_case_normal",  "接案·市井"),
        CASE_EERIE(  "bgm_case_eerie",   "调查·阴煞"),
        GHOST_HUNT(  "bgm_ghost_hunt",   "捉鬼·紧张"),
        CULTIVATION( "bgm_cultivation",  "修炼·禅定"),
        AR_AMBIENT(  "bgm_ar_ambient",   "AR·现实感应"),
        VICTORY(     "bgm_victory",      "结案·告破")
    }

    private var soundPool: SoundPool? = null
    private val loadedSounds = mutableMapOf<String, Int>()
    private var bgmPlayer: MediaPlayer? = null
    private var currentBgm: Bgm? = null
    private var isMuted = false

    // ─── 初始化 ──────────────────────────────────
    fun init(context: Context) {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(attrs)
            .build()
        // 预加载所有存在的音效
        Sfx.entries.forEach { sfx ->
            val resId = context.resources.getIdentifier(sfx.resName, "raw", context.packageName)
            if (resId != 0) {
                val soundId = soundPool?.load(context, resId, 1) ?: 0
                if (soundId != 0) loadedSounds[sfx.resName] = soundId
            }
        }
    }

    // ─── 播放短音效 ───────────────────────────────
    fun playSfx(sfx: Sfx, volume: Float = 0.85f) {
        if (isMuted) return
        val soundId = loadedSounds[sfx.resName] ?: return
        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    // ─── 背景音乐 ─────────────────────────────────
    fun playBgm(context: Context, bgm: Bgm, loop: Boolean = true, fadeIn: Boolean = true) {
        if (isMuted || currentBgm == bgm) return
        val resId = context.resources.getIdentifier(bgm.resName, "raw", context.packageName)
        if (resId == 0) return  // 文件不存在，静默降级

        CoroutineScope(Dispatchers.Main).launch {
            if (fadeIn) fadeOutBgm()
            bgmPlayer?.release()
            bgmPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(context.resources.openRawResourceFd(resId))
                isLooping = loop
                prepare()
                if (fadeIn) {
                    setVolume(0f, 0f)
                    start()
                    fadeInBgm()
                } else {
                    setVolume(0.6f, 0.6f)
                    start()
                }
            }
            currentBgm = bgm
        }
    }

    fun stopBgm(fade: Boolean = true) {
        CoroutineScope(Dispatchers.Main).launch {
            if (fade) fadeOutBgm()
            bgmPlayer?.apply { if (isPlaying) stop(); release() }
            bgmPlayer = null
            currentBgm = null
        }
    }

    fun toggleMute() { isMuted = !isMuted; if (isMuted) stopBgm(false) }
    fun isMuted() = isMuted

    private suspend fun fadeOutBgm(durationMs: Long = 600L) {
        val player = bgmPlayer ?: return
        val steps = 20
        val delay = durationMs / steps
        repeat(steps) { i ->
            val vol = 0.6f * (1f - (i + 1f) / steps)
            player.setVolume(vol, vol)
            delay(delay)
        }
        player.stop()
    }

    private suspend fun fadeInBgm(durationMs: Long = 800L) {
        val player = bgmPlayer ?: return
        val steps = 20
        val delay = durationMs / steps
        repeat(steps) { i ->
            val vol = 0.6f * ((i + 1f) / steps)
            player.setVolume(vol, vol)
            delay(delay)
        }
    }

    fun release() {
        soundPool?.release(); soundPool = null
        bgmPlayer?.release(); bgmPlayer = null
        loadedSounds.clear()
    }

    // ─── 场景切换自动换BGM ──────────────────────────
    fun playBgmForRoute(context: Context, route: String) {
        val bgm = when {
            route.startsWith("case_play") -> Bgm.CASE_EERIE
            route == "cases"              -> Bgm.CASE_NORMAL
            route == "ghost_hunt"         -> Bgm.GHOST_HUNT
            route == "cultivation"        -> Bgm.CULTIVATION
            route == "ar_ghost"           -> Bgm.AR_AMBIENT
            else                          -> Bgm.MAIN_MENU
        }
        playBgm(context, bgm)
    }

    // ─── 免费音源说明（放在这里方便开发者查阅）──────────
    /*
     * 推荐免费CC0音源 freesound.org：
     *
     * sfx_ghost_appear  → 搜索 "ghost whoosh" https://freesound.org/search/?q=ghost+appear
     * sfx_ghost_caught  → 搜索 "seal magic" https://freesound.org/search/?q=magic+seal
     * sfx_ghost_fled    → 搜索 "ghost scream" https://freesound.org/search/?q=ghost+flee
     * sfx_compass_spin  → 搜索 "compass spinning" https://freesound.org/search/?q=compass
     * sfx_iching_shake  → 搜索 "coins shake rattle" https://freesound.org/search/?q=coins+rattle
     * sfx_realm_up      → 搜索 "level up chime" https://freesound.org/search/?q=level+up
     * sfx_bell          → 搜索 "chinese bell" https://freesound.org/search/?q=chinese+bell
     * sfx_thunder       → 搜索 "thunder crack" https://freesound.org/search/?q=thunder
     * bgm_ghost_hunt    → 搜索 "chinese horror ambient" https://freesound.org/search/?q=horror+ambient
     * bgm_cultivation   → 搜索 "meditation zen ambient" https://freesound.org/search/?q=zen+meditation
     *
     * 下载后重命名放入 app/src/main/res/raw/ 即可自动加载
     * 支持格式：.ogg（推荐）、.mp3、.wav
     */
}
