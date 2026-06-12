package com.aning.xuanxue.feature.bazi

/** 八字专业补充：神煞、大运、流年基础资料层 */
data class ShenshaHit(
    val name: String,
    val basis: String,
    val target: String,
    val meaning: String,
    val source: String
)

data class MajorLuckPillar(
    val index: Int,
    val ageRange: String,
    val pillar: String,
    val direction: String,
    val note: String
)

data class FlowYearPillar(
    val year: Int,
    val pillar: String,
    val note: String
)

object BaziProCalculator {
    private val gan = listOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    private val zhi = listOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
    private val jiazi: List<String> = List(60) { i -> gan[i % 10] + zhi[i % 12] }

    fun isYangGan(g: Char): Boolean = g in listOf('甲', '丙', '戊', '庚', '壬')

    /** 阳男阴女顺，阴男阳女逆 */
    fun luckForward(yearPillar: String, male: Boolean): Boolean {
        val yg = yearPillar.firstOrNull()?.let { isYangGan(it) } ?: true
        return (male && yg) || (!male && !yg)
    }

    /**
     * 大运序列：以月柱为基准顺/逆推十柱。
     * 起运岁数需按节气差精算；本版先列十年干支序列，供专业用户配合真起运另校。
     */
    fun majorLuck(monthPillar: String, forward: Boolean): List<MajorLuckPillar> {
        val start = jiazi.indexOf(monthPillar).takeIf { it >= 0 } ?: 0
        return (1..10).map { step ->
            val idx = Math.floorMod(start + if (forward) step else -step, 60)
            MajorLuckPillar(
                index = step,
                ageRange = "${(step - 1) * 10 + 1}-${step * 10}岁",
                pillar = jiazi[idx],
                direction = if (forward) "顺排" else "逆排",
                note = "以月柱${monthPillar}为基准${if (forward) "顺" else "逆"}推第${step}步；起运岁数待节气精算。"
            )
        }
    }

    fun flowYears(startYear: Int, count: Int = 10): List<FlowYearPillar> =
        (0 until count).map { offset ->
            val y = startYear + offset
            FlowYearPillar(
                year = y,
                pillar = ganzhiYear(y),
                note = "${y}年流年${ganzhiYear(y)}；需与大运、原局合冲刑害并看。"
            )
        }

    fun ganzhiYear(year: Int): String = jiazi[Math.floorMod(year - 1984, 60)]

    fun shensha(
        yearPillar: String,
        monthPillar: String,
        dayPillar: String,
        hourPillar: String,
        dayXunKong: String
    ): List<ShenshaHit> {
        val branches = listOf(
            "年支" to yearPillar.getOrNull(1)?.toString().orEmpty(),
            "月支" to monthPillar.getOrNull(1)?.toString().orEmpty(),
            "日支" to dayPillar.getOrNull(1)?.toString().orEmpty(),
            "时支" to hourPillar.getOrNull(1)?.toString().orEmpty()
        )
        val dayGan = dayPillar.getOrNull(0) ?: return emptyList()
        val dayBranch = dayPillar.getOrNull(1)?.toString().orEmpty()
        val yearBranch = yearPillar.getOrNull(1)?.toString().orEmpty()
        val hits = mutableListOf<ShenshaHit>()

        fun addIfBranch(name: String, basis: String, target: String, meaning: String, source: String) {
            branches.filter { it.second == target }.forEach { pos ->
                hits.add(ShenshaHit(name, basis, "${pos.first}$target", meaning, source))
            }
        }

        fun groupTarget(branch: String, map: Map<Set<String>, String>): String? =
            map.entries.firstOrNull { branch in it.key }?.value

        // 天乙贵人：以日干取贵人
        val tianYi = when (dayGan) {
            '甲', '戊', '庚' -> listOf("丑", "未")
            '乙', '己' -> listOf("子", "申")
            '丙', '丁' -> listOf("亥", "酉")
            '壬', '癸' -> listOf("巳", "卯")
            '辛' -> listOf("午", "寅")
            else -> emptyList()
        }
        tianYi.forEach { addIfBranch("天乙贵人", "日干$dayGan", it, "主贵助、逢凶有解，仍须看旺衰喜忌。", "《三命通会》常用神煞") }

        // 文昌、禄神、羊刃：以日干取支
        val wenChang = mapOf('甲' to "巳", '乙' to "午", '丙' to "申", '丁' to "酉", '戊' to "申", '己' to "酉", '庚' to "亥", '辛' to "子", '壬' to "寅", '癸' to "卯")[dayGan]
        wenChang?.let { addIfBranch("文昌", "日干$dayGan", it, "主文章、学业、文艺、聪敏，忌刑冲破坏。", "文昌贵人法") }
        val lu = mapOf('甲' to "寅", '乙' to "卯", '丙' to "巳", '丁' to "午", '戊' to "巳", '己' to "午", '庚' to "申", '辛' to "酉", '壬' to "亥", '癸' to "子")[dayGan]
        lu?.let { addIfBranch("禄神", "日干$dayGan", it, "主俸禄根气、衣食根基，须看是否得地得用。", "禄神法") }
        val ren = mapOf('甲' to "卯", '乙' to "寅", '丙' to "午", '丁' to "巳", '戊' to "午", '己' to "巳", '庚' to "酉", '辛' to "申", '壬' to "子", '癸' to "亥")[dayGan]
        ren?.let { addIfBranch("羊刃", "日干$dayGan", it, "主刚烈、竞争、刀兵之象；喜制化，忌失控。", "羊刃法") }

        val triadMapTaohua = mapOf(setOf("寅", "午", "戌") to "卯", setOf("申", "子", "辰") to "酉", setOf("亥", "卯", "未") to "子", setOf("巳", "酉", "丑") to "午")
        val triadMapYima = mapOf(setOf("寅", "午", "戌") to "申", setOf("申", "子", "辰") to "寅", setOf("亥", "卯", "未") to "巳", setOf("巳", "酉", "丑") to "亥")
        val triadMapHuagai = mapOf(setOf("寅", "午", "戌") to "戌", setOf("申", "子", "辰") to "辰", setOf("亥", "卯", "未") to "未", setOf("巳", "酉", "丑") to "丑")
        val triadMapJiang = mapOf(setOf("寅", "午", "戌") to "午", setOf("申", "子", "辰") to "子", setOf("亥", "卯", "未") to "卯", setOf("巳", "酉", "丑") to "酉")

        listOf("年支" to yearBranch, "日支" to dayBranch).forEach { (basisName, basisBranch) ->
            groupTarget(basisBranch, triadMapTaohua)?.let { addIfBranch("桃花", "$basisName$basisBranch", it, "主交际、情感、人缘、审美，也防酒色口舌。", "三合桃花法") }
            groupTarget(basisBranch, triadMapYima)?.let { addIfBranch("驿马", "$basisName$basisBranch", it, "主动迁移、远行、奔波、职业变化。", "三合驿马法") }
            groupTarget(basisBranch, triadMapHuagai)?.let { addIfBranch("华盖", "$basisName$basisBranch", it, "主孤高、艺学、宗教玄学、清冷之象。", "三合华盖法") }
            groupTarget(basisBranch, triadMapJiang)?.let { addIfBranch("将星", "$basisName$basisBranch", it, "主权柄、担当、组织力，忌被冲破。", "将星法") }
        }

        // 旬空：以日柱旬空标注四支命中
        dayXunKong.forEach { k ->
            val target = k.toString()
            if (target.isNotBlank()) addIfBranch("旬空", "日柱$dayPillar", target, "空亡主虚、迟、缺、落空；喜忌须结合十神宫位。", "旬空法")
        }

        return hits.distinctBy { it.name + it.basis + it.target }
    }
}
