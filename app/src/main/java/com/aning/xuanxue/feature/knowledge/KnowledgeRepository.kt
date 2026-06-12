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
            id = "bagua-qian",
            title = "乾卦",
            category = KnowledgeCategory.BaguaWuxing,
            summary = "乾为天，常象征刚健、开创、方向感。",
            content = "乾卦在后天八卦中对应西北，五行多归金。用于空间文化时，可作为长辈、权威、开创力的象征参考；现实层面更适合理解为空间秩序、采光和行动目标。",
            tags = listOf("八卦", "乾", "金")
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
            id = "face-santing",
            title = "三庭五眼",
            category = KnowledgeCategory.FacePalm,
            summary = "三庭五眼是传统面相中观察面部比例的说法。",
            content = "三庭五眼可作为民俗文化中的比例观察工具。现代使用时更适合理解为审美、仪态和自我观察，不应用来给人下绝对结论。",
            tags = listOf("面相", "比例", "观察")
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
        )
    )

    fun byCategory(category: KnowledgeCategory): List<KnowledgeArticle> =
        articles.filter { it.category == category }

    fun find(id: String): KnowledgeArticle? = articles.firstOrNull { it.id == id }
}
