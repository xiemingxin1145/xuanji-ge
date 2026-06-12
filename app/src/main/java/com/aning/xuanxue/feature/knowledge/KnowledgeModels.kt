package com.aning.xuanxue.feature.knowledge

data class KnowledgeArticle(
    val id: String,
    val title: String,
    val category: KnowledgeCategory,
    val summary: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val caution: String = "传统文化与民俗资料，仅供了解与娱乐参考，不作为现实决策依据。"
)

enum class KnowledgeCategory(val label: String) {
    Daoism("道教基础"),
    BaguaWuxing("八卦五行"),
    Fengshui("风水常识"),
    Folk("民俗传统"),
    Wellness("节气养生"),
    FacePalm("面相手相"),
    Dream("梦境象征"),
    Talisman("符卡文化")
}
