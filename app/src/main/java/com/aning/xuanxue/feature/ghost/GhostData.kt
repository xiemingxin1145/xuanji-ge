package com.aning.xuanxue.feature.ghost

import androidx.compose.ui.graphics.Color

/**
 * 最后一个道士 · 鬼怪图鉴
 * 来源：山海经、搜神记、太平广记（均为公共域）
 */

enum class GhostRarity(val label: String, val color: Color) {
    COMMON("游魂",    Color(0xFF78909C)),
    UNCOMMON("厉鬼",  Color(0xFF66BB6A)),
    RARE("凶煞",      Color(0xFF42A5F5)),
    EPIC("妖魂",      Color(0xFFAB47BC)),
    LEGENDARY("古神残骸", Color(0xFFFF7043))
}

enum class GhostElement(val label: String, val symbol: String) {
    WATER("水", "☵"),
    FIRE("火", "☲"),
    WOOD("木", "☳"),
    METAL("金", "☱"),
    EARTH("土", "☷")
}

data class GhostType(
    val id: String,
    val name: String,
    val alias: String,
    val rarity: GhostRarity,
    val element: GhostElement,
    val origin: String,           // 典故出处
    val description: String,
    val catchDifficulty: Int,     // 1-10
    val weaknessTool: String,     // 克制工具提示
    val sealPattern: SealPattern, // 镇压符咒类型
    val dropEssence: Int,         // 捉住后获得的鬼气
    val dropXp: Int,
    val loreUnlock: String        // 解锁后展示的民俗知识
)

enum class SealPattern {
    CIRCLE,     // 画圆
    TRIANGLE,   // 画三角
    CROSS,      // 画十字
    SPIRAL,     // 螺旋
    EIGHT       // 八字
}

object GhostRegistry {
    val all: List<GhostType> = listOf(

        GhostType(
            id = "you_hun",
            name = "游魂", alias = "孤魂野鬼",
            rarity = GhostRarity.COMMON,
            element = GhostElement.WATER,
            origin = "《礼记·祭义》",
            description = "死而无所归谓之鬼。游魂多为意外横死、无人祭奠者，徘徊于阳间，性情哀怨而不凶险。以朱砂符安抚，可引渡超度。",
            catchDifficulty = 2,
            weaknessTool = "老黄历宜日 + 朱砂符",
            sealPattern = SealPattern.CIRCLE,
            dropEssence = 15,
            dropXp = 20,
            loreUnlock = "民间认为，死后四十九日若无人超度，魂魄便化为游魂。中元节烧纸钱正是为安抚无主游魂。"
        ),

        GhostType(
            id = "shui_gui",
            name = "水鬼", alias = "落水冤魂",
            rarity = GhostRarity.COMMON,
            element = GhostElement.WATER,
            origin = "《太平广记·溺鬼》",
            description = "溺死水中之魂，无法自行离水，须找替身方能转世。多出没于江河湖泊附近，入夜后于水边哭泣，引人落水。",
            catchDifficulty = 3,
            weaknessTool = "罗盘坎位定位 + 镇水符",
            sealPattern = SealPattern.CIRCLE,
            dropEssence = 20,
            dropXp = 30,
            loreUnlock = "水鬼需找'替身'才能投胎，这与中国传统的替代巫术观念有关。民间会在落水处设香案超度，防止水鬼作祟。"
        ),

        GhostType(
            id = "yuan_hun",
            name = "冤魂", alias = "含冤之鬼",
            rarity = GhostRarity.UNCOMMON,
            element = GhostElement.FIRE,
            origin = "《搜神记·冤案篇》",
            description = "含冤而死，执念深重，不散不灭。怨气化火，专寻仇家报复。与游魂不同，冤魂有明确目标，道士须先化解冤屈，方可超度。",
            catchDifficulty = 5,
            weaknessTool = "易经起卦问其冤由 + 解冤符",
            sealPattern = SealPattern.TRIANGLE,
            dropEssence = 45,
            dropXp = 60,
            loreUnlock = "东汉蔡邕《独断》载：冤死者鬼形多带血色。民间相传，为冤死者在案发地立碑昭雪，可令其自行散去。"
        ),

        GhostType(
            id = "li_gui",
            name = "厉鬼", alias = "恶煞凶鬼",
            rarity = GhostRarity.UNCOMMON,
            element = GhostElement.METAL,
            origin = "《礼记·檀弓》",
            description = "暴死、凶死而成厉，怨气极重，形貌狰狞。厉鬼不似游魂般飘忽，而是主动攻击阳气旺盛之人。须以五行压制，正面对抗。",
            catchDifficulty = 6,
            weaknessTool = "罗盘定乾位 + 五行镇煞符",
            sealPattern = SealPattern.CROSS,
            dropEssence = 70,
            dropXp = 100,
            loreUnlock = "《周礼》中'厉鬼'专指无后代祭祀的孤魂，国家会定期举行厉祭，由官方祭祀以免其为乱。城隍庙供奉城隍即有管理厉鬼之职。"
        ),

        GhostType(
            id = "shan_mei",
            name = "山魅", alias = "山林精怪",
            rarity = GhostRarity.RARE,
            element = GhostElement.WOOD,
            origin = "《山海经·南山经》",
            description = "山林久远之地积聚的精气所化，形如人而独足，行走无声。《山海经》载：'山魅如人，见人则笑，薄暮尤甚。'非鬼非妖，难以常法对付。",
            catchDifficulty = 7,
            weaknessTool = "易经艮卦 + 山神令牌",
            sealPattern = SealPattern.SPIRAL,
            dropEssence = 120,
            dropXp = 180,
            loreUnlock = "山魅崇拜与中国山神信仰密切相关。《楚辞·山鬼》中的'山鬼'形象妩媚，被认为即山魅原型。古人入山前必祭山神以求平安。"
        ),

        GhostType(
            id = "ye_cha",
            name = "夜叉", alias = "执暗之鬼",
            rarity = GhostRarity.RARE,
            element = GhostElement.METAL,
            origin = "佛道融合典籍·《封神演义》",
            description = "源自佛教护法，入道家典籍后演变为凶暴灵体。形体壮硕，铁甲披身，专司黑暗中的执法，但有时被邪力驱使为恶。",
            catchDifficulty = 8,
            weaknessTool = "八字命格克制 + 降魔咒",
            sealPattern = SealPattern.CROSS,
            dropEssence = 200,
            dropXp = 280,
            loreUnlock = "夜叉（Yaksha）源自古印度神话，随佛教传入中国后与本土鬼神体系融合。在道教体系中，城隍的鬼卒有时即为夜叉形象。"
        ),

        GhostType(
            id = "jiu_mei",
            name = "九尾狐魅", alias = "天狐化形",
            rarity = GhostRarity.EPIC,
            element = GhostElement.FIRE,
            origin = "《山海经·大荒东经》·《搜神记》",
            description = "修炼千年的狐妖，化人形，惑人心。《山海经》载青丘之狐有九尾，食之不蛊。九尾之数象征极高道行，非普通法术可制。",
            catchDifficulty = 9,
            weaknessTool = "命格五行克制 + 玄机爆发级共鸣",
            sealPattern = SealPattern.EIGHT,
            dropEssence = 500,
            dropXp = 700,
            loreUnlock = "九尾狐在中国上古神话中本为祥瑞，《山海经》中青丘国九尾狐象征太平盛世。后经汉代纬书渲染，逐渐演变为妖媚之象。"
        ),

        GhostType(
            id = "gu_shen",
            name = "古神残骸", alias = "上古遗识",
            rarity = GhostRarity.LEGENDARY,
            element = GhostElement.EARTH,
            origin = "《山海经·大荒北经》",
            description = "上古诸神陨落后遗留的意识碎片，非生非死，非鬼非神。体量庞大，智识尚存，对现世充满困惑与执念。捉之不易，化解更难。",
            catchDifficulty = 10,
            weaknessTool = "三才齐备 · 玄机爆发 · 阁主亲临",
            sealPattern = SealPattern.SPIRAL,
            dropEssence = 2000,
            dropXp = 3000,
            loreUnlock = "《山海经》所载诸神，如刑天、夸父，皆为陨落而未消散的上古神祇。道教认为，天地初开时的神灵意识永不真正消亡，只是以不同形态潜伏。"
        )
    )

    fun byId(id: String) = all.first { it.id == id }
    fun byRarity(rarity: GhostRarity) = all.filter { it.rarity == rarity }
}
