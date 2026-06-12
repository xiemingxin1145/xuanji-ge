package com.aning.xuanxue.feature.guide

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.KV
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold

private data class GuideOption(
    val title: String,
    val subtitle: String,
    val routeHint: String,
    val steps: List<String>,
    val aiTask: String
)

private val guideOptions = listOf(
    GuideOption(
        title = "事业",
        subtitle = "今日状态、行动节奏、现实建议",
        routeHint = "建议流程：今日黄历 → 八字五行 → AI 玄师整理",
        steps = listOf("先看今日宜忌和干支", "再结合八字五行偏向", "最后让 AI 玄师生成可执行建议"),
        aiTask = "用户想问事业方向，请结合今日黄历、五行倾向与现实行动建议做简要分析。"
    ),
    GuideOption(
        title = "财运",
        subtitle = "消费、合作、项目机会提醒",
        routeHint = "建议流程：今日黄历 → 方位参考 → AI 玄师整理",
        steps = listOf("先看今日宜忌", "参考今日方位和五行", "提醒用户理性判断投资与消费"),
        aiTask = "用户想问财务与机会，请给出传统文化角度的象意解释和现实层面的理性建议。"
    ),
    GuideOption(
        title = "感情",
        subtitle = "关系状态、沟通方式、情绪提醒",
        routeHint = "建议流程：易经起卦 → AI 解读 → 现实沟通建议",
        steps = listOf("先明确所问关系", "可用易经起卦记录当下象意", "AI 玄师给出沟通建议"),
        aiTask = "用户想问感情与关系，请以温和方式分析关系状态，并给出现实沟通建议。"
    ),
    GuideOption(
        title = "家宅风水",
        subtitle = "大门、床位、书桌、空间整理",
        routeHint = "建议流程：风水罗盘 → 分金/纳水 → AI 空间建议",
        steps = listOf("打开罗盘测大门或床头", "记录角度、坐山、卦位", "AI 玄师生成空间整理建议"),
        aiTask = "用户想问家宅空间，请引导其测量大门、床位或书桌方位，并给出安全的空间整理建议。"
    ),
    GuideOption(
        title = "出行择日",
        subtitle = "出门、签约、办事节奏",
        routeHint = "建议流程：老黄历 → 冲煞/宜忌 → AI 提醒",
        steps = listOf("先看今日宜忌", "查看冲煞和时辰", "AI 玄师生成出行提醒"),
        aiTask = "用户想问出行或办事时机，请结合黄历信息给出提醒，同时强调现实安全与准备。"
    ),
    GuideOption(
        title = "解梦",
        subtitle = "梦境象意、情绪线索、今日提醒",
        routeHint = "建议流程：描述梦境 → AI 玄师解析",
        steps = listOf("写下梦见的人、物、地点", "补充醒来后的情绪", "AI 玄师给出民俗象意和心理提醒"),
        aiTask = "用户想解梦，请先请用户描述梦境细节，再从民俗象意和情绪线索两方面分析。"
    ),
    GuideOption(
        title = "起名",
        subtitle = "五行方向、用字气质、现实读音",
        routeHint = "建议流程：八字五行 → 姓名五行 → AI 起名建议",
        steps = listOf("先输入出生时间看五行", "再看姓氏与候选字", "AI 玄师给出用字方向"),
        aiTask = "用户想起名，请结合五行方向、读音、寓意和现实使用感给出建议。"
    )
)

@Composable
fun GuideScreen(
    onBack: () -> Unit,
    onAiPrompt: (String) -> Unit
) {
    var selected by remember { mutableStateOf<GuideOption?>(null) }

    XScaffold(title = "今日问玄 · 玄门向导", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("你今天想问哪方面？")
                Spacer(Modifier.height(8.dp))
                Text(
                    "先选目的，再由 APP 推荐流程。别让用户乱点工具，要让它一步步带着走。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            guideOptions.forEach { option ->
                OutlinedButton(
                    onClick = { selected = option },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(option.title, color = Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(3.dp))
                        Text(option.subtitle, color = TextSub, fontSize = 12.sp)
                    }
                }
            }

            selected?.let { option ->
                XCard(Modifier.fillMaxWidth()) {
                    SectionTitle("推荐流程 · ${option.title}")
                    Spacer(Modifier.height(8.dp))
                    KV("路线", option.routeHint)
                    Spacer(Modifier.height(8.dp))
                    option.steps.forEachIndexed { index, step ->
                        Text("${index + 1}. $step", color = TextMain, fontSize = 13.sp, lineHeight = 19.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("输出会带入 AI 玄师输入框，不会自动发送。", color = Jade, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { onAiPrompt(buildGuidePrompt(option)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                    ) {
                        Text("问 AI 玄师", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun buildGuidePrompt(option: GuideOption): String = buildString {
    appendLine("【今日问玄 · 玄门向导】")
    appendLine("用户关注：${option.title}")
    appendLine("推荐路线：${option.routeHint}")
    appendLine("建议步骤：")
    option.steps.forEachIndexed { index, step -> appendLine("${index + 1}. $step") }
    appendLine()
    appendLine(option.aiTask)
    appendLine("请按：传统文化解释、现实建议、需要补充的信息、今日印记一句话，四段输出。")
    appendLine("请保持温和，不做绝对承诺，不制造焦虑，重大事项提醒用户理性判断。")
}
