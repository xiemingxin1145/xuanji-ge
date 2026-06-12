package com.aning.xuanxue.feature.ai

import java.util.Locale

data class CompassAiContext(
    val degree: Int,
    val mountain: String,
    val bagua: String,
    val fiveElement: String,
    val fenjin: String,
    val magneticUt: Float,
    val accuracyText: String
)

data class BaziAiContext(
    val yearPillar: String,
    val monthPillar: String,
    val dayPillar: String,
    val hourPillar: String,
    val dayMaster: String,
    val dayMasterElement: String,
    val fiveElementCounts: Map<String, Int>,
    val tenGods: List<String>,
    val hiddenStems: List<String>,
    val naYin: List<String>,
    val xunKong: List<String>,
    val taiYuan: String,
    val mingGong: String,
    val shenGong: String
)

data class IChingAiContext(
    val benGuaName: String,
    val benGuaBrief: String,
    val bianGuaName: String?,
    val bianGuaBrief: String?,
    val movingYaoNames: List<String>,
    val yaoLinesFromBottom: List<String>,
    val questionHint: String = "未填写具体问事，可按当下处境作泛用参考。"
)

data class AlmanacAiContext(
    val solarDate: String,
    val lunarDate: String,
    val ganZhi: String,
    val yi: List<String>,
    val ji: List<String>,
    val chongSha: String,
    val deity: String
)

data class NameAiContext(
    val surname: String,
    val candidateName: String,
    val baziSummary: String,
    val preferredElements: List<String>
)

object AiPromptBuilder {
    fun buildCompassPrompt(context: CompassAiContext): String = buildString {
        appendLine("【罗盘解读上下文】")
        appendLine("角度：${context.degree}°")
        appendLine("坐山：${context.mountain}山")
        appendLine("卦位：${context.bagua}")
        appendLine("五行：${context.fiveElement}")
        appendLine("分金：${context.fenjin}")
        appendLine("磁场强度：${String.format(Locale.US, "%.1f", context.magneticUt)} μT")
        appendLine("罗盘精度：${context.accuracyText}")
        appendLine()
        appendLine(commonRequest("请解读这个坐向，并给出空间整理建议。"))
    }

    fun buildBaziPrompt(context: BaziAiContext): String = buildString {
        appendLine("【八字排盘上下文】")
        appendLine("年柱：${context.yearPillar}")
        appendLine("月柱：${context.monthPillar}")
        appendLine("日柱：${context.dayPillar}")
        appendLine("时柱：${context.hourPillar}")
        appendLine("日主：${context.dayMaster}（${context.dayMasterElement}）")
        appendLine("五行统计：${context.fiveElementCounts.entries.joinToString("、") { "${it.key}${it.value}" }}")
        appendLine("十神：${context.tenGods.joinToString("、")}")
        appendLine("藏干：${context.hiddenStems.joinToString("、")}")
        appendLine("纳音：${context.naYin.joinToString("、")}")
        appendLine("旬空：${context.xunKong.joinToString("、")}")
        appendLine("胎元：${context.taiYuan}；命宫：${context.mingGong}；身宫：${context.shenGong}")
        appendLine()
        appendLine(commonRequest("请做一份四柱五行的简要解读。"))
    }

    fun buildIChingPrompt(context: IChingAiContext): String = buildString {
        appendLine("【易经起卦上下文】")
        appendLine("本卦：${context.benGuaName}")
        appendLine("本卦简意：${context.benGuaBrief}")
        appendLine("变卦：${context.bianGuaName ?: "无"}")
        appendLine("变卦简意：${context.bianGuaBrief ?: "无"}")
        appendLine("动爻：${context.movingYaoNames.ifEmpty { listOf("无动爻") }.joinToString("、")}")
        appendLine("六爻自下而上：${context.yaoLinesFromBottom.joinToString("、")}")
        appendLine("问事提示：${context.questionHint}")
        appendLine()
        appendLine(commonRequest("请按本卦、动爻、变卦三个层次解读。"))
    }

    fun buildAlmanacPrompt(context: AlmanacAiContext): String = buildString {
        appendLine("【黄历上下文】")
        appendLine("公历：${context.solarDate}")
        appendLine("农历：${context.lunarDate}")
        appendLine("干支：${context.ganZhi}")
        appendLine("宜：${context.yi.joinToString("、")}")
        appendLine("忌：${context.ji.joinToString("、")}")
        appendLine("冲煞：${context.chongSha}")
        appendLine("值神：${context.deity}")
        appendLine()
        appendLine(commonRequest("请生成今日提醒和一句今日印记。"))
    }

    fun buildNamePrompt(context: NameAiContext): String = buildString {
        appendLine("【姓名五行上下文】")
        appendLine("姓氏：${context.surname}")
        appendLine("候选名：${context.candidateName}")
        appendLine("八字摘要：${context.baziSummary}")
        appendLine("偏向五行：${context.preferredElements.joinToString("、")}")
        appendLine()
        appendLine(commonRequest("请给出姓名用字方向和现实起名建议。"))
    }

    private fun commonRequest(task: String): String = buildString {
        appendLine(task)
        appendLine("回答结构：1. 传统文化解释；2. 现实生活建议；3. 风险边界；4. 今日印记一句话。")
        appendLine("请避免绝对承诺，避免制造焦虑；重大决策请提醒用户理性判断。")
    }
}

object PendingAiPromptStore {
    private var pendingPrompt: String? = null
    fun set(prompt: String) { pendingPrompt = prompt }
    fun consume(): String? {
        val p = pendingPrompt
        pendingPrompt = null
        return p
    }
}
