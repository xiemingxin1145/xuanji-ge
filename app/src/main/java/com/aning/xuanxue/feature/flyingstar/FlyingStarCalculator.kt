package com.aning.xuanxue.feature.flyingstar

/**
 * 三元玄空飞星 · 排盘引擎
 *
 * 流程：① 运星入中顺飞得运盘 → ② 取坐山宫、向首宫之运盘星分别入中
 *      → ③ 依坐向元龙之玄空阴阳定顺逆，分飞得山盘、向盘
 *      → ④ 判旺山旺向 / 上山下水 / 双星到向 / 双星到坐，附合十、三般卦等。
 */

/** 单宫三星 */
data class PalaceStars(
    val palace: Palace,
    val yun: Int,
    val shan: Int,
    val xiang: Int
)

data class FlyingStarResult(
    val yun: Int,
    val sitting: XkMountain,
    val facing: XkMountain,
    val yunPlate: IntArray,
    val shanPlate: IntArray,
    val xiangPlate: IntArray,
    val sittingGrid: Int,
    val facingGrid: Int,
    val shanCenter: Int,
    val xiangCenter: Int,
    val shanForward: Boolean,
    val xiangForward: Boolean,
    val pattern: String,
    val patternDesc: String,
    val patternLuck: Int,        // 1 吉 0 平 -1 凶
    val extraNotes: List<String>,
    val replaced: Boolean
) {
    /** 按宫格 0..8 取整合后的九宫三星 */
    fun palaceStars(): List<PalaceStars> = PALACES.map {
        PalaceStars(it, yunPlate[it.grid], shanPlate[it.grid], xiangPlate[it.grid])
    }
}

object FlyingStarCalculator {

    /** 飞泊：center 入中，forward=true 顺飞 / false 逆飞；返回按宫格 0..8 的星数组 */
    fun flyPlate(center: Int, forward: Boolean): IntArray {
        val plate = IntArray(9)
        for (p in PALACES) {
            val offset = ((p.luoshu - 5) + 9) % 9
            plate[p.grid] = if (forward) {
                ((center - 1 + offset) % 9) + 1
            } else {
                (((center - 1 - offset) % 9) + 9) % 9 + 1
            }
        }
        return plate
    }

    /** 向首 = 坐山对宫（+180°） */
    fun facingOf(sitting: XkMountain): XkMountain {
        val deg = (sitting.centerDeg + 180) % 360
        return XK_MOUNTAINS.first { it.centerDeg == deg }
    }

    /**
     * 定顺逆：入中之运盘星 center 对应之卦，取与坐/向同元龙之山，看其玄空阴阳。
     * center == 5 无卦，借坐/向本山之阴阳。阳顺(true)，阴逆(false)。
     */
    fun swingForward(centerYunStar: Int, origin: XkMountain): Boolean {
        if (centerYunStar == 5) return origin.yang
        val gua = NUM_TO_GUA[centerYunStar] ?: return origin.yang
        val m = mountainOf(gua, origin.yuanLong) ?: return origin.yang
        return m.yang
    }

    /**
     * 排盘主函数
     * @param yun 元运 1..9
     * @param sittingName 坐山（二十四山名）
     * @param useReplacement 是否用替（兼向起星，口诀各派有异，仅参考）
     */
    fun compute(yun: Int, sittingName: String, useReplacement: Boolean = false): FlyingStarResult {
        val sitting = MOUNTAIN_BY_NAME[sittingName] ?: XK_MOUNTAINS.first()
        val facing = facingOf(sitting)

        val yunPlate = flyPlate(yun, true)
        val sittingGrid = GUA_TO_GRID[sitting.gua]!!
        val facingGrid = GUA_TO_GRID[facing.gua]!!

        // 运盘落于坐山宫、向首宫之星（定顺逆所依）
        val shanYunStar = yunPlate[sittingGrid]
        val xiangYunStar = yunPlate[facingGrid]

        val shanForward = swingForward(shanYunStar, sitting)
        val xiangForward = swingForward(xiangYunStar, facing)

        // 入中之数：下卦用运盘星；用替则换替星
        val shanCenter = if (useReplacement) (TI_STAR[sitting.name] ?: shanYunStar) else shanYunStar
        val xiangCenter = if (useReplacement) (TI_STAR[facing.name] ?: xiangYunStar) else xiangYunStar

        val shanPlate = flyPlate(shanCenter, shanForward)
        val xiangPlate = flyPlate(xiangCenter, xiangForward)

        val (pattern, desc, luck) = judgePattern(yun, shanPlate, xiangPlate, sittingGrid, facingGrid)
        val notes = extraNotes(yun, yunPlate, shanPlate, xiangPlate)

        return FlyingStarResult(
            yun = yun,
            sitting = sitting,
            facing = facing,
            yunPlate = yunPlate,
            shanPlate = shanPlate,
            xiangPlate = xiangPlate,
            sittingGrid = sittingGrid,
            facingGrid = facingGrid,
            shanCenter = shanCenter,
            xiangCenter = xiangCenter,
            shanForward = shanForward,
            xiangForward = xiangForward,
            pattern = pattern,
            patternDesc = desc,
            patternLuck = luck,
            extraNotes = notes,
            replaced = useReplacement
        )
    }

    private fun judgePattern(
        yun: Int, shan: IntArray, xiang: IntArray, sit: Int, face: Int
    ): Triple<String, String, Int> {
        if (yun == 5) {
            return Triple(
                "五运盘",
                "五运无正卦，前十年寄二黑、后十年寄八白入中，格局须按寄宫另议。",
                0
            )
        }
        val sShan = shan[sit]; val sXiang = xiang[sit]
        val fShan = shan[face]; val fXiang = xiang[face]
        return when {
            sShan == yun && fXiang == yun -> Triple(
                "旺山旺向", "当运山星到坐、向星到向，山管人丁水管财，坐实向空则丁财两旺，上吉之局。", 1
            )
            sXiang == yun && fShan == yun -> Triple(
                "上山下水", "当运向星到坐、山星到向，与峦头相背则损丁破财；若坐空向实反吉，须配峦头。", -1
            )
            fShan == yun && fXiang == yun -> Triple(
                "双星到向", "山向当运星俱到向首，利财而丁稍弱，向首宜见水、宜空旷。", 0
            )
            sShan == yun && sXiang == yun -> Triple(
                "双星到坐", "山向当运星俱到坐山，利丁而财稍弱，坐后宜见水或低旷以收向星之气。", 0
            )
            else -> Triple("一般格局", "非四正格局，吉凶以各宫山向组合及峦头配合论断。", 0)
        }
    }

    private fun extraNotes(yun: Int, yunP: IntArray, shanP: IntArray, xiangP: IntArray): List<String> {
        val notes = mutableListOf<String>()

        var shanHeShi = true
        var xiangHeShi = true
        for (g in 0 until 9) {
            if (yunP[g] + shanP[g] != 10) shanHeShi = false
            if (yunP[g] + xiangP[g] != 10) xiangHeShi = false
        }
        if (shanHeShi) notes.add("山盘与运盘全盘合十——主人丁兴旺。")
        if (xiangHeShi) notes.add("向盘与运盘全盘合十——主财禄丰盈。")

        val triads = listOf(setOf(1, 4, 7), setOf(2, 5, 8), setOf(3, 6, 9))
        var sanBan = true
        for (g in 0 until 9) {
            val s = setOf(yunP[g], shanP[g], xiangP[g])
            if (s !in triads) { sanBan = false; break }
        }
        if (sanBan) notes.add("父母三般卦——通天彻地之贵局（七星打劫格），主连发。")

        return notes
    }

    /** 二十四山名列表（供选择器） */
    fun mountainNames(): List<String> = XK_MOUNTAINS.map { it.name }
}
