package com.aning.xuanxue.feature.knowledge

object KnowledgeRepository {
    val articles: List<KnowledgeArticle> = listOf(
        KnowledgeArticle(
            id = "dao-qingjing",
            title = "清静观念",
            category = KnowledgeCategory.Daoism,
            summary = "清静常被视为修心入门，重在少躁、少争、少被外物牵动。",
            content = "清静不是逃避生活，而是在事务之中保持心神有主。放到现代生活里，可理解为减少无效刺激、减少情绪消耗、让判断回到清明状态。",
            tags = listOf("修心", "道教", "日课")
        ),
        KnowledgeArticle(
            id = "dao-sanqing",
            title = "三清简介",
            category = KnowledgeCategory.Daoism,
            summary = "三清是道教神仙体系中的重要尊神称谓。",
            content = "三清通常指玉清、上清、太清三境尊神，属于道教文化中的高位象征。应用在资料库中，适合做神仙谱系、节日文化、宫观文化的基础词条。",
            tags = listOf("神仙谱系", "道教")
        ),
        KnowledgeArticle(
            id = "dao-beidou",
            title = "北斗文化",
            category = KnowledgeCategory.Daoism,
            summary = "北斗在传统文化中常与时令、方位、守护意象相连。",
            content = "北斗文化在道教与民间信仰中都有重要位置。APP 中可把它作为星宿、夜间静心、节日民俗的知识入口，而不是现实预测工具。",
            tags = listOf("北斗", "星宿", "道教")
        ),
        KnowledgeArticle(
            id = "dao-faqi",
            title = "道教法器文化",
            category = KnowledgeCategory.Daoism,
            summary = "法器是仪式文化中的象征物，承载秩序、提醒与庄重感。",
            content = "常见法器如铃、剑、印、令牌等，重点在仪式象征和文化理解。用于 APP 时可做成图鉴、故事和虚拟收藏，不宣称实际神秘效力。",
            tags = listOf("法器", "图鉴", "仪式")
        ),
        KnowledgeArticle(
            id = "bagua-qian",
            title = "乾卦",
            category = KnowledgeCategory.BaguaWuxing,
            summary = "乾为天，常象征刚健、开创、方向感。",
            content = "乾卦在后天八卦中对应西北，五行多归金。用于空间文化时，可作为长辈、权威、开创力的象征参考；现实层面更适合理解为空间秩序、采光和行动目标。",
            tags = listOf("八卦", "乾", "金")
        ),
        KnowledgeArticle(
            id = "bagua-kun",
            title = "坤卦",
            category = KnowledgeCategory.BaguaWuxing,
            summary = "坤为地，常象征承载、稳定、包容与积累。",
            content = "坤卦在后天八卦中对应西南，五行多归土。现实建议可理解为整理基础、稳定节奏、照顾家庭空间与长期积累。",
            tags = listOf("八卦", "坤", "土")
        ),
        KnowledgeArticle(
            id = "bagua-wuxing-mu",
            title = "五行木",
            category = KnowledgeCategory.BaguaWuxing,
            summary = "木象征生发、成长、舒展和计划。",
            content = "五行木常与东方、春季、青绿色、成长有关。用于日常建议时，可对应学习、整理计划、植物、通风和舒展运动。",
            tags = listOf("五行", "木", "东方")
        ),
        KnowledgeArticle(
            id = "bagua-wuxing-fire",
            title = "五行火",
            category = KnowledgeCategory.BaguaWuxing,
            summary = "火象征光明、表达、热情和显现。",
            content = "五行火常与南方、夏季、红色、表达力有关。现实建议可转化为提升采光、公开表达、控制急躁、保持睡眠节律。",
            tags = listOf("五行", "火", "南方")
        ),
        KnowledgeArticle(
            id = "fengshui-24shan",
            title = "二十四山",
            category = KnowledgeCategory.Fengshui,
            summary = "二十四山是罗盘细分方位的重要基础。",
            content = "二十四山把一周天分为二十四个方位，每山约十五度。APP 中的坐山、分金、纳水等功能都可围绕二十四山展开，适合用于大门、床位、书桌等方向记录。",
            tags = listOf("罗盘", "二十四山", "方位")
        ),
        KnowledgeArticle(
            id = "fengshui-fenjin",
            title = "分金",
            category = KnowledgeCategory.Fengshui,
            summary = "分金是对二十四山方位的进一步细分。",
            content = "分金常用于更细致地观察立向。第一版可作为文化解释和角度记录，不宜做绝对判断。现实使用时，重点仍是空间动线、采光、通风与舒适度。",
            tags = listOf("分金", "罗盘")
        ),
        KnowledgeArticle(
            id = "fengshui-nashui",
            title = "纳水",
            category = KnowledgeCategory.Fengshui,
            summary = "纳水可理解为对水流、道路、人流方向的观察。",
            content = "现代城市里，水不只指河流，也可借喻道路、车流、人流、排水和阳台外部环境。APP 第一版适合做记录和解释，不宜给出绝对吉凶。",
            tags = listOf("纳水", "道路", "人流")
        ),
        KnowledgeArticle(
            id = "fengshui-bazhai",
            title = "八宅概念",
            category = KnowledgeCategory.Fengshui,
            summary = "八宅常把住宅方位与人、门、床、灶联系起来观察。",
            content = "八宅是民间常见的风水体系之一。产品化时可用于方位卡片、家宅检查清单和空间整理建议，现实判断仍要结合采光、通风、安全和生活动线。",
            tags = listOf("八宅", "家宅", "方位")
        ),
        KnowledgeArticle(
            id = "folk-duanwu",
            title = "端午民俗",
            category = KnowledgeCategory.Folk,
            summary = "端午节包含避暑、卫生、纪念与家族仪式等多重文化。",
            content = "端午常见民俗有粽子、艾草、香囊、龙舟等。现代应用可转化为节令提醒、家居清洁、饮食节制和亲友互动。",
            tags = listOf("节日", "端午", "民俗")
        ),
        KnowledgeArticle(
            id = "folk-zao",
            title = "祭灶文化",
            category = KnowledgeCategory.Folk,
            summary = "祭灶是传统年俗中的家庭仪式之一。",
            content = "祭灶反映古人对厨房、饮食和家庭秩序的重视。放在 APP 中，可做腊月民俗提醒，也可延展到厨房清洁、饮食节制、家庭团聚等现实建议。",
            tags = listOf("年俗", "家庭", "厨房")
        ),
        KnowledgeArticle(
            id = "folk-qingming",
            title = "清明民俗",
            category = KnowledgeCategory.Folk,
            summary = "清明兼有节气、踏青与追思的文化意义。",
            content = "清明既是节气，也是重要民俗节点。现实应用可转化为出行提醒、家庭追思、整理旧物和亲友联系。",
            tags = listOf("清明", "节气", "追思")
        ),
        KnowledgeArticle(
            id = "folk-zhongyuan",
            title = "中元节文化",
            category = KnowledgeCategory.Folk,
            summary = "中元节在民间常与慎终追远、敬畏生命相关。",
            content = "中元节在不同地区习俗差异很大。APP 可介绍地方民俗和文化来源，避免恐吓式表达，强调尊重、纪念与安全。",
            tags = listOf("中元", "民俗", "地方习俗")
        ),
        KnowledgeArticle(
            id = "wellness-spring",
            title = "春季养生",
            category = KnowledgeCategory.Wellness,
            summary = "春季重在舒展、早睡早起、减少郁结。",
            content = "传统五行中春属木，重在生发。现实层面可理解为增加户外活动、保持规律作息、少熬夜、温和拉伸。健康问题请咨询专业医生。",
            tags = listOf("节气", "养生", "木")
        ),
        KnowledgeArticle(
            id = "wellness-wuxing-emotion",
            title = "五行情绪调节",
            category = KnowledgeCategory.Wellness,
            summary = "五行可作为情绪观察的文化模型。",
            content = "木火土金水可对应不同情绪倾向。APP 中可用于自我观察：焦躁时放慢节奏，思虑多时整理环境，低落时增加光照和运动。它不是医学诊断。",
            tags = listOf("情绪", "五行", "静心")
        ),
        KnowledgeArticle(
            id = "wellness-winter",
            title = "冬至养生",
            category = KnowledgeCategory.Wellness,
            summary = "冬至重在保暖、休息、蓄养精力。",
            content = "冬至是阴阳转换的重要节点之一。现实层面可提醒保暖、规律作息、减少过度消耗，饮食以适量、清淡和温暖为主。",
            tags = listOf("冬至", "养生", "作息")
        ),
        KnowledgeArticle(
            id = "wellness-breath",
            title = "静心呼吸",
            category = KnowledgeCategory.Wellness,
            summary = "短时呼吸练习适合与每日印记、静心卡结合。",
            content = "可采用温和的数息：吸气四拍、停一拍、呼气六拍，重复三到五轮。若有身体不适，应停止并咨询专业人士。",
            tags = listOf("呼吸", "静心", "日课")
        ),
        KnowledgeArticle(
            id = "face-santing",
            title = "三庭五眼",
            category = KnowledgeCategory.FacePalm,
            summary = "三庭五眼是传统面相中观察面部比例的说法。",
            content = "三庭五眼可作为民俗文化中的比例观察工具。现代使用时更适合理解为审美、仪态和自我观察，不应用来给人下绝对结论。",
            tags = listOf("面相", "比例", "观察")
        ),
        KnowledgeArticle(
            id = "face-12gong",
            title = "面相十二宫",
            category = KnowledgeCategory.FacePalm,
            summary = "十二宫是面相文化中对面部区域的传统划分。",
            content = "面相十二宫属于民俗观察体系，适合做文化学习和图解说明。产品使用时应避免给用户贴标签，可强调仪态、气色、表情管理和生活状态。",
            tags = listOf("面相", "十二宫", "图解")
        ),
        KnowledgeArticle(
            id = "palm-life-line",
            title = "生命线文化",
            category = KnowledgeCategory.FacePalm,
            summary = "生命线是手相文化中常被提到的一条主线。",
            content = "手相里的生命线多用于观察精力、状态和生活节奏的象征说法。APP 中可做文化解释，不做寿命或疾病判断。",
            tags = listOf("手相", "掌纹")
        ),
        KnowledgeArticle(
            id = "palm-heart-line",
            title = "感情线文化",
            category = KnowledgeCategory.FacePalm,
            summary = "感情线常被民俗手相用于观察情绪和关系表达。",
            content = "感情线可作为传统手相文化中的象征线索。现实建议应落到沟通方式、情绪表达和关系边界，不做绝对断语。",
            tags = listOf("手相", "感情线", "关系")
        ),
        KnowledgeArticle(
            id = "dream-water",
            title = "梦见水",
            category = KnowledgeCategory.Dream,
            summary = "水梦常与情绪、流动、变化有关。",
            content = "民俗中水常关联财、情绪和流动。现代解释更适合从情绪波动、压力释放、环境记忆等角度观察。梦境不宜被当成确定预兆。",
            tags = listOf("梦境", "水", "情绪")
        ),
        KnowledgeArticle(
            id = "dream-snake",
            title = "梦见蛇",
            category = KnowledgeCategory.Dream,
            summary = "蛇梦在民俗中象征复杂，常与变化、隐忧、生命力相关。",
            content = "不同地区对蛇梦解释差异很大。APP 可从民俗象征和心理压力两方面提供参考，重点提醒用户记录梦境细节和醒后情绪。",
            tags = listOf("梦境", "蛇", "象征")
        ),
        KnowledgeArticle(
            id = "dream-fly",
            title = "梦见飞行",
            category = KnowledgeCategory.Dream,
            summary = "飞行梦常与自由、逃离压力、想突破限制相关。",
            content = "民俗解释中飞行有升腾之象，现代视角可理解为对空间、压力或目标的心理投射。可提醒用户记录梦中高度、方向和情绪。",
            tags = listOf("梦境", "飞行", "压力")
        ),
        KnowledgeArticle(
            id = "dream-house",
            title = "梦见房屋",
            category = KnowledgeCategory.Dream,
            summary = "房屋梦常与安全感、家庭、个人边界有关。",
            content = "房屋在梦中常象征自我空间或家庭结构。可结合梦中的房间、门窗、光线、是否熟悉来做温和分析。",
            tags = listOf("梦境", "房屋", "家庭")
        ),
        KnowledgeArticle(
            id = "card-jingxin",
            title = "静心卡",
            category = KnowledgeCategory.Talisman,
            summary = "静心卡用于提醒用户放慢节奏、减少情绪消耗。",
            content = "符卡在 APP 中应作为虚拟收藏和心理提醒，不宣称实际神秘效力。静心卡适合搭配呼吸练习、短时静坐、睡前整理。",
            tags = listOf("符卡", "静心", "收藏")
        ),
        KnowledgeArticle(
            id = "card-shouzhai",
            title = "守宅卡",
            category = KnowledgeCategory.Talisman,
            summary = "守宅卡用于提醒用户重视家庭空间秩序。",
            content = "守宅卡可以转化为现实动作：清理玄关、检查门窗、保持厨房干净、减少杂物。它是文化符号与生活建议的结合。",
            tags = listOf("符卡", "家宅", "空间")
        ),
        KnowledgeArticle(
            id = "card-kaiming",
            title = "开明卡",
            category = KnowledgeCategory.Talisman,
            summary = "开明卡用于提醒用户整理思路、打开局面。",
            content = "开明卡适合用于学习、写作、工作规划前。现实动作可以是列三件最重要的事、清理桌面、关闭干扰。",
            tags = listOf("符卡", "学习", "计划")
        ),
        KnowledgeArticle(
            id = "card-hehe",
            title = "和合卡",
            category = KnowledgeCategory.Talisman,
            summary = "和合卡用于提醒用户改善沟通、减少对抗。",
            content = "和合卡不代表关系一定变好，而是提醒用户在沟通中降低攻击性、明确边界、给对方表达空间。",
            tags = listOf("符卡", "沟通", "关系")
        )
    )

    fun byCategory(category: KnowledgeCategory): List<KnowledgeArticle> =
        articles.filter { it.category == category }

    fun search(keyword: String): List<KnowledgeArticle> {
        val q = keyword.trim()
        if (q.isEmpty()) return articles
        return articles.filter { article ->
            article.title.contains(q, ignoreCase = true) ||
                article.summary.contains(q, ignoreCase = true) ||
                article.content.contains(q, ignoreCase = true) ||
                article.tags.any { it.contains(q, ignoreCase = true) } ||
                article.category.label.contains(q, ignoreCase = true)
        }
    }

    fun find(id: String): KnowledgeArticle? = articles.firstOrNull { it.id == id }
}
