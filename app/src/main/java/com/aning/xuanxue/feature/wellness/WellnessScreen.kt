package com.aning.xuanxue.feature.wellness

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.aning.xuanxue.ui.KV
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold
import com.nlf.calendar.Solar
import java.util.Calendar

private data class WellnessDay(
    val element: String,
    val colorHint: String,
    val breath: String,
    val body: String,
    val emotion: String,
    val food: String,
    val action: String
)

@Composable
fun WellnessScreen(
    onBack: () -> Unit,
    onAiPrompt: (String) -> Unit
) {
    val info = remember { todayWellness() }
    val solarInfo = remember { todayText() }

    XScaffold(title = "五行养生日课", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日五行提醒")
                Spacer(Modifier.height(8.dp))
                Text(
                    "把节气、五行、作息和情绪调节做成每日小日课。仅作传统文化与生活方式参考。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(solarInfo, color = GoldBright, fontSize = 13.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(InkSurface),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize().padding(24.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = kotlin.math.min(cx, cy) * 0.78f
                    drawCircle(Gold.copy(alpha = 0.14f), radius = r)
                    drawCircle(Cinnabar.copy(alpha = 0.12f), radius = r * 0.68f)
                    drawCircle(Gold.copy(alpha = 0.22f), radius = r * 0.38f)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(info.element, color = Cinnabar, fontSize = 72.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("今日偏 ${info.element} · ${info.colorHint}", color = GoldBright, fontSize = 17.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("少一点急躁，多一点照看自己", color = TextSub, fontSize = 12.sp)
                }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日日课")
                Spacer(Modifier.height(8.dp))
                KV("呼吸", info.breath)
                KV("身体", info.body)
                KV("情绪", info.emotion)
                KV("饮食", info.food)
                Spacer(Modifier.height(10.dp))
                Text("今日行动：${info.action}", color = GoldBright, fontSize = 14.sp, lineHeight = 22.sp)
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("三分钟静心")
                Spacer(Modifier.height(8.dp))
                Text("1. 坐稳，肩颈放松。", color = TextMain, fontSize = 13.sp)
                Text("2. 吸气四拍，停一拍，呼气六拍。", color = TextMain, fontSize = 13.sp)
                Text("3. 重复三到五轮，把注意力收回来。", color = TextMain, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("如有身体不适，请停止练习并咨询专业人士。", color = TextSub, fontSize = 11.sp)
            }

            Button(
                onClick = { onAiPrompt(buildWellnessPrompt(info, solarInfo)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
            ) { Text("问 AI 玄师生成今日养生提醒", fontWeight = FontWeight.Bold) }

            Text(
                "本模块不提供医学诊断，不替代医生建议；饮食、运动、作息请结合自身情况。",
                color = TextSub,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun todayWellness(): WellnessDay {
    val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return when (day % 5) {
        0 -> WellnessDay("木", "青绿 · 舒展", "缓慢深呼吸，配合肩颈舒展。", "散步十五分钟，少久坐。", "把堵住的话写下来，不急着发出去。", "清淡蔬果，少油腻。", "整理一个计划，把目标拆成三步。")
        1 -> WellnessDay("火", "朱红 · 明亮", "吸四呼六，降低心火与急躁。", "晒一点自然光，睡前少刷屏。", "先停三秒再回应，避免冲动表达。", "温热适量，少辛辣刺激。", "把今天最重要的话说清楚。")
        2 -> WellnessDay("土", "黄棕 · 稳定", "腹式呼吸，感受身体落地。", "整理房间一角，稳定环境。", "少反复思虑，先完成一件小事。", "规律三餐，少过甜。", "把一件拖延的杂事处理掉。")
        3 -> WellnessDay("金", "白金 · 收束", "短吸长呼，帮助收束注意力。", "清理桌面，减少干扰。", "练习做取舍，不把所有事都抓在手里。", "清润适量，少熬夜。", "关掉一个干扰源，专注二十五分钟。")
        else -> WellnessDay("水", "玄黑 · 静养", "呼气更长，放慢节奏。", "早睡或午间闭目十分钟。", "允许自己慢下来，不急着证明。", "温水少量多次，晚间少寒凉。", "睡前写下今天放下的一件事。")
    }
}

private fun todayText(): String {
    val c = Calendar.getInstance()
    val solar = Solar.fromYmd(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    val lunar = solar.lunar
    return "公历${solar.year}年${solar.month}月${solar.day}日 · 农历${lunar.monthInChinese}月${lunar.dayInChinese} · ${lunar.dayInGanZhi}日"
}

private fun buildWellnessPrompt(info: WellnessDay, solarInfo: String): String = buildString {
    appendLine("【五行养生日课】")
    appendLine("日期：$solarInfo")
    appendLine("今日五行：${info.element}")
    appendLine("色彩提示：${info.colorHint}")
    appendLine("呼吸建议：${info.breath}")
    appendLine("身体建议：${info.body}")
    appendLine("情绪建议：${info.emotion}")
    appendLine("饮食建议：${info.food}")
    appendLine("今日行动：${info.action}")
    appendLine()
    appendLine("请生成一份温和的今日养生提醒，包含传统文化解释、现实作息建议、情绪调节和一句今日印记。")
    appendLine("请不要提供医学诊断，不替代医生建议，不做绝对承诺。")
}
