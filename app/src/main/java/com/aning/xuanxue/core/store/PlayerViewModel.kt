package com.aning.xuanxue.core.store

import android.app.Application
import androidx.lifecycle.*
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.feature.cultivation.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 全局玩家状态ViewModel，在所有Screen间共享
 * 使用Activity级别scope，整个会话存活
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val store = PlayerDataStore(application)

    // ─── 核心存档流 ─────────────────────────────────
    val playerSave: StateFlow<PlayerSave> = store.playerSaveFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerSave())

    val onboardingDone: StateFlow<Boolean> = store.onboardingDoneFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ─── 三才数值（工具用完后写入，全局共享）─────────────
    val tianShi: StateFlow<Int> = store.tianShiFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)
    val diLi: StateFlow<Int> = store.diLiFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)
    val renHe: StateFlow<Int> = store.renHeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)

    // ─── 当前玄机共鸣（由三才实时计算）───────────────
    val resonance: StateFlow<XuanjiResonance.Result> = combine(tianShi, diLi, renHe) { t, d, r ->
        XuanjiResonance.calculate(t, d, r)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, XuanjiResonance.calculate(50, 50, 50))

    // ─── 今日是否已签到 ──────────────────────────────
    val todayCheckedIn: StateFlow<Boolean> = store.lastDailyDateFlow.map { last ->
        last == todayStr()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ─── 用工具后更新三才 ────────────────────────────
    fun onAlmanacUsed(tianShiScore: Int) = viewModelScope.launch {
        store.setTianShi(tianShiScore)
        store.recordToolUse("almanac", playerSave.value)
    }
    fun onCompassUsed(diLiScore: Int) = viewModelScope.launch {
        store.setDiLi(diLiScore)
        store.recordToolUse("compass", playerSave.value)
    }
    fun onIChingUsed(renHeScore: Int) = viewModelScope.launch {
        store.setRenHe(renHeScore)
        store.recordToolUse("iching", playerSave.value)
    }
    fun onBaziUsed() = viewModelScope.launch {
        store.recordToolUse("bazi", playerSave.value)
    }

    // ─── 捉鬼成功 ───────────────────────────────────
    fun onGhostCaught(ghostId: String, essence: Int, xp: Long) = viewModelScope.launch {
        store.catchGhost(ghostId, essence, xp, playerSave.value)
    }

    // ─── 案件完成 ───────────────────────────────────
    fun onCaseCompleted(caseId: String, xp: Long, reputation: Int, lingqi: Int) = viewModelScope.launch {
        store.completeCase(caseId, xp, reputation, lingqi, playerSave.value)
    }

    // ─── 每日签到 ───────────────────────────────────
    fun onDailyCheckIn() = viewModelScope.launch {
        if (!todayCheckedIn.value) {
            val lingqiReward = (50..120).random()
            store.setDailyCheckIn(todayStr(), lingqiReward, playerSave.value)
        }
    }

    // ─── 完成引导 ───────────────────────────────────
    fun onOnboardingDone() = viewModelScope.launch { store.setOnboardingDone() }

    private fun todayStr() = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
}
