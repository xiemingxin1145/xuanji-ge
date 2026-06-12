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

    private val ctx = app.applicationContext

    // ─── 存档流 ─────────────────────────────────────
    val playerSave: StateFlow<PlayerSave> = PlayerDataStore.playerSaveFlow(ctx)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerSave())

    // ─── 天时地利人和（模块间共享）───────────────────
    val resonanceTriple: StateFlow<Triple<Int,Int,Int>> = PlayerDataStore.resonanceFlow(ctx)
        .stateIn(viewModelScope, SharingStarted.Eagerly, Triple(50,50,50))

    val resonanceResult: StateFlow<XuanjiResonance.Result> = resonanceTriple
        .map { (t,d,r) -> XuanjiResonance.calculate(t,d,r) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, XuanjiResonance.calculate(50,50,50))

    // ─── 每日状态 ────────────────────────────────────
    private val _todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val dailyState: StateFlow<Pair<String,Boolean>> = PlayerDataStore.dailyFlow(ctx)
        .stateIn(viewModelScope, SharingStarted.Eagerly, "" to false)
    val isDailyLingqiAvailable: Boolean
        get() = dailyState.value.first != _todayDate

    // ─── 新手引导 ────────────────────────────────────
    val onboardingDone: StateFlow<Boolean> = PlayerDataStore.onboardingDoneFlow(ctx)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ─── 操作函数 ────────────────────────────────────

    /** 黄历查询后更新天时，自动计算玄机共鸣 */
    fun onAlmanacViewed(tianShi: Int) = viewModelScope.launch {
        val (_, d, r) = resonanceTriple.value
        PlayerDataStore.updateResonanceTriple(ctx, tianShi, d, r)
        PlayerDataStore.recordToolUsed(ctx, "almanac", lingqi = 20)
        checkAchievements()
    }

    /** 罗盘使用后更新地利 */
    fun onCompassUsed(diLi: Int) = viewModelScope.launch {
        val (t, _, r) = resonanceTriple.value
        PlayerDataStore.updateResonanceTriple(ctx, t, diLi, r)
        PlayerDataStore.recordToolUsed(ctx, "compass")
        checkAchievements()
    }

    /** 易经起卦后更新人和（使用八字熵值） */
    fun onIChingCast(renHe: Int) = viewModelScope.launch {
        val (t, d, _) = resonanceTriple.value
        PlayerDataStore.updateResonanceTriple(ctx, t, d, renHe)
        PlayerDataStore.recordToolUsed(ctx, "iching")
        checkAchievements()
    }

    /** 八字排盘后更新人和 */
    fun onBaziCalculated(renHe: Int) = viewModelScope.launch {
        val (t, d, _) = resonanceTriple.value
        PlayerDataStore.updateResonanceTriple(ctx, t, d, renHe)
        PlayerDataStore.recordToolUsed(ctx, "bazi")
        checkAchievements()
    }

    /** 成功捉鬼 */
    fun onGhostCaught(ghostId: String, essence: Int, xp: Long) = viewModelScope.launch {
        PlayerDataStore.recordGhostCaught(ctx, ghostId, essence, xp)
        val result = resonanceResult.value
        if (result.level.name == "EPIC" || result.level.name == "LEGENDARY") {
            PlayerDataStore.recordEpicResonance(ctx)
        }
        checkAchievements()
    }

    /** 案件结案 */
    fun onCaseComplete(caseId: String, xp: Long, rep: Int, lingqi: Int) = viewModelScope.launch {
        PlayerDataStore.recordCaseComplete(ctx, caseId, xp, rep, lingqi)
        checkAchievements()
    }

    /** 解锁典故 */
    fun onLoreUnlocked(loreId: String) = viewModelScope.launch {
        PlayerDataStore.unlockLore(ctx, loreId)
    }

    /** 领取每日灵气 */
    fun claimDailyLingqi() = viewModelScope.launch {
        if (isDailyLingqiAvailable) {
            PlayerDataStore.claimDailyLingqi(ctx, _todayDate, 50)
        }
    }

    /** 完成新手引导 */
    fun setOnboardingDone() = viewModelScope.launch {
        PlayerDataStore.setOnboardingDone(ctx)
    }

    // ─── 成就检测 ─────────────────────────────────────
    private suspend fun checkAchievements() {
        val save = playerSave.value
        val ach = save.unlockedAchievementIds
        if ("first_case" !in ach && save.casesCompleted >= 1)
            PlayerDataStore.unlockAchievement(ctx, "first_case", 200L)
        if ("first_ghost" !in ach && save.ghostsCaught >= 1)
            PlayerDataStore.unlockAchievement(ctx, "first_ghost", 300L)
        if ("tool_master" !in ach && save.toolsUsedSet.size >= 4)
            PlayerDataStore.unlockAchievement(ctx, "tool_master", 500L)
        if ("resonance_epic" !in ach && save.epicResonanceCount >= 1)
            PlayerDataStore.unlockAchievement(ctx, "resonance_epic", 1000L)
        if ("five_cases" !in ach && save.casesCompleted >= 5)
            PlayerDataStore.unlockAchievement(ctx, "five_cases", 1000L)
        if ("ten_ghosts" !in ach && save.ghostsCaught >= 10)
            PlayerDataStore.unlockAchievement(ctx, "ten_ghosts", 1500L)
    }
}
