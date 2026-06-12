package com.aning.xuanxue.feature.compass

import java.util.Locale
import kotlin.math.sqrt

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

    // ============ 真实磁场（μT） ============

    /** 由磁力计三轴计算磁场总强度（微特斯拉 μT） */
    fun magneticMagnitude(x: Float, y: Float, z: Float): Float =
        sqrt(x * x + y * y + z * z)

    /** 格式化 μT 显示 */
    fun formatUt(ut: Float): String = String.format(Locale.US, "%.1f", ut)

    /**
     * 磁场状态判定（仅作环境参考，非吉凶或灵异判断）。
     * 地表常态磁场约 25–65 μT；明显偏离多由附近金属 / 电器 / 磁吸干扰所致。
     */
    fun magneticFieldStatus(ut: Float): String = when {
        ut < 20f -> "异常"
        ut <= 70f -> "正常"
        ut <= 130f -> "偏高"
        else -> "异常"
    }

    /** 状态对应的一句现实建议 */
    fun magneticAdvice(ut: Float): String = when (magneticFieldStatus(ut)) {
        "正常" -> "磁场平稳，传统说法中更利于定向参考。"
        "偏高" -> "磁场偏高，附近可能有金属或电器，建议远离后重测。"
        else -> "磁场读数异常，请远离金属、电器、磁吸手机壳后重新校准。"
    }

    /** 传感器校准精度转文案：高 / 中 / 低 / 未校准 */
    fun accuracyText(accuracy: Int): String = when (accuracy) {
        3 -> "高"
        2 -> "中"
        1 -> "低"
        else -> "未校准"
    }

    // ============ 罗盘 AI 解读上下文 ============

    /** 罗盘 AI 解读上下文数据结构（暂不真正调用 AI） */
    data class CompassAiPrompt(
        val degree: Int,
        val mountain: String,
        val bagua: String,
        val fiveElement: String,
        val fenjin: String,
        val magneticUt: Float,
        val accuracyText: String
    ) {
        fun toPromptText(): String = buildString {
            appendLine("【罗盘当前坐向上下文】")
            appendLine("朝向角度：$degree°")
            appendLine("坐山：$mountain 山")
            appendLine("卦位：$bagua 卦")
            appendLine("五行：$fiveElement")
            appendLine("分金：$fenjin")
            appendLine("磁场强度：${formatUt(magneticUt)} μT")
            appendLine("罗盘精度：$accuracyText")
            appendLine()
            appendLine("请以传统文化与娱乐参考的角度，简要解读此坐向的方位含义，")
            append("并给出现实层面的摆放 / 使用建议；不作吉凶绝对断言，不涉灵异、驱邪、转运。")
        }
    }

    /** 构建罗盘 AI 解读上下文 */
    fun buildCompassPrompt(
        degree: Int,
        mountain: String,
        bagua: String,
        fiveElement: String,
        fenjin: String,
        magneticUt: Float,
        accuracyText: String
    ): CompassAiPrompt = CompassAiPrompt(
        degree = degree,
        mountain = mountain,
        bagua = bagua,
        fiveElement = fiveElement,
        fenjin = fenjin,
        magneticUt = magneticUt,
        accuracyText = accuracyText
    )
}
