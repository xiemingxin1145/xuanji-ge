package com.aning.xuanxue.feature.mountain

import com.aning.xuanxue.feature.flyingstar.XK_MOUNTAINS

/**
 * 二十四山向断语库 · 第一版
 *
 * 给专业风水用户看的索引型资料：列山、向、度数、卦宫、元龙、玄空阴阳、断法提示。
 * 不替使用者下绝对结论，重点把盘面规则和可检查项摆出来。
 */
data class MountainOracle(
    val mountain: String,
    val facing: String,
    val range: String,
    val palace: String,
    val yuanLong: String,
    val yinYang: String,
    val elementHint: String,
    val usage: String,
    val cautions: List<String>,
    val flyingStarHint: String
)

object MountainOracleRepository {
    val all: List<MountainOracle> = XK_MOUNTAINS.mapIndexed { index, mountain ->
        val facing = XK_MOUNTAINS[(index + 12) % 24]
        val start = (mountain.centerDeg - 7.5 + 360) % 360
        val end = (mountain.centerDeg + 7.5) % 360
        MountainOracle(
            mountain = mountain.name,
            facing = facing.name,
            range = formatRange(start, end),
            palace = mountain.gua,
            yuanLong = yuanLongName(mountain.yuanLong),
            yinYang = if (mountain.yang) "阳 · 顺飞" else "阴 · 逆飞",
            elementHint = palaceElement(mountain.gua),
            usage = usageOf(mountain.name, facing.name, mountain.gua),
            cautions = cautionsOf(mountain.name, facing.name, mountain.gua),
            flyingStarHint = "玄空排盘取${mountain.name}山${facing.name}向；山盘顺逆以${mountain.name}山元龙阴阳推，向盘以${facing.name}向元龙阴阳推。"
        )
    }

    fun find(mountain: String): MountainOracle? = all.firstOrNull { it.mountain == mountain }

    fun search(keyword: String): List<MountainOracle> {
        val q = keyword.trim()
        if (q.isEmpty()) return all
        return all.filter {
            it.mountain.contains(q) || it.facing.contains(q) || it.palace.contains(q) ||
                it.yuanLong.contains(q) || it.elementHint.contains(q) || it.usage.contains(q) ||
                it.cautions.any { c -> c.contains(q) }
        }
    }

    private fun formatRange(start: Double, end: Double): String =
        if (start > end) "${one(start)}°–360° / 0°–${one(end)}°" else "${one(start)}°–${one(end)}°"

    private fun one(v: Double): String = String.format("%.1f", v)

    private fun yuanLongName(value: Int): String = when (value) {
        0 -> "地元龙"
        1 -> "天元龙"
        else -> "人元龙"
    }

    private fun palaceElement(gua: String): String = when (gua) {
        "坎" -> "坎宫水"
        "坤" -> "坤宫土"
        "震" -> "震宫木"
        "巽" -> "巽宫木"
        "乾" -> "乾宫金"
        "兑" -> "兑宫金"
        "艮" -> "艮宫土"
        "离" -> "离宫火"
        else -> "中宫土"
    }

    private fun usageOf(mountain: String, facing: String, gua: String): String = when (gua) {
        "坎" -> "${mountain}山${facing}向属坎宫，多取水气、北方、流动与藏蓄之象。实务须看向首开阔、来水去水、门路动线。"
        "离" -> "${mountain}山${facing}向属离宫，多取明堂、光照、文名与火象。实务宜察采光、电火、尖角与高亢形势。"
        "震" -> "${mountain}山${facing}向属震宫，多取动象、生发、长男与东方木气。实务宜察道路冲动、树木、声响与动线。"
        "巽" -> "${mountain}山${facing}向属巽宫，多取风入、文昌、流通与细微之气。实务宜察风口、走廊、窗户与气流。"
        "乾" -> "${mountain}山${facing}向属乾宫，多取权威、父位、首脑与西北金气。实务宜察高低、靠山、金属形煞与道路。"
        "兑" -> "${mountain}山${facing}向属兑宫，多取口舌、少女、悦象与西方金气。实务宜察开口、缺角、金属锐器与声煞。"
        "艮" -> "${mountain}山${facing}向属艮宫，多取止定、少男、山势与东北土气。实务宜察靠山、土石、门槛与阻滞。"
        "坤" -> "${mountain}山${facing}向属坤宫，多取承载、母位、田宅与西南土气。实务宜察厨房、庭院、低湿与杂物堆积。"
        else -> "${mountain}山${facing}向须结合二十四山分金、三元元运、山向飞星与峦头形势综合判断。"
    }

    private fun cautionsOf(mountain: String, facing: String, gua: String): List<String> {
        val base = mutableListOf(
            "先校准罗盘，避开车体、电器、钢筋强磁干扰。",
            "下卦/兼向须看实测度数，近分界线不宜轻断。",
            "断语须配元运、山向双星、流年流月和峦头，不可只凭一山一向。"
        )
        when (gua) {
            "坎" -> base.add("坎宫忌污水、反弓水、路冲直射，得令见水方可论财。")
            "离" -> base.add("离宫忌火形尖射、电火杂乱，得令宜明堂开阔。")
            "震" -> base.add("震宫忌动线冲急、声煞扰动，须分旺衰动静。")
            "巽" -> base.add("巽宫忌穿堂风、斜风割脚，宜缓不宜泄。")
            "乾" -> base.add("乾宫忌低陷缺角、金形煞压迫，宜有实靠。")
            "兑" -> base.add("兑宫忌破口、尖金、声煞，易引口舌，须察门窗。")
            "艮" -> base.add("艮宫忌阻塞污秽、土石压迫，宜整洁稳定。")
            "坤" -> base.add("坤宫忌湿浊杂乱，宜平稳厚实。")
        }
        base.add("当前条目为${mountain}山${facing}向资料索引，具体吉凶交由专业使用者综合取断。")
        return base
    }
}
