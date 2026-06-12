package com.aning.xuanxue.core.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aning.xuanxue.feature.cultivation.*
import kotlinx.coroutines.flow.*

val Context.playerDataStore: DataStore<Preferences> by preferencesDataStore("player_save")

object PlayerPrefsKeys {
    val TOTAL_XP            = longPreferencesKey("total_xp")
    val GHOST_ESSENCE       = intPreferencesKey("ghost_essence")
    val LINGQI              = intPreferencesKey("lingqi")
    val REPUTATION          = intPreferencesKey("reputation")
    val CASES_COMPLETED     = intPreferencesKey("cases_completed")
    val GHOSTS_CAUGHT       = intPreferencesKey("ghosts_caught")
    val EPIC_RESONANCE      = intPreferencesKey("epic_resonance")
    val TOOLS_USED          = stringPreferencesKey("tools_used")
    val UNLOCKED_LORE       = stringPreferencesKey("unlocked_lore")
    val CAUGHT_GHOST_IDS    = stringPreferencesKey("caught_ghost_ids")
    val COMPLETED_CASE_IDS  = stringPreferencesKey("completed_case_ids")
    val UNLOCKED_ACHIEVEMENTS = stringPreferencesKey("achievements")
    val FIVE_METAL          = intPreferencesKey("five_metal")
    val FIVE_WOOD           = intPreferencesKey("five_wood")
    val FIVE_WATER          = intPreferencesKey("five_water")
    val FIVE_FIRE           = intPreferencesKey("five_fire")
    val FIVE_EARTH          = intPreferencesKey("five_earth")
    val LAST_DAILY_DATE     = stringPreferencesKey("last_daily_date")
    val DAILY_TASKS_DONE    = stringPreferencesKey("daily_tasks_done")
    val ONBOARDING_DONE     = booleanPreferencesKey("onboarding_done")
    val TIAN_SHI            = intPreferencesKey("tian_shi")
    val DI_LI               = intPreferencesKey("di_li")
    val REN_HE              = intPreferencesKey("ren_he")
}

class PlayerDataStore(private val context: Context) {

    val playerSaveFlow: Flow<PlayerSave> = context.playerDataStore.data.map { prefs ->
        PlayerSave(
            totalXp            = prefs[PlayerPrefsKeys.TOTAL_XP] ?: 0L,
            ghostEssence       = prefs[PlayerPrefsKeys.GHOST_ESSENCE] ?: 0,
            lingqi             = prefs[PlayerPrefsKeys.LINGQI] ?: 0,
            reputation         = prefs[PlayerPrefsKeys.REPUTATION] ?: 0,
            casesCompleted     = prefs[PlayerPrefsKeys.CASES_COMPLETED] ?: 0,
            ghostsCaught       = prefs[PlayerPrefsKeys.GHOSTS_CAUGHT] ?: 0,
            epicResonanceCount = prefs[PlayerPrefsKeys.EPIC_RESONANCE] ?: 0,
            toolsUsedSet       = (prefs[PlayerPrefsKeys.TOOLS_USED] ?: "").split(",").filter { it.isNotBlank() }.toSet(),
            unlockedLore       = (prefs[PlayerPrefsKeys.UNLOCKED_LORE] ?: "").split(",").filter { it.isNotBlank() }.toSet(),
            caughtGhostIds     = (prefs[PlayerPrefsKeys.CAUGHT_GHOST_IDS] ?: "").split(",").filter { it.isNotBlank() }.toSet(),
            completedCaseIds   = (prefs[PlayerPrefsKeys.COMPLETED_CASE_IDS] ?: "").split(",").filter { it.isNotBlank() }.toSet(),
            unlockedAchievementIds = (prefs[PlayerPrefsKeys.UNLOCKED_ACHIEVEMENTS] ?: "").split(",").filter { it.isNotBlank() }.toSet(),
            fiveElements       = FiveElementStats(
                metal = prefs[PlayerPrefsKeys.FIVE_METAL] ?: 10,
                wood  = prefs[PlayerPrefsKeys.FIVE_WOOD] ?: 10,
                water = prefs[PlayerPrefsKeys.FIVE_WATER] ?: 10,
                fire  = prefs[PlayerPrefsKeys.FIVE_FIRE] ?: 10,
                earth = prefs[PlayerPrefsKeys.FIVE_EARTH] ?: 10
            )
        )
    }.catch { emit(PlayerSave()) }

    val onboardingDoneFlow: Flow<Boolean> = context.playerDataStore.data
        .map { it[PlayerPrefsKeys.ONBOARDING_DONE] ?: false }

    val lastDailyDateFlow: Flow<String> = context.playerDataStore.data
        .map { it[PlayerPrefsKeys.LAST_DAILY_DATE] ?: "" }

    val tianShiFlow: Flow<Int> = context.playerDataStore.data.map { it[PlayerPrefsKeys.TIAN_SHI] ?: 50 }
    val diLiFlow: Flow<Int> = context.playerDataStore.data.map { it[PlayerPrefsKeys.DI_LI] ?: 50 }
    val renHeFlow: Flow<Int> = context.playerDataStore.data.map { it[PlayerPrefsKeys.REN_HE] ?: 50 }

    suspend fun addXpEvent(event: XpEvent, save: PlayerSave): PlayerSave {
        val updated = save.copy(
            totalXp      = save.totalXp + event.xp,
            ghostEssence = save.ghostEssence + event.essenceReward,
            lingqi       = save.lingqi + event.lingqiReward
        )
        persistSave(updated)
        return updated
    }

    suspend fun catchGhost(ghostId: String, essence: Int, xp: Long, save: PlayerSave): PlayerSave {
        val updated = save.copy(
            ghostsCaught   = save.ghostsCaught + 1,
            ghostEssence   = save.ghostEssence + essence,
            totalXp        = save.totalXp + xp,
            caughtGhostIds = save.caughtGhostIds + ghostId,
            fiveElements   = save.fiveElements.copy(metal = (save.fiveElements.metal + 1).coerceAtMost(99))
        )
        persistSave(updated)
        return updated
    }

    suspend fun completeCase(caseId: String, xp: Long, reputation: Int, lingqi: Int, save: PlayerSave): PlayerSave {
        val updated = save.copy(
            casesCompleted   = save.casesCompleted + 1,
            totalXp          = save.totalXp + xp,
            reputation       = save.reputation + reputation,
            lingqi           = save.lingqi + lingqi,
            completedCaseIds = save.completedCaseIds + caseId
        )
        persistSave(updated)
        return updated
    }

    suspend fun recordToolUse(toolId: String, save: PlayerSave): PlayerSave {
        val updated = save.copy(
            toolsUsedSet = save.toolsUsedSet + toolId,
            totalXp      = save.totalXp + XpEvent.TOOL_USE.xp
        )
        persistSave(updated)
        return updated
    }

    suspend fun setTianShi(value: Int) {
        context.playerDataStore.edit { it[PlayerPrefsKeys.TIAN_SHI] = value }
    }
    suspend fun setDiLi(value: Int) {
        context.playerDataStore.edit { it[PlayerPrefsKeys.DI_LI] = value }
    }
    suspend fun setRenHe(value: Int) {
        context.playerDataStore.edit { it[PlayerPrefsKeys.REN_HE] = value }
    }

    suspend fun setOnboardingDone() {
        context.playerDataStore.edit { it[PlayerPrefsKeys.ONBOARDING_DONE] = true }
    }

    suspend fun setDailyCheckIn(date: String, lingqiReward: Int, save: PlayerSave): PlayerSave {
        val updated = save.copy(
            lingqi = save.lingqi + lingqiReward,
            totalXp = save.totalXp + XpEvent.DAILY_ALMANAC.xp
        )
        context.playerDataStore.edit {
            it[PlayerPrefsKeys.LAST_DAILY_DATE] = date
            persistToPrefs(it, updated)
        }
        return updated
    }

    private suspend fun persistSave(save: PlayerSave) {
        context.playerDataStore.edit { persistToPrefs(it, save) }
    }

    private fun persistToPrefs(prefs: MutablePreferences, save: PlayerSave) {
        prefs[PlayerPrefsKeys.TOTAL_XP]           = save.totalXp
        prefs[PlayerPrefsKeys.GHOST_ESSENCE]       = save.ghostEssence
        prefs[PlayerPrefsKeys.LINGQI]              = save.lingqi
        prefs[PlayerPrefsKeys.REPUTATION]          = save.reputation
        prefs[PlayerPrefsKeys.CASES_COMPLETED]     = save.casesCompleted
        prefs[PlayerPrefsKeys.GHOSTS_CAUGHT]       = save.ghostsCaught
        prefs[PlayerPrefsKeys.EPIC_RESONANCE]      = save.epicResonanceCount
        prefs[PlayerPrefsKeys.TOOLS_USED]          = save.toolsUsedSet.joinToString(",")
        prefs[PlayerPrefsKeys.UNLOCKED_LORE]       = save.unlockedLore.joinToString(",")
        prefs[PlayerPrefsKeys.CAUGHT_GHOST_IDS]    = save.caughtGhostIds.joinToString(",")
        prefs[PlayerPrefsKeys.COMPLETED_CASE_IDS]  = save.completedCaseIds.joinToString(",")
        prefs[PlayerPrefsKeys.UNLOCKED_ACHIEVEMENTS] = save.unlockedAchievementIds.joinToString(",")
        prefs[PlayerPrefsKeys.FIVE_METAL]          = save.fiveElements.metal
        prefs[PlayerPrefsKeys.FIVE_WOOD]           = save.fiveElements.wood
        prefs[PlayerPrefsKeys.FIVE_WATER]          = save.fiveElements.water
        prefs[PlayerPrefsKeys.FIVE_FIRE]           = save.fiveElements.fire
        prefs[PlayerPrefsKeys.FIVE_EARTH]          = save.fiveElements.earth
    }
}
