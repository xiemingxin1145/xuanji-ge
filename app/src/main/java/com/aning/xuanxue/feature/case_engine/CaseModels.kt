package com.aning.xuanxue.feature.case_engine

/**
 * 玄机阁·阴阳录 — 案件叙事引擎
 * 数据模型层
 */

enum class Atmosphere {
    MYSTERIOUS,   // 幽暗神秘
    TENSE,        // 剑拔弩张
    EERIE,        // 阴森诡异
    CALM,         // 平静日常
    TRIUMPHANT    // 破案凯旋
}

enum class ToolAction(val route: String, val label: String) {
    ICHING("iching", "起卦推演"),
    COMPASS("compass", "罗盘定位"),
    ALMANAC("almanac", "查看天时"),
    BAZI("bazi", "排命格"),
    RESONANCE("xuanji_resonance_demo", "玄机共鸣")
}

data class CaseChoice(
    val id: String,
    val label: String,
    val desc: String,
    val nextSceneId: String,
    val minResonance: Int = 0,
    val toolRequired: ToolAction? = null,
    val rewardXuanqi: Int = 0
)

data class CaseScene(
    val id: String,
    val atmosphere: Atmosphere,
    val narrative: String,
    val speakerName: String? = null,
    val dialogue: String? = null,
    val bgHint: String = "",          // 背景氛围关键词（用于动态粒子颜色）
    val toolPrompt: String? = null,   // 提示玩家使用工具
    val toolRequired: ToolAction? = null,
    val choices: List<CaseChoice>,
    val isEnding: Boolean = false,
    val endingType: EndingType? = null
)

enum class EndingType {
    GOOD,    // 圆满结案
    BAD,     // 惨败
    HIDDEN   // 隐藏结局
}

data class CaseMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val difficulty: Int,           // 1-5
    val clientName: String,
    val clientDesc: String,
    val rewardDesc: String,
    val requiredReputation: Int = 0,
    val tags: List<String> = emptyList()
)

data class CaseFull(
    val meta: CaseMeta,
    val scenes: List<CaseScene>
) {
    fun sceneById(id: String) = scenes.first { it.id == id }
    val startScene get() = scenes.first()
}

// 玩家进行中的案件状态
data class CaseProgress(
    val caseId: String,
    val currentSceneId: String,
    val tianShi: Int = 50,    // 由黄历获取
    val diLi: Int = 50,       // 由罗盘获取
    val renHe: Int = 50,      // 由客户八字获取
    val collectedClues: List<String> = emptyList(),
    val xuanqiEarned: Int = 0
)
