package com.aning.xuanxue.feature.talisman

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.InkSurface
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold
import kotlin.random.Random

private data class TalismanCard(
    val name: String,
    val seal: String,
    val element: String,
    val meaning: String,
    val action: String
)

private val cards = listOf(
    TalismanCard("静心卡", "静", "水", "放慢节奏，收摄心神，减少情绪消耗。", "今晚睡前整理桌面，做三轮缓慢呼吸。"),
    TalismanCard("守宅卡", "宅", "土", "重视家庭空间、边界与秩序。", "清理玄关、检查门窗，把杂物归位。"),
    TalismanCard("开明卡", "明", "火", "打开思路，照亮目标，把复杂事拆小。", "列出今天最重要的三件事，先做第一件。"),
    TalismanCard("和合卡", "和", "木", "改善沟通，减少对抗，给关系留余地。", "重要对话先说事实，再说感受，少下结论。"),
    TalismanCard("定金卡", "定", "金", "收束心神，做取舍，避免被噪音牵着走。", "关掉一个干扰源，专注二十五分钟。")
)

@Composable
fun TalismanScreen(
    onBack: () -> Unit,
    onAiPrompt: (String) -> Unit
) {
    var current by remember { mutableStateOf(cards[0]) }

    XScaffold(title = "今日符卡", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("抽一张今日符卡")
                Spacer(Modifier.height(8.dp))
                Text(
                    "符卡是传统符号文化与生活提醒的结合，不宣称神秘效力。它更像每日行动卡。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(InkSurface),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize().padding(24.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = kotlin.math.min(cx, cy) * 0.88f
                    drawCircle(Gold.copy(alpha = 0.16f), radius = r)
                    drawCircle(Gold.copy(alpha = 0.28f), radius = r * 0.76f)
                    rotate(45f) {
                        drawRect(Cinnabar.copy(alpha = 0.16f), size = size)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(current.seal, color = Cinnabar, fontSize = 88.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(current.name, color = GoldBright, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("五行：${current.element}", color = Jade, fontSize = 13.sp)
                }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("符意")
                Spacer(Modifier.height(8.dp))
                Text(current.meaning, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
                Spacer(Modifier.height(12.dp))
                Text("今日行动：${current.action}", color = GoldBright, fontSize = 14.sp, lineHeight = 22.sp)
                Spacer(Modifier.height(8.dp))
                Text("传统文化娱乐参考，请以现实行动和理性判断为主。", color = TextSub, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { current = cards[Random.nextInt(cards.size)] },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                ) { Text("再抽一张") }
                Button(
                    onClick = { onAiPrompt(buildTalismanPrompt(current)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                ) { Text("问 AI 玄师") }
            }

            Text(
                "可作为后续“个人印记”的入口：每天抽卡、收藏、记录行动反馈。",
                color = TextSub,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun buildTalismanPrompt(card: TalismanCard): String = buildString {
    appendLine("【今日符卡】")
    appendLine("卡名：${card.name}")
    appendLine("符字：${card.seal}")
    appendLine("五行：${card.element}")
    appendLine("符意：${card.meaning}")
    appendLine("今日行动：${card.action}")
    appendLine()
    appendLine("请基于这张符卡，生成一段温和的今日提醒、现实行动建议和一句今日印记。")
    appendLine("请不要做绝对承诺，不制造焦虑，重大事项提醒用户理性判断。")
}
