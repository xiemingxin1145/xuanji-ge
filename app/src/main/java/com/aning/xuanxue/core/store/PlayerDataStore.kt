package com.aning.xuanxue.core.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aning.xuanxue.feature.cultivation.FiveElementStats
import com.aning.xuanxue.feature.cultivation.PlayerSave
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 最后一个道士 · 全局持久化存档
 * DataStore Preferences — 关 App 后数据不丢失
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "taoist_save")

object PlayerDataStore {

    // ─── Keys ──────────────────────────────────────────
    private val TOTAL_XP          = longPreferencesKey("total_xp")
    private val GHOST_ESSENCE     = intPreferencesKey("ghost_essence")
    private val LINGQI            = intPreferencesKey("lingqi")
    private val REPUTATION        = intPreferencesKey("reputation")
    private val CASES_COMPLETED   = intPreferencesKey("cases_completed")
    private val GHOSTS_CAUGHT     = intPreferencesKey("ghosts_caught")
    private val EPIC_RESONANCE    = intPreferencesKey("epic_resonance")
    private val TOOLS_USED        = stringPreferencesKey("tools_used")       // comma-joined
    private val UNLOCKED_LORE     = stringPreferencesKey("unlocked_lore")
    private val CAUGHT_GHOST_IDS  = stringPreferencesKey("caught_ghost_ids")
    private val COMPLETED_CASES   = stringPreferencesKey("completed_cases")
    private val ACHIEVEMENTS      = stringPreferencesKey("achievements")
    private val FIVE_METAL        = intPreferencesKey("five_metal")
    private val FIVE_WOOD         = intPreferencesKey("five_wood")
    private val FIVE_WATER        = intPreferencesKey("five_water")
    private val FIVE_FIRE         = intPreferencesKey("five_fire")
    private val FIVE_EARTH        = intPreferencesKey("five_earth")
    private val DAILY_DATE        = stringPreferencesKey("daily_date")
    private val DAILY_LINGQI_CLAIMED = booleanPreferencesKey("daily_lingqi")
    private val DAILY_QUESTS_DONE = stringPreferencesKey("daily_quests_done")
    private val ONBOARDING_DONE   = booleanPreferencesKey("onboarding_done")
    private val CURRENT_TIANSHI   = intPreferencesKey("tianshi")
    private val CURRENT_DILI      = intPreferencesKey("dili")
    private val CURRENT_RENHE     = intPreferencesKey("renhe")

    // ─── Flow ──────────────────────────────────────────
    fun playerSaveFlow(context: Context): Flow<PlayerSave> =
        context.dataStore.data.map { p ->
            PlayerSave(
                totalXp         = p[TOTAL_XP] ?: 0L,
                ghostEssence    = p[GHOST_ESSENCE] ?: 0,
                lingqi          = p[LINGQI] ?: 0,
                reputation      = p[REPUTATION] ?: 0,
                casesCompleted  = p[CASES_COMPLETED] ?: 0,
                ghostsCaught    = p[GHOSTS_CAUGHT] ?: 0,
                epicResonanceCount = p[EPIC_RESONANCE] ?: 0,
                toolsUsedSet    = p[TOOLS_USED]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
                unlockedLore    = p[UNLOCKED_LORE]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
                caughtGhostIds  = p[CAUGHT_GHOST_IDS]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
                completedCaseIds= p[COMPLETED_CASES]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
                unlockedAchievementIds = p[ACHIEVEMENTS]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
                fiveElements    = FiveElementStats(
                    metal = p[FIVE_METAL] ?: 10,
                    wood  = p[FIVE_WOOD]  ?: 10,
                    water = p[FIVE_WATER] ?: 10,
                    fire  = p[FIVE_FIRE]  ?: 10,
                    earth = p[FIVE_EARTH] ?: 10
                )
            )
        }

    fun resonanceFlow(context: Context): Flow<Triple<Int,Int,Int>> =
        context.dataStore.data.map { p ->
            Triple(
                p[CURRENT_TIANSHI] ?: 50,
                p[CURRENT_DILI]    ?: 50,
                p[CURRENT_RENHE]   ?: 50
            )
        }

    fun dailyFlow(context: Context): Flow<Pair<String,Boolean>> =
        context.dataStore.data.map { p ->
            Pair(p[DAILY_DATE] ?: "", p[DAILY_LINGQI_CLAIMED] ?: false)
        }

    fun onboardingDoneFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { p -> p[ONBOARDING_DONE] ?: false }

    // ─── Writers ───────────────────────────────────────
    suspend fun addXp(context: Context, xp: Long) =
        context.dataStore.edit { p -> p[TOTAL_XP] = (p[TOTAL_XP] ?: 0L) + xp }

    suspend fun addGhostEssence(context: Context, amount: Int) =
        context.dataStore.edit { p -> p[GHOST_ESSENCE] = (p[GHOST_ESSENCE] ?: 0) + amount }

    suspend fun addLingqi(context: Context, amount: Int) =
        context.dataStore.edit { p -> p[LINGQI] = (p[LINGQI] ?: 0) + amount }

    suspend fun addReputation(context: Context, amount: Int) =
        context.dataStore.edit { p -> p[REPUTATION] = (p[REPUTATION] ?: 0) + amount }

    suspend fun recordGhostCaught(context: Context, ghostId: String, essence: Int, xp: Long) =
        context.dataStore.edit { p ->
            p[GHOSTS_CAUGHT] = (p[GHOSTS_CAUGHT] ?: 0) + 1
            p[GHOST_ESSENCE] = (p[GHOST_ESSENCE] ?: 0) + essence
            p[TOTAL_XP]      = (p[TOTAL_XP] ?: 0L) + xp
            val current = p[CAUGHT_GHOST_IDS] ?: ""
            if (!current.contains(ghostId))
                p[CAUGHT_GHOST_IDS] = if (current.isBlank()) ghostId else "$current,$ghostId"
            // 五行成长：捉鬼提升对应属性
            p[FIVE_METAL] = ((p[FIVE_METAL] ?: 10) + 1).coerceAtMost(999)
        }

    suspend fun recordCaseComplete(context: Context, caseId: String, xp: Long, rep: Int, lingqi: Int) =
        context.dataStore.edit { p ->
            p[CASES_COMPLETED] = (p[CASES_COMPLETED] ?: 0) + 1
            p[TOTAL_XP]        = (p[TOTAL_XP] ?: 0L) + xp
            p[REPUTATION]      = (p[REPUTATION] ?: 0) + rep
            p[LINGQI]          = (p[LINGQI] ?: 0) + lingqi
            val cur = p[COMPLETED_CASES] ?: ""
            if (!cur.contains(caseId))
                p[COMPLETED_CASES] = if (cur.isBlank()) caseId else "$cur,$caseId"
        }

    suspend fun recordToolUsed(context: Context, toolId: String, lingqi: Int = 2) =
        context.dataStore.edit { p ->
            val cur = p[TOOLS_USED] ?: ""
            if (!cur.contains(toolId))
                p[TOOLS_USED] = if (cur.isBlank()) toolId else "$cur,$toolId"
            p[LINGQI]  = (p[LINGQI] ?: 0) + lingqi
            p[TOTAL_XP]= (p[TOTAL_XP] ?: 0L) + 5L
        }

    suspend fun updateResonanceTriple(context: Context, tianShi: Int, diLi: Int, renHe: Int) =
        context.dataStore.edit { p ->
            p[CURRENT_TIANSHI] = tianShi
            p[CURRENT_DILI]    = diLi
            p[CURRENT_RENHE]   = renHe
        }

    suspend fun recordEpicResonance(context: Context) =
        context.dataStore.edit { p ->
            p[EPIC_RESONANCE] = (p[EPIC_RESONANCE] ?: 0) + 1
            p[TOTAL_XP] = (p[TOTAL_XP] ?: 0L) + 200L
            p[LINGQI]   = (p[LINGQI] ?: 0) + 50
        }

    suspend fun claimDailyLingqi(context: Context, date: String, amount: Int) =
        context.dataStore.edit { p ->
            p[DAILY_DATE]          = date
            p[DAILY_LINGQI_CLAIMED]= true
            p[LINGQI]              = (p[LINGQI] ?: 0) + amount
        }

    suspend fun unlockLore(context: Context, loreId: String) =
        context.dataStore.edit { p ->
            val cur = p[UNLOCKED_LORE] ?: ""
            if (!cur.contains(loreId))
                p[UNLOCKED_LORE] = if (cur.isBlank()) loreId else "$cur,$loreId"
            p[TOTAL_XP] = (p[TOTAL_XP] ?: 0L) + 30L
        }

    suspend fun setOnboardingDone(context: Context) =
        context.dataStore.edit { p -> p[ONBOARDING_DONE] = true }

    suspend fun unlockAchievement(context: Context, achId: String, xpReward: Long) =
        context.dataStore.edit { p ->
            val cur = p[ACHIEVEMENTS] ?: ""
            if (!cur.contains(achId)) {
                p[ACHIEVEMENTS] = if (cur.isBlank()) achId else "$cur,$achId"
                p[TOTAL_XP] = (p[TOTAL_XP] ?: 0L) + xpReward
            }
        }
}
