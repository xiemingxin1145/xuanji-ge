package com.aning.xuanxue.feature.knowledge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.InkSurface2
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold

@Composable
fun KnowledgeScreen(
    onBack: () -> Unit,
    onAiPrompt: (String) -> Unit
) {
    var category by remember { mutableStateOf(KnowledgeCategory.Daoism) }
    var selected by remember { mutableStateOf<KnowledgeArticle?>(null) }
    val list = remember(category) { KnowledgeRepository.byCategory(category) }

    XScaffold(title = "玄门资料库", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("传统文化资料库")
                Spacer(Modifier.height(8.dp))
                Text(
                    "先做本地知识库，不联网也能查。后续可继续扩成道教、民俗、风水、梦境、养生、面相手相大全。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KnowledgeCategory.values().forEach { c ->
                    AssistChip(
                        onClick = {
                            category = c
                            selected = null
                        },
                        label = { Text(c.label, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (c == category) Gold else InkSurface2,
                            labelColor = if (c == category) Ink else TextMain
                        )
                    )
                }
            }

            if (selected == null) {
                list.forEach { article ->
                    XCard(Modifier.fillMaxWidth()) {
                        Text(article.title, color = GoldBright, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(article.summary, color = TextMain, fontSize = 13.sp, lineHeight = 19.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(article.tags.joinToString(" · "), color = TextSub, fontSize = 11.sp)
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { selected = article },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                        ) { Text("查看详情") }
                    }
                }
            } else {
                val article = selected!!
                XCard(Modifier.fillMaxWidth()) {
                    Text(article.title, color = GoldBright, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(article.summary, color = Jade, fontSize = 13.sp, lineHeight = 19.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(article.content, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("标签：${article.tags.joinToString("、")}", color = TextSub, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(article.caution, color = TextSub, fontSize = 11.sp, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onAiPrompt(buildKnowledgePrompt(article)) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                        ) { Text("问 AI 玄师") }
                        Button(
                            onClick = { selected = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                        ) { Text("返回列表") }
                    }
                }
            }
        }
    }
}

private fun buildKnowledgePrompt(article: KnowledgeArticle): String = buildString {
    appendLine("【玄门资料库文章】")
    appendLine("标题：${article.title}")
    appendLine("分类：${article.category.label}")
    appendLine("摘要：${article.summary}")
    appendLine("正文：${article.content}")
    appendLine("标签：${article.tags.joinToString("、")}")
    appendLine()
    appendLine("请基于这篇资料，用通俗语言继续讲解，并给出适合日常生活的参考建议。")
    appendLine("请保持温和，不做绝对承诺，不制造焦虑，重大事项提醒用户理性判断。")
}
