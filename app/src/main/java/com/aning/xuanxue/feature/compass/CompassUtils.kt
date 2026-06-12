package com.aning.xuanxue.feature.compass

import kotlin.math.roundToInt

object CompassUtils {

    /** 角度转二十四山 */
    fun degreeToMountain24(deg: Float): String {
        val index = ((deg + 7.5f) / 15f).toInt() % 24
        return MOUNTAINS_24.getOrElse(index) { "子" }
    }

    /** 角度转后天八卦 */
    fun degreeToBagua(deg: Float): String {
        val normalized = ((deg % 360) + 360) % 360
        val closest = BAGUA_8.minByOrNull {
            val d = kotlin.math.abs(it.first - normalized)
            kotlin.math.min(d, 360 - d)
        }
        return closest?.second ?: "坎"
    }

    /** 角度转五行（简化） */
    fun degreeToFiveElement(deg: Float): String {
        val mountain = degreeToMountain24(deg)
        return FIVE_ELEMENTS[mountain] ?: "水"
    }

    /** 角度转分金参考（简化版） */
    fun degreeToFenjin(deg: Float): String {
        val base = degreeToMountain24(deg)
        val offset = ((deg % 15) / 5).toInt()
        return when (offset) {
            0 -> "$base 正分金"
            1 -> "$base 偏分金"
            else -> "$base 末分金"
        }
    }

    /** 模拟磁场强度等级（0-3） */
    fun magneticStrengthLevel(accuracy: Int): Int {
        return when {
            accuracy >= 3 -> 3 // 高
            accuracy >= 2 -> 2
            accuracy >= 1 -> 1
            else -> 0
        }
    }

    fun magneticLevelText(level: Int): String = when (level) {
        3 -> "磁场稳定 · 宜立向"
        2 -> "磁场一般 · 建议微调"
        1 -> "磁场波动 · 注意干扰"
        else -> "磁场异常 · 建议重测"
    }
}
