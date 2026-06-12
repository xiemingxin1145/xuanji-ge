package com.aning.xuanxue.feature.case_engine

/**
 * 案件数据仓库 — 硬编码第一批案件
 * 后期可迁移到JSON / Room
 */
object CaseRepository {

    val allCases: List<CaseFull> = listOf(case001(), case002())

    fun getCase(id: String) = allCases.first { it.meta.id == id }

    // ─────────────────────────────────────────
    // 案件001：旧楼鬼语
    // ─────────────────────────────────────────
    private fun case001() = CaseFull(
        meta = CaseMeta(
            id = "case_001",
            title = "旧楼鬼语",
            subtitle = "深夜的哭声究竟来自何方",
            difficulty = 1,
            clientName = "林阿婆",
            clientDesc = "城中旧式公屋住户，自称每逢子时便听见楼道哭声，已三月有余",
            rewardDesc = "声望+15 · 灵气+200 · 解锁「奠基符」图纸",
            requiredReputation = 0,
            tags = listOf("幽灵", "风水", "都市", "新手引导")
        ),
        scenes = listOf(
            CaseScene(
                id = "s1_arrival",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "夜雨敲窗，玄机阁的铜铃忽地无风自响。\n\n一位头发花白的老阿婆颤巍巍推开木门，她的眼圈乌青，手里攥着一块泛黄的符纸——那是某位江湖术士留下的残次品，朱砂笔迹已经洇湿模糊。",
                speakerName = "林阿婆",
                dialogue = "阁主，我住在城西那栋旧楼，三十年了。可从三个月前开始，每到子时，四楼楼道就有小孩哭声……我去敲门，没有人。物业说没有小孩住那层，可那哭声……我一个老婆子快撑不住了。",
                bgHint = "rain_dark",
                choices = listOf(
                    CaseChoice("c1_ask_time", "询问具体时间与规律", "了解天时信息", "s2_asktime", rewardXuanqi = 5),
                    CaseChoice("c1_ask_floor", "追问四楼的历史", "收集地利线索", "s2_askfloor", rewardXuanqi = 5),
                    CaseChoice("c1_direct", "表示明日亲往勘察", "直接行动派", "s2_direct", rewardXuanqi = 0)
                )
            ),
            CaseScene(
                id = "s2_asktime",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "林阿婆仔细回想，声音从未在子时之前出现，而且……",
                speakerName = "林阿婆",
                dialogue = "对对，每次都是子时刚过几分钟，而且……奇怪，只有月相圆的时候最响。上个月月圆那晚，我隔着门都能听见，像是有什么东西在……呜咽。",
                bgHint = "moonlight_eerie",
                toolPrompt = "天时线索已收集，建议查验近期黄历与月相",
                toolRequired = ToolAction.ALMANAC,
                choices = listOf(
                    CaseChoice("c2_compass", "前往旧楼，罗盘勘察四楼格局", "获取地利数据", "s3_compass", minResonance = 0),
                    CaseChoice("c2_resonance", "先在阁中推演天时地利人和", "稳扎稳打", "s3_resonance_check", minResonance = 0)
                )
            ),
            CaseScene(
                id = "s2_askfloor",
                atmosphere = Atmosphere.EERIE,
                narrative = "老阿婆的眼神暗了一暗，像是触碰到了什么不愿回忆的往事。",
                speakerName = "林阿婆",
                dialogue = "四楼……十八年前住着一户姓钱的人家。那家的小女儿，才五岁，走丢了，再没找到。之后那户人家就搬走了，那间房一直空着，没人肯租。",
                bgHint = "cold_blue_memory",
                choices = listOf(
                    CaseChoice("c2_compass", "前往旧楼，罗盘勘察四楼格局", "获取地利数据", "s3_compass"),
                    CaseChoice("c2_resonance", "先在阁中推演天时地利人和", "稳扎稳打", "s3_resonance_check")
                )
            ),
            CaseScene(
                id = "s2_direct",
                atmosphere = Atmosphere.CALM,
                narrative = "林阿婆感激地点点头，将那块湿透的符纸放在桌上，颤着手说了声谢谢，便消失在夜雨里。\n\n你握着符纸，感到一股冰凉从指尖蔓上来——这不是普通的阴气。",
                bgHint = "rain_cold",
                toolPrompt = "线索尚不完整，建议先以易经起卦问此案吉凶",
                toolRequired = ToolAction.ICHING,
                choices = listOf(
                    CaseChoice("c2_iching_done", "起卦完毕，前往旧楼", "易经引路", "s3_compass"),
                    CaseChoice("c2_skip", "直接动身，不起卦", "快刀斩乱麻", "s3_compass", minResonance = 45)
                )
            ),
            CaseScene(
                id = "s3_compass",
                atmosphere = Atmosphere.EERIE,
                narrative = "旧楼四楼楼道，灯管一明一灭，嗡嗡作响。\n\n你展开罗盘，针尖忽然剧烈颤动——这楼的巽位（东南角）存在严重的煞气堆积。四楼最东南那间空房，正是煞气汇聚的核心。\n\n更诡异的是，罗盘红针反复指向空房门缝下的一道细微光线。",
                bgHint = "compass_red_glow",
                toolPrompt = "罗盘已锁定煞气核心，现在推算玄机共鸣",
                toolRequired = ToolAction.RESONANCE,
                choices = listOf(
                    CaseChoice("c3_push_door", "推开那扇空房的门", "直面煞气", "s4_room_enter", minResonance = 60),
                    CaseChoice("c3_set_talisman", "在门缝处布置镇压符", "稳健处理", "s4_talisman_route", minResonance = 30),
                    CaseChoice("c3_retreat", "退出楼道，重新推演", "谨慎派", "s3_resonance_check")
                )
            ),
            CaseScene(
                id = "s3_resonance_check",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "回到玄机阁，你摊开天时地利人和三项数据，指尖在推盘上缓缓转动。\n\n天时不稳——月圆之夜阴气最盛，须速战速决。\n地利已知——巽位煞气，需以乾位之气破之。\n人和尚缺——那孩子的生辰？无人知晓，此为最大变数。",
                bgHint = "desk_candlelight",
                toolRequired = ToolAction.RESONANCE,
                choices = listOf(
                    CaseChoice("c3_goto_compass", "带齐法器，前往旧楼", "准备充分", "s3_compass", rewardXuanqi = 10),
                    CaseChoice("c3_ask_neighbor", "先去问旧楼邻居，查那孩子生辰", "补全人和", "s3_find_birthday")
                )
            ),
            CaseScene(
                id = "s3_find_birthday",
                atmosphere = Atmosphere.CALM,
                narrative = "二楼的李大爷是这栋楼最老的住户。他想了很久，说那孩子叫钱小雨，属虎，生于壬子年丙午月。\n\n你在心里默算——壬子水命，五行缺火，难怪此地阴气长聚，无人化解。",
                bgHint = "corridor_dim",
                choices = listOf(
                    CaseChoice("c3_resonance_full", "重新计算玄机共鸣（已补全人和）", "三项数据齐全", "s3_resonance_full_check", rewardXuanqi = 15)
                )
            ),
            CaseScene(
                id = "s3_resonance_full_check",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "天时·地利·人和三项齐备，玄机共鸣计算完成。\n\n共鸣值达到强烈等级——今夜行事，成算在七分以上。",
                bgHint = "resonance_gold_burst",
                toolRequired = ToolAction.RESONANCE,
                choices = listOf(
                    CaseChoice("c3_goto_room", "前往空房，一次了结", "万事俱备", "s4_room_enter_full", rewardXuanqi = 0)
                )
            ),
            CaseScene(
                id = "s4_room_enter",
                atmosphere = Atmosphere.EERIE,
                narrative = "门轴发出一声低沉的呻吟，空房暗若深渊。\n\n手电光扫过去——房间正中，一双小小的布鞋，整整齐齐地摆在那里。\n\n你身后的门，在无风的室内，缓缓关上了。",
                bgHint = "darkness_cold",
                choices = listOf(
                    CaseChoice("c4_talk", "轻声说：「小雨，我来帮你了。」", "以善意沟通", "s5_good_ending", minResonance = 55, rewardXuanqi = 30),
                    CaseChoice("c4_force", "取出最强驱邪符，强行镇压", "强硬路线", "s5_bad_ending", minResonance = 0, rewardXuanqi = 5)
                )
            ),
            CaseScene(
                id = "s4_room_enter_full",
                atmosphere = Atmosphere.TRIUMPHANT,
                narrative = "门推开的瞬间，你感到玄机共鸣的金光在丹田处猛地一震。\n\n房间正中那双布鞋周围，浮现出一圈微弱的荧光——那是一个孩子模糊的轮廓，低着头，无声地哭泣。\n\n她感应到你的到来，抬起头。眼神里没有恶意，只有漫长的、无边的迷茫。",
                bgHint = "ghost_soft_glow",
                choices = listOf(
                    CaseChoice("c4_talk_full", "跪下来，轻声说：「小雨，我知道你在这里。你愿意走了吗？」", "慈悲化解", "s5_hidden_ending", rewardXuanqi = 50)
                )
            ),
            CaseScene(
                id = "s4_talisman_route",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "你在四楼巽位门缝、乾位墙角、坎位楼道灯架处各贴一符，以乾克巽，以土镇水。\n\n朱砂符纸贴上的瞬间，楼道的灯管奇异地稳定下来，不再闪烁。\n\n哭声，消失了。",
                bgHint = "talisman_red_calm",
                choices = listOf(
                    CaseChoice("c4_check_result", "静候片刻，感知煞气是否消散", "谨慎验收", "s5_partial_ending", rewardXuanqi = 20)
                )
            ),
            CaseScene(
                id = "s5_good_ending",
                atmosphere = Atmosphere.TRIUMPHANT,
                narrative = "沉默。\n\n然后，你听见一声极轻极轻的回应——不是哭声，是一个孩子在说：「……谢谢你。」\n\n布鞋消失了。房间里残留的阴气，像被春风吹散的薄雾，悄然散去。\n\n林阿婆再也没有在夜里听见哭声。",
                bgHint = "dawn_gold",
                isEnding = true,
                endingType = EndingType.GOOD,
                choices = emptyList()
            ),
            CaseScene(
                id = "s5_bad_ending",
                atmosphere = Atmosphere.TENSE,
                narrative = "符纸燃起，房间里猛地涌起一股怨气——你强行镇压了她，但没有化解。\n\n三天后，林阿婆告诉你：哭声没了，但她的猫死了。楼道的镜子无故破碎了两块。\n\n怨灵被压而未散，只是换了一种方式存在。",
                bgHint = "dull_failure",
                isEnding = true,
                endingType = EndingType.BAD,
                choices = emptyList()
            ),
            CaseScene(
                id = "s5_hidden_ending",
                atmosphere = Atmosphere.TRIUMPHANT,
                narrative = "她沉默了很久。\n\n然后，那双布鞋缓缓浮起，随着一道淡金色的光落入你掌心——变成了一枚小小的铜钱。\n\n你没有驱逐她，没有镇压她。你让她自己选择了离开。\n\n玄机阁门口的铜铃，在毫无风的深夜，清脆地响了三声。",
                bgHint = "hidden_gold_dawn",
                isEnding = true,
                endingType = EndingType.HIDDEN,
                choices = emptyList()
            ),
            CaseScene(
                id = "s5_partial_ending",
                atmosphere = Atmosphere.CALM,
                narrative = "符咒的效果维持了整整一个月。\n\n林阿婆打来电话，说哭声彻底消失了——但她总感觉四楼偶尔还有什么东西在，安静地待着，不伤人，也不说话。\n\n也许，有些东西，并不需要被赶走。",
                bgHint = "bittersweet_grey",
                isEnding = true,
                endingType = EndingType.GOOD,
                choices = emptyList()
            )
        )
    )

    // ─────────────────────────────────────────
    // 案件002：锁魂茶馆（解锁：声望≥20）
    // ─────────────────────────────────────────
    private fun case002() = CaseFull(
        meta = CaseMeta(
            id = "case_002",
            title = "锁魂茶馆",
            subtitle = "百年老字号里藏着的不只是秘方",
            difficulty = 2,
            clientName = "茶馆老板 张怀远",
            clientDesc = "城中百年老字号「怀远茶庄」第三代传人，近月来频频梦见先祖，账目离奇错乱",
            rewardDesc = "声望+25 · 灵气+350 · 解锁「镇宅五行阵」",
            requiredReputation = 20,
            tags = listOf("先祖执念", "商业风水", "经营", "茶馆")
        ),
        scenes = listOf(
            CaseScene(
                id = "s1_intro",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "茶香飘过门槛，一个中年男人坐在玄机阁里，手边的茶杯却一直没动。他叫张怀远，身后是一家传了百年的茶庄。\n\n而百年的老店，往往也有百年的旧账。",
                speakerName = "张怀远",
                dialogue = "阁主，我知道这听起来荒唐……但我每晚都梦见我太爷爷。他站在茶庄后院，指着地上，一句话不说，就那么看着我。账房那边，钱老是对不上，伙计们也接连出事——上周新来的小伙，在仓库里晕倒了，说看见一个穿长袍的老人。",
                bgHint = "tea_smoke_dim",
                choices = listOf(
                    CaseChoice("c1_ask_history", "茶庄后院有什么特别之处？", "地利线索", "s2_history"),
                    CaseChoice("c1_ask_dream", "太爷爷在梦里指的是地上哪里？", "人和线索", "s2_dream"),
                    CaseChoice("c1_bazi", "为张怀远排八字，看祖先羁绊", "命理入手", "s2_bazi_check")
                )
            ),
            CaseScene(
                id = "s2_history",
                atmosphere = Atmosphere.EERIE,
                narrative = "张怀远沉默了一下，说：「后院有口老井，封了三十年了。太爷爷临终前说过，那口井不能开，但也不能迁。」\n\n老井——地脉节点，极有可能是气场紊乱之源。",
                bgHint = "old_well_dark",
                toolRequired = ToolAction.COMPASS,
                choices = listOf(
                    CaseChoice("c2_investigate", "前往茶庄，罗盘勘查古井方位", "实地调查", "s3_well_compass")
                )
            ),
            CaseScene(
                id = "s2_dream",
                atmosphere = Atmosphere.MYSTERIOUS,
                narrative = "「地上……」张怀远皱眉回忆，「他指的是后院的青砖地。那片砖，比其他地方颜色深一些，我一直以为是返潮。」\n\n青砖变色——土中有物，或是旧物未迁，或是有人曾在此处埋下什么。",
                bgHint = "dark_bricks",
                choices = listOf(
                    CaseChoice("c2_investigate", "前往茶庄，罗盘勘查后院", "实地调查", "s3_well_compass")
                )
            ),
            CaseScene(
                id = "s2_bazi_check",
                atmosphere = Atmosphere.CALM,
                narrative = "张怀远报出生辰，你起出四柱——此人命局中祖星极旺，祖先牵绊深重，而近年大运行至「伏吟」，极易引发旧事浮现。\n\n先祖并非索命，而是有话要说。",
                bgHint = "bazi_chart_glow",
                toolRequired = ToolAction.BAZI,
                choices = listOf(
                    CaseChoice("c2_goto", "前往茶庄一探究竟", "实地调查", "s3_well_compass", rewardXuanqi = 10)
                )
            ),
            CaseScene(
                id = "s3_well_compass",
                atmosphere = Atmosphere.EERIE,
                narrative = "茶庄后院，青砖铺地，古井封口的石板已经长满了青苔。\n\n罗盘放下，针尖激烈震颤——井口正处于整个宅院的「财位」与「凶位」叠加之处，封井三十年，导致财气与怨气同时郁积，无法流转。\n\n井盖下方，你隐约感到有什么东西在呼吸。",
                bgHint = "well_compass_glow",
                toolRequired = ToolAction.RESONANCE,
                choices = listOf(
                    CaseChoice("c3_open_well", "计算共鸣，决定是否开井疏导", "以气疏气", "s4_open_well_ritual", minResonance = 65, rewardXuanqi = 20),
                    CaseChoice("c3_seal_better", "重新布局，以五行阵重封古井", "镇而不开", "s4_reseal_ritual", minResonance = 40)
                )
            ),
            CaseScene(
                id = "s4_open_well_ritual",
                atmosphere = Atmosphere.TRIUMPHANT,
                narrative = "井盖揭开的瞬间，一股陈年的茶香混着泥土气息扑面而来——不是煞气，是百年的积郁，终于得以呼吸。\n\n井壁上，刻着一行小字：「怀远吾孙，秘方在此，勿忘初心，茶为人饮。」",
                bgHint = "well_open_golden",
                isEnding = true,
                endingType = EndingType.GOOD,
                choices = emptyList()
            ),
            CaseScene(
                id = "s4_reseal_ritual",
                atmosphere = Atmosphere.CALM,
                narrative = "五行镇宅阵布下，财位补以乾金之气，凶位以坤土压制。\n\n张怀远当晚，梦见太爷爷对他点了点头，转身离去。\n\n茶庄的账，从此再没出过差错。",
                bgHint = "talisman_gold_calm",
                isEnding = true,
                endingType = EndingType.GOOD,
                choices = emptyList()
            )
        )
    )
}
