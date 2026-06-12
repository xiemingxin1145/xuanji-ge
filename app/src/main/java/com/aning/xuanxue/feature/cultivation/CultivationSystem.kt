package com.aning.xuanxue.feature.cultivation

import androidx.compose.ui.graphics.Color

/**
 * 最后一个道士 · 道行修炼系统
 * DataStore-backed 养成引擎
 */

// ─── 境界 ────────────────────────────────────────
enum class Realm(
    val display: String,
    val subtitle: String,
    val requiredXp: Long,
    val color: Color,
    val unlocksAbility: String
) {
    MORTAL(        "凡人",   "尚未觉醒",            0L,         Color(0xFF9E9E9E), "基础玄学工具"),
    QI_REFINING(   "练气期", "感知天地灵气",         1_000L,     Color(0xFF4FC3F7), "解锁捉鬼模式"),
    FOUNDATION(    "筑基期", "凝聚本命灵根",         5_000L,     Color(0xFF81C784), "解锁第二卷宗线"),
    CORE_FORMATION("金丹期", "金丹初成，道法初显",   15_000L,    Color(0xFFFFD700), "玄机共鸣+20%加成"),
    NASCENT_SOUL(  "元婴期", "元婴出窍，神游三界",   40_000L,    Color(0xFFFF9800), "AR探秘解锁"),
    SOUL_TRANSFORM("化神期", "化神归一，与道合真",   100_000L,   Color(0xFFE91E63), "解锁传说级鬼怪"),
    VOID_REFINING( "炼虚期", "炼虚合道，窥见天机",   250_000L,   Color(0xFF9C27B0), "隐藏案件线解锁"),
    MAHAYANA(      "大乘期", "大乘飞升，一步之遥",   500_000L,   Color(0xFFFF5722), "全属性双倍"),
    TRUE_IMMORTAL( "真仙",   "飞升证道，永驻仙籍",   Long.MAX_VALUE, Color(0xFFFFFFFF), "传说结局解锁")
}

// ─── 五行属性 ──────────────────────────────────
data class FiveElementStats(
    val metal: Int = 10,   // 金：攻击/镇压力
    val wood: Int = 10,    // 木：感知/探查力
    val water: Int = 10,   // 水：符咒威力
    val fire: Int = 10,    // 火：驱邪速度
    val earth: Int = 10    // 土：防护/化解力
) {
    val total get() = metal + wood + water + fire + earth
    fun dominant(): String = mapOf(
        "金" to metal, "木" to wood, "水" to water, "火" to fire, "土" to earth
    ).maxBy { it.value }.key
}

// ─── 成就系统 ──────────────────────────────────
data class Achievement(
    val id: String,
    val title: String,
    val desc: String,
    val xpReward: Long,
    val condition: String
)

object AchievementRegistry {
    val all = listOf(
        Achievement("first_case",    "初出茅庐", "完成第一个案件",          200L,   "cases_completed >= 1"),
        Achievement("first_ghost",   "捉鬼新丁", "第一次成功捉鬼",          300L,   "ghosts_caught >= 1"),
        Achievement("tool_master",   "玄门工具人","使用所有六种玄学工具各一次", 500L, "tools_used >= 6"),
        Achievement("resonance_epic","玄机爆发", "触发一次玄机爆发级共鸣",   1000L,  "epic_resonance >= 1"),
        Achievement("five_cases",    "名声渐起", "完成五个案件",             1000L,  "cases_completed >= 5"),
        Achievement("ten_ghosts",    "捉鬼达人", "累计捉鬼十只",             1500L,  "ghosts_caught >= 10"),
        Achievement("rare_ghost",    "稀有猎手", "捉住一只稀有鬼怪",         2000L,  "rare_caught >= 1"),
        Achievement("lore_collector","志怪爱好者","解锁十条民俗典故",         1000L,  "lore_unlocked >= 10"),
        Achievement("legendary",     "最后的道士","触发传说结局",             5000L,  "legendary_ending = true")
    )
}

// ─── 玩家存档（用于DataStore序列化）──────────────
data class PlayerSave(
    val totalXp: Long = 0L,
    val ghostEssence: Int = 0,
    val lingqi: Int = 0,          // 灵气（黄历每日获取）
    val reputation: Int = 0,
    val casesCompleted: Int = 0,
    val ghostsCaught: Int = 0,
    val epicResonanceCount: Int = 0,
    val toolsUsedSet: Set<String> = emptySet(),
    val unlockedLore: Set<String> = emptySet(),
    val caughtGhostIds: Set<String> = emptySet(),
    val completedCaseIds: Set<String> = emptySet(),
    val unlockedAchievementIds: Set<String> = emptySet(),
    val fiveElements: FiveElementStats = FiveElementStats(),
    val daoBao: List<String> = emptyList()  // 道法宝典已学技能
) {
    val realm: Realm get() = Realm.entries.lastOrNull { totalXp >= it.requiredXp } ?: Realm.MORTAL
    
    val nextRealm: Realm? get() {
        val current = realm
        val idx = Realm.entries.indexOf(current)
        return if (idx < Realm.entries.size - 1) Realm.entries[idx + 1] else null
    }
    
    val xpToNextRealm: Long get() {
        val next = nextRealm ?: return 0L
        return next.requiredXp - totalXp
    }
    
    val realmProgress: Float get() {
        val current = realm
        val next = nextRealm ?: return 1f
        val base = current.requiredXp
        val target = next.requiredXp
        return ((totalXp - base).toFloat() / (target - base).toFloat()).coerceIn(0f, 1f)
    }
}

// ─── XP来源事件 ────────────────────────────────
enum class XpEvent(val label: String, val xp: Long, val essenceReward: Int = 0, val lingqiReward: Int = 0) {
    CASE_COMPLETE_GOOD(   "圆满结案",    500L,  50,  30),
    CASE_COMPLETE_BAD(    "结案存疑",    150L,  10,  5),
    CASE_HIDDEN_ENDING(   "隐秘结局",   1500L, 200, 100),
    GHOST_COMMON(         "捉游魂",      20L,   15,  0),
    GHOST_UNCOMMON(       "捉厉鬼",      60L,   45,  0),
    GHOST_RARE(           "捉稀有鬼",   180L,  120,  0),
    GHOST_EPIC(           "捉妖魂",     700L,  500,  0),
    GHOST_LEGENDARY(      "镇古神",    3000L, 2000,  0),
    TOOL_USE(             "使用玄学工具",  5L,   0,   2),
    DAILY_ALMANAC(        "查今日黄历",   10L,   0,  20),
    EPIC_RESONANCE(       "玄机爆发",   200L,   0,  50),
    LORE_UNLOCK(          "解锁典故",    30L,   0,   0)
}
