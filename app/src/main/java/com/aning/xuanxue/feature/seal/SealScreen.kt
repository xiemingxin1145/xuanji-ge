package com.aning.xuanxue.feature.seal

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

private data class SealProfile(
    val dateText: String,
    val dayGanZhi: String,
    val elementSeal: String,
    val baguaSeal: String,
    val todayCard: String,
    val focus: String,
    val dailySeal: String
)

@Composable
fun SealScreen(
    onBack: () -> Unit,
    onAiPrompt: (String) -> Unit
) {
    val profile = remember { todaySealProfile() }

    XScaffold(title = "我的印记", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("玄门档案")
                Spacer(Modifier.height(8.dp))
                Text(
                    "把今日问玄、符卡、梦境、养生日课等记录汇成个人印记。第一版先做今日档案，后续接本地持久化历史。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(InkSurface),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize().padding(24.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = kotlin.math.min(cx, cy) * 0.84f
                    drawCircle(Gold.copy(alpha = 0.13f), radius = r)
                    drawCircle(Cinnabar.copy(alpha = 0.12f), radius = r * 0.72f)
                    drawCircle(Gold.copy(alpha = 0.24f), radius = r * 0.42f)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(profile.dailySeal, color = Cinnabar, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text("今日印记", color = GoldBright, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(profile.dateText, color = TextSub, fontSize = 12.sp)
                }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日档案")
                Spacer(Modifier.height(8.dp))
                KV("日干支", profile.dayGanZhi)
                KV("五行印", profile.elementSeal)
                KV("八卦印", profile.baguaSeal)
                KV("今日符卡", profile.todayCard)
                KV("今日关注", profile.focus)
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("成长记录入口")
                Spacer(Modifier.height(8.dp))
                Text("· 今日符卡：适合记录抽卡与行动反馈", color = TextMain, fontSize = 13.sp)
                Text("· 梦境记录：适合记录梦境、情绪与关键词", color = TextMain, fontSize = 13.sp)
                Text("· 五行养生：适合记录作息、呼吸与情绪状态", color = TextMain, fontSize = 13.sp)
                Text("· 罗盘/卦象：后续接入本地历史记录", color = TextSub, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("下一版可接 DataStore/本地 JSON，把这些记录长期保存。", color = TextSub, fontSize = 12.sp)
            }

            Button(
                onClick = { onAiPrompt(buildSealPrompt(profile)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
            ) { Text("问 AI 玄师解读我的今日印记", fontWeight = FontWeight.Bold) }

            Text(
                "个人印记用于个性化体验和传统文化娱乐参考，不代表确定性预测。",
                color = TextSub,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun todaySealProfile(): SealProfile {
    val c = Calendar.getInstance()
    val solar = Solar.fromYmd(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    val lunar = solar.lunar
    val day = c.get(Calendar.DAY_OF_YEAR)
    val elements = listOf("木印 · 生发", "火印 · 开明", "土印 · 稳定", "金印 · 收束", "水印 · 静养")
    val bagua = listOf("乾印 · 开创", "坤印 · 承载", "震印 · 行动", "巽印 · 入微", "坎印 · 深思", "离印 · 明辨", "艮印 · 止定", "兑印 · 沟通")
    val cards = listOf("静心卡", "守宅卡", "开明卡", "和合卡", "定金卡")
    val focus = listOf("整理秩序", "照看情绪", "打开思路", "稳住节奏", "减少消耗")
    val seals = listOf("守心", "开明", "安宅", "止躁", "归元")
    return SealProfile(
        dateText = "公历${solar.year}年${solar.month}月${solar.day}日 · 农历${lunar.monthInChinese}月${lunar.dayInChinese}",
        dayGanZhi = lunar.dayInGanZhi,
        elementSeal = elements[day % elements.size],
        baguaSeal = bagua[day % bagua.size],
        todayCard = cards[day % cards.size],
        focus = focus[day % focus.size],
        dailySeal = seals[day % seals.size]
    )
}

private fun buildSealPrompt(profile: SealProfile): String = buildString {
    appendLine("【我的今日印记】")
    appendLine("日期：${profile.dateText}")
    appendLine("日干支：${profile.dayGanZhi}")
    appendLine("五行印：${profile.elementSeal}")
    appendLine("八卦印：${profile.baguaSeal}")
    appendLine("今日符卡：${profile.todayCard}")
    appendLine("今日关注：${profile.focus}")
    appendLine("今日印记：${profile.dailySeal}")
    appendLine()
    appendLine("请基于这份今日印记，生成传统文化解释、现实行动建议、情绪提醒和一句适合收藏的印记短句。")
    appendLine("请保持温和，不做绝对承诺，不制造焦虑，重大事项提醒用户理性判断。")
}
