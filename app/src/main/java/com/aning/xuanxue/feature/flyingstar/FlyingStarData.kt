package com.aning.xuanxue.feature.flyingstar

/**
 * 三元玄空飞星 · 静态资料库
 *
 * 说明：本模块为传统堪舆「三元玄空飞星」之理气推演资料，供专业研习与文化参考。
 * 断语多引自《紫白诀》《玄空秘旨》《玄机赋》及通行函授教材，各派略有出入，仅作参考。
 */

/** 九宫之一（按 3×3 宫格排布，上离南、下坎北、左震东、右兑西） */
data class Palace(
    val grid: Int,      // 0..8 行优先（row-major）
    val gua: String,    // 卦名
    val dir: String,    // 方位
    val luoshu: Int     // 洛书数（固定地盘数）
)

/**
 * 九宫排布（玄空盘惯例：上南下北、左东右西）
 *   巽4  离9  坤2
 *   震3  中5  兑7
 *   艮8  坎1  乾6
 */
val PALACES = listOf(
    Palace(0, "巽", "东南", 4),
    Palace(1, "离", "正南", 9),
    Palace(2, "坤", "西南", 2),
    Palace(3, "震", "正东", 3),
    Palace(4, "中", "中宫", 5),
    Palace(5, "兑", "正西", 7),
    Palace(6, "艮", "东北", 8),
    Palace(7, "坎", "正北", 1),
    Palace(8, "乾", "西北", 6)
)

/** 洛书数 -> 宫格 index 快查 */
val LUOSHU_TO_GRID: Map<Int, Int> = PALACES.associate { it.luoshu to it.grid }
/** 卦名 -> 宫格 index 快查 */
val GUA_TO_GRID: Map<String, Int> = PALACES.associate { it.gua to it.grid }

/** 元龙：0=地元龙 1=天元龙 2=人元龙 */
object YuanLong { const val DI = 0; const val TIAN = 1; const val REN = 2 }

/**
 * 二十四山 · 玄空属性
 * yang=true 阳（顺飞）  yang=false 阴（逆飞）
 * 规则：天元 子午卯酉阴、乾坤艮巽阳；人元 乙辛丁癸阴、寅申巳亥阳；地元 辰戌丑未阴、甲庚壬丙阳。
 */
data class XkMountain(
    val name: String,
    val centerDeg: Int,   // 山中心角度（正北 0°）
    val gua: String,      // 所属卦宫
    val yuanLong: Int,    // 元龙
    val yang: Boolean     // 玄空阴阳
)

/** 从「子」起，顺时针每 15°，与罗盘 MOUNTAINS_24 对应 */
val XK_MOUNTAINS = listOf(
    XkMountain("子", 0, "坎", YuanLong.TIAN, false),
    XkMountain("癸", 15, "坎", YuanLong.REN, false),
    XkMountain("丑", 30, "艮", YuanLong.DI, false),
    XkMountain("艮", 45, "艮", YuanLong.TIAN, true),
    XkMountain("寅", 60, "艮", YuanLong.REN, true),
    XkMountain("甲", 75, "震", YuanLong.DI, true),
    XkMountain("卯", 90, "震", YuanLong.TIAN, false),
    XkMountain("乙", 105, "震", YuanLong.REN, false),
    XkMountain("辰", 120, "巽", YuanLong.DI, false),
    XkMountain("巽", 135, "巽", YuanLong.TIAN, true),
    XkMountain("巳", 150, "巽", YuanLong.REN, true),
    XkMountain("丙", 165, "离", YuanLong.DI, true),
    XkMountain("午", 180, "离", YuanLong.TIAN, false),
    XkMountain("丁", 195, "离", YuanLong.REN, false),
    XkMountain("未", 210, "坤", YuanLong.DI, false),
    XkMountain("坤", 225, "坤", YuanLong.TIAN, true),
    XkMountain("申", 240, "坤", YuanLong.REN, true),
    XkMountain("庚", 255, "兑", YuanLong.DI, true),
    XkMountain("酉", 270, "兑", YuanLong.TIAN, false),
    XkMountain("辛", 285, "兑", YuanLong.REN, false),
    XkMountain("戌", 300, "乾", YuanLong.DI, false),
    XkMountain("乾", 315, "乾", YuanLong.TIAN, true),
    XkMountain("亥", 330, "乾", YuanLong.REN, true),
    XkMountain("壬", 345, "坎", YuanLong.DI, true)
)

val MOUNTAIN_BY_NAME: Map<String, XkMountain> = XK_MOUNTAINS.associateBy { it.name }

/** 洛书数 -> 后天卦（5 无卦） */
val NUM_TO_GUA: Map<Int, String> = mapOf(
    1 to "坎", 2 to "坤", 3 to "震", 4 to "巽",
    6 to "乾", 7 to "兑", 8 to "艮", 9 to "离"
)

/** 某卦 + 元龙 -> 对应之山（用于山向星定顺逆） */
fun mountainOf(gua: String, yuanLong: Int): XkMountain? =
    XK_MOUNTAINS.firstOrNull { it.gua == gua && it.yuanLong == yuanLong }

/** 三元九运（公元起讫年，下元九运 2024–2043 当令） */
data class YunInfo(val num: Int, val yuan: String, val starName: String, val startYear: Int, val endYear: Int)

val YUN_LIST = listOf(
    YunInfo(1, "上元", "一白坎", 1864, 1883),
    YunInfo(2, "上元", "二黑坤", 1884, 1903),
    YunInfo(3, "上元", "三碧震", 1904, 1923),
    YunInfo(4, "中元", "四绿巽", 1924, 1943),
    YunInfo(5, "中元", "五黄中", 1944, 1963),
    YunInfo(6, "中元", "六白乾", 1964, 1983),
    YunInfo(7, "下元", "七赤兑", 1984, 2003),
    YunInfo(8, "下元", "八白艮", 2004, 2023),
    YunInfo(9, "下元", "九紫离", 2024, 2043)
)

/** 由公元年份取所属元运（落在 1864–2043 外则就近取端点） */
fun yunOfYear(year: Int): Int =
    YUN_LIST.firstOrNull { year in it.startYear..it.endYear }?.num
        ?: if (year < 1864) 1 else 9

/** 九星 · 名号 / 五行 / 卦 / 当令吉象 / 失令凶象 */
data class StarInfo(
    val num: Int,
    val name: String,
    val element: String,
    val gua: String,
    val wang: String,   // 当令（得令）
    val shuai: String   // 失令（失运）
)

val STARS = mapOf(
    1 to StarInfo(1, "一白贪狼", "水", "坎", "官贵文秀、添丁旺财、智慧", "漂泊刑耗、肾耳之疾、桃花"),
    2 to StarInfo(2, "二黑巨门", "土", "坤", "得令为天医、田产兴旺、医药发家", "病符寡宿、腹疾肠胃、孕产不利"),
    3 to StarInfo(3, "三碧禄存", "木", "震", "兴家创业、威权、功名", "蚩尤好斗、官非盗劫、足肝之疾"),
    4 to StarInfo(4, "四绿文曲", "木", "巽", "文昌科甲、才艺、利读书", "淫荡漂荡、官讼、自缢之厄"),
    5 to StarInfo(5, "五黄廉贞", "土", "中", "（中宫尊星，得令亦须慎用）", "五黄煞、至毒、大病恶疾、灾祸，宜静忌动"),
    6 to StarInfo(6, "六白武曲", "金", "乾", "权威武贵、横财、驿马", "刑伤孤克、头疾、官非"),
    7 to StarInfo(7, "七赤破军", "金", "兑", "武贵进财、口才、偏财", "盗贼口舌、刀兵、桃花损财"),
    8 to StarInfo(8, "八白左辅", "土", "艮", "当运财星、富贵功名、旺丁旺财", "小口损伤、关节脾胃、田宅破"),
    9 to StarInfo(9, "九紫右弼", "火", "离", "文章科第、婚喜吉庆、催贵", "回禄火灾、血光目疾、心病")
)

/** 当令星（即当前元运之运星，得令为旺） */
fun isWang(star: Int, yun: Int): Boolean = star == yun
/** 生气（未来一运）/ 退气（已过一运）粗判 */
fun starPhase(star: Int, yun: Int): String = when {
    star == yun -> "旺（当令）"
    star == (yun % 9) + 1 -> "生气（次旺）"
    star == (if (yun == 1) 9 else yun - 1) -> "退气"
    else -> "衰死"
}

/** 二星组合断语（键 = 小数*10+大数；同一组合不分山向主客，作通则参考） */
data class ComboText(val text: String, val luck: Int) // luck: 1 吉, 0 平, -1 凶

val COMBO_2STAR: Map<Int, ComboText> = mapOf(
    11 to ComboText("坎水重重，主聪明文秀，亦防漂泊、桃花、肾耳之疾。", 0),
    12 to ComboText("土克水，《玄机赋》『水土同宫，腹多痞块』，主肠胃腹疾、孕产不利。", -1),
    13 to ComboText("水木相生而暗藏斗争，主是非口舌、官非。", 0),
    14 to ComboText("一四同宫，《紫白诀》『准发科名之显』，文昌位，利读书功名。", 1),
    15 to ComboText("水逢廉贞，主肾耳之疾、中毒、暗病，五黄到处宜静。", -1),
    16 to ComboText("一六共宗，金水相生，文魁官贵、聪明显达。", 1),
    17 to ComboText("金水相涵，主才情，然亦主酒色桃花、贪花恋酒。", 0),
    18 to ComboText("土克水克小口，然八为当运财星，得令仍可旺财，须看元运。", 0),
    19 to ComboText("水火交战，主心目之疾；得令则既济，聪明而显。", 0),
    22 to ComboText("二黑重叠，病符叠加，主疾病缠绵、寡妇当家。", -1),
    23 to ComboText("斗牛煞，《玄空秘旨》『斗牛煞起，惹官刑』，主斗争是非、官非。", -1),
    24 to ComboText("风行地上，主婆媳不和、妇人当家、肠胃风疾。", -1),
    25 to ComboText("二五交加，『必损主』，主大病、死亡，玄空第一凶，宜化忌动。", -1),
    26 to ComboText("坤乾土金相生，得令旺财（坚金遇土，富比陶朱）；老父老母会，亦主神鬼之事。", 1),
    27 to ComboText("二七合先天火，主火灾、心血之症、不正桃花。", -1),
    28 to ComboText("艮坤土土比和，八为财星，得令旺田产财禄。", 1),
    29 to ComboText("火土相生而生病符，主愚钝、目疾、妇人血症。", -1),
    33 to ComboText("震木重叠，主官非词讼、斗争、足疾肝病。", -1),
    34 to ComboText("木木比和，文采之中藏淫荡漂荡之象。", 0),
    35 to ComboText("木土相战逢廉贞，主脾胃肝胆之疾、破财。", -1),
    36 to ComboText("金克木，主伤足、肝病、官非刑伤。", -1),
    37 to ComboText("三七叠临，『遭劫盗，定见官司』，穿心煞，主破财盗劫。", -1),
    38 to ComboText("土木相克，损伤小口，不利幼儿。", -1),
    39 to ComboText("木火通明，主聪明文采、科甲；失令则官非火劫。", 1),
    44 to ComboText("巽木重叠，文昌之象，然亦主桃花、自缢之凶。", 0),
    45 to ComboText("木土逢廉贞，主疯疾、脾胃之病、阴邪。", -1),
    46 to ComboText("金克木，长房长妇受灾，主肝胆筋骨之疾。", -1),
    47 to ComboText("金克木，文昌受制，不利学业，主肝肺之疾、官讼。", -1),
    48 to ComboText("土木相克，不利小口、儿童，关节脾胃。", -1),
    49 to ComboText("木火通明，文章显达、喜庆；当运主科名婚喜。", 1),
    55 to ComboText("五黄重叠，至凶之地，主大病大灾，宜静不宜动，忌修造。", -1),
    56 to ComboText("土金相生，乾武当权，主武贵、横财、旺丁。", 1),
    57 to ComboText("七为先天火，火金相战，主肺疾、口舌、火厄。", -1),
    58 to ComboText("土土比和带五黄，财星受夹，旺中藏损丁之患。", 0),
    59 to ComboText("火土相生而旺五黄，毒气愈炽，主恶疾、血症。", -1),
    66 to ComboText("乾金重叠，刚过则折，主头疾、官非、孤克。", -1),
    67 to ComboText("交剑煞，金气过盛，主斗争、官非、刀伤血光。", -1),
    68 to ComboText("土金相生，富贵双全，财丁两旺（得令大吉）。", 1),
    69 to ComboText("火克金，主头痛肺疾、官非；失令防火灾。", -1),
    77 to ComboText("兑金重叠，主口舌、刀伤、桃花、肺疾。", -1),
    78 to ComboText("土金相生，兑口主食禄，得令旺财进口。", 1),
    79 to ComboText("七九穿途，『常逢回禄之灾』，主火灾、心血之症。", -1),
    88 to ComboText("艮土重叠，当运第一吉星，主旺财旺丁、富贵功名。", 1),
    89 to ComboText("火土相生，富贵喜庆，主旺丁财、婚喜（吉）。", 1),
    99 to ComboText("离火重叠，得令主文明喜庆；失令主火灾、目疾、血症。", 0)
)

/** 取二星组合断语（无序键） */
fun comboOf(a: Int, b: Int): ComboText? {
    if (a == 5 || b == 5) {
        // 含五黄者优先给出五黄提示
        COMBO_2STAR[minOf(a, b) * 10 + maxOf(a, b)]?.let { return it }
    }
    return COMBO_2STAR[minOf(a, b) * 10 + maxOf(a, b)]
}

/** 山向双星配六十四卦（键 = 山星*10+向星；含 5 者无卦） */
val SIXTY_FOUR: Map<Int, String> = mapOf(
    11 to "坎", 12 to "师", 13 to "解", 14 to "涣", 16 to "讼", 17 to "困", 18 to "蒙", 19 to "未济",
    21 to "比", 22 to "坤", 23 to "豫", 24 to "观", 26 to "否", 27 to "萃", 28 to "剥", 29 to "晋",
    31 to "屯", 32 to "复", 33 to "震", 34 to "益", 36 to "无妄", 37 to "随", 38 to "颐", 39 to "噬嗑",
    41 to "井", 42 to "升", 43 to "恒", 44 to "巽", 46 to "姤", 47 to "大过", 48 to "蛊", 49 to "鼎",
    61 to "需", 62 to "泰", 63 to "大壮", 64 to "小畜", 66 to "乾", 67 to "夬", 68 to "大畜", 69 to "大有",
    71 to "节", 72 to "临", 73 to "归妹", 74 to "中孚", 76 to "履", 77 to "兑", 78 to "损", 79 to "睽",
    81 to "蹇", 82 to "谦", 83 to "小过", 84 to "渐", 86 to "遁", 87 to "咸", 88 to "艮", 89 to "旅",
    91 to "既济", 92 to "明夷", 93 to "丰", 94 to "家人", 96 to "同人", 97 to "革", 98 to "贲", 99 to "离"
)

fun guaOf(shan: Int, xiang: Int): String? = SIXTY_FOUR[shan * 10 + xiang]

/**
 * 替星诀（起星）· 通行《青囊》挨星诀
 *   子癸甲申 → 贪狼一（1）
 *   壬卯乙未坤 → 巨门二（2）
 *   乾亥辰巽巳戌 → 武曲六（6）
 *   酉辛丑艮丙 → 破军七（7）
 *   寅午庚丁 → 右弼九（9，亦有用本运星者）
 * 注：替星口诀各派（无常、章氏等）有异，此为参考，仅供兼向起星推演。
 */
val TI_STAR: Map<String, Int> = run {
    val m = mutableMapOf<String, Int>()
    listOf("子", "癸", "甲", "申").forEach { m[it] = 1 }
    listOf("壬", "卯", "乙", "未", "坤").forEach { m[it] = 2 }
    listOf("乾", "亥", "辰", "巽", "巳", "戌").forEach { m[it] = 6 }
    listOf("酉", "辛", "丑", "艮", "丙").forEach { m[it] = 7 }
    listOf("寅", "午", "庚", "丁").forEach { m[it] = 9 }
    m
}
