package com.aning.xuanxue.core.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aning.xuanxue.core.store.PlayerDataStore
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.feature.cultivation.PlayerSave
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 最后一个道士 · 全局游戏状态
 * 所有模块通过此ViewModel共享天时/地利/人和，成就、存档全部打通
 */
class GameStateViewModel(app: Application) : AndroidViewModel(app) {

    private val store = PlayerDataStore(app.applicationContext)
    private val _today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    // ─── 存档流 ─────────────────────────────────────
    val playerSave: StateFlow<PlayerSave> = store.playerSaveFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerSave())

    // ─── 天时地利人和（模块间共享）───────────────────
    val resonanceTriple: StateFlow<Triple<Int,Int,Int>> = combine(
        store.tianShiFlow, store.diLiFlow, store.renHeFlow
    ) { t, d, r -> Triple(t, d, r) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Triple(50,50,50))

    val resonanceResult: StateFlow<XuanjiResonance.Result> = resonanceTriple
        .map { (t,d,r) -> XuanjiResonance.calculate(t,d,r) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, XuanjiResonance.calculate(50,50,50))

    // ─── 每日状态 ────────────────────────────────────
    val todayCheckedIn: StateFlow<Boolean> = store.lastDailyDateFlow
        .map { it == _today }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ─── 新手引导 ────────────────────────────────────
    val onboardingDone: StateFlow<Boolean> = store.onboardingDoneFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ─── 操作函数 ────────────────────────────────────

    fun onAlmanacViewed(tianShi: Int) = viewModelScope.launch {
        store.setTianShi(tianShi)
        store.recordToolUse("almanac", playerSave.value)
        checkAchievements()
    }

    fun onCompassUsed(diLi: Int) = viewModelScope.launch {
        store.setDiLi(diLi)
        store.recordToolUse("compass", playerSave.value)
        checkAchievements()
    }

    fun onIChingCast(renHe: Int) = viewModelScope.launch {
        store.setRenHe(renHe)
        store.recordToolUse("iching", playerSave.value)
        checkAchievements()
    }

    fun onBaziCalculated(renHe: Int) = viewModelScope.launch {
        store.setRenHe(renHe)
        store.recordToolUse("bazi", playerSave.value)
        checkAchievements()
    }

    fun onGhostCaught(ghostId: String, essence: Int, xp: Long) = viewModelScope.launch {
        store.catchGhost(ghostId, essence, xp, playerSave.value)
        val result = resonanceResult.value
        if (result.level.ordinal >= 3) { // EPIC or higher
            recordEpicResonance()
        }
        checkAchievements()
    }

    fun onCaseComplete(caseId: String, xp: Long, rep: Int, lingqi: Int) = viewModelScope.launch {
        store.completeCase(caseId, xp, rep, lingqi, playerSave.value)
        checkAchievements()
    }

    fun claimDailyLingqi() = viewModelScope.launch {
        if (!todayCheckedIn.value) {
            store.setDailyCheckIn(_today, (50..120).random(), playerSave.value)
        }
    }

    fun setOnboardingDone() = viewModelScope.launch {
        store.setOnboardingDone()
    }

    private suspend fun recordEpicResonance() {
        val save = playerSave.value
        val updated = save.copy(epicResonanceCount = save.epicResonanceCount + 1)
        store.catchGhost("", 0, 200L, updated) // reuses persist logic, +200 xp for epic resonance
    }

    // ─── 成就检测 ─────────────────────────────────────
    private suspend fun checkAchievements() {
        val save = playerSave.value
        val ach = save.unlockedAchievementIds
        suspend fun unlock(id: String, xp: Long) {
            if (id !in ach) {
                val updated = save.copy(
                    unlockedAchievementIds = save.unlockedAchievementIds + id,
                    totalXp = save.totalXp + xp
                )
                store.recordToolUse("__ach_$id", updated)
            }
        }
        if (save.casesCompleted >= 1)  unlock("first_case",  200L)
        if (save.ghostsCaught >= 1)    unlock("first_ghost", 300L)
        if (save.toolsUsedSet.size >= 4) unlock("tool_master", 500L)
        if (save.epicResonanceCount >= 1) unlock("resonance_epic", 1000L)
        if (save.casesCompleted >= 5)  unlock("five_cases",  1000L)
        if (save.ghostsCaught >= 10)   unlock("ten_ghosts",  1500L)
    }
}
