package com.aning.xuanxue.feature.flyingstar

import java.util.Calendar

/**
 * 玄空流年 / 流月紫白飞星
 *
 * 第一版原则：
 * - 年星：按公历年份数字根推中宫星（2024 三碧、2025 二黑、2026 一白）。
 * - 月星：按年支三合组定正月入中星，逐月逆退。
 * - 专业实务可按立春换年、节气换月再校正，本版先提供清晰可验的盘面叠加。
 */
data class TimeFlyingStarResult(
    val year: Int,
    val month: Int,
    val yearBranch: String,
    val annualCenter: Int,
    val monthCenter: Int,
    val annualPlate: IntArray,
    val monthPlate: IntArray,
    val annualNote: String,
    val monthNote: String
)

object FlyingStarTimeCalculator {
    private val branches = listOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    fun current(): TimeFlyingStarResult {
        val c = Calendar.getInstance()
        return compute(
            year = c.get(Calendar.YEAR),
            month = c.get(Calendar.MONTH) + 1
        )
    }

    fun compute(year: Int, month: Int): TimeFlyingStarResult {
        val branch = branchOfYear(year)
        val annual = annualCenter(year)
        val monthStar = monthlyCenter(branch, month)
        return TimeFlyingStarResult(
            year = year,
            month = month,
            yearBranch = branch,
            annualCenter = annual,
            monthCenter = monthStar,
            annualPlate = FlyingStarCalculator.flyPlate(annual, true),
            monthPlate = FlyingStarCalculator.flyPlate(monthStar, true),
            annualNote = "${year}年${branch}年，${STARS[annual]?.name ?: annual}入中。专业实务宜按立春换年校正。",
            monthNote = "${month}月，流月${STARS[monthStar]?.name ?: monthStar}入中。专业实务宜按节气换月校正。"
        )
    }

    fun annualCenter(year: Int): Int {
        val root = digitRoot(year)
        return wrapStar(11 - root)
    }

    fun branchOfYear(year: Int): String {
        val index = Math.floorMod(year - 4, 12)
        return branches[index]
    }

    fun monthlyCenter(yearBranch: String, month: Int): Int {
        val firstMonth = when (yearBranch) {
            "子", "午", "卯", "酉" -> 8
            "辰", "戌", "丑", "未" -> 5
            else -> 2 // 寅申巳亥
        }
        return wrapStar(firstMonth - (month.coerceIn(1, 12) - 1))
    }

    fun starBrief(star: Int): String {
        val info = STARS[star]
        return if (info == null) "$star" else "$star ${info.name} · ${info.element}"
    }

    fun overlayWarning(star: Int): String = when (star) {
        5 -> "五黄到宫，宜静不宜动，忌大拆大修。"
        2 -> "二黑病符到宫，宜清静整洁，少动土。"
        3 -> "三碧到宫，防口舌争执，宜稳言慎行。"
        7 -> "七赤到宫，防口舌损财与金属伤。"
        8 -> "八白到宫，仍可作财星余气参考。"
        9 -> "九紫到宫，当前下元九运当令，主喜庆显达。"
        else -> "按本宫山向星、峦头和年月叠加综合论。"
    }

    private fun digitRoot(num: Int): Int {
        var n = kotlin.math.abs(num)
        while (n >= 10) {
            n = n.toString().sumOf { it - '0' }
        }
        return n
    }

    private fun wrapStar(value: Int): Int = Math.floorMod(value - 1, 9) + 1
}
