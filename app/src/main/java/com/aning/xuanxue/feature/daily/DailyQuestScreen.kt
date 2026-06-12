package com.aning.xuanxue.feature.daily

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.aning.xuanxue.core.state.GameStateViewModel
import com.aning.xuanxue.ui.*
import java.text.SimpleDateFormat
import java.util.*

data class DailyQuest(
    val id: String,
    val title: String,
    val desc: String,
    val target: String,
    val xpReward: Int,
    val essenceReward: Int
)

val todayQuests = listOf(
    DailyQuest("dq_almanac", "观今日天象", "查看今日黄历，感知天时变化", "almanac", 50, 0),
    DailyQuest("dq_compass", "测地脉走向", "使用罗盘感应一次地利方位", "compass", 50, 0),
    DailyQuest("dq_ghost",   "捉鬼一只",   "在捉鬼行动中成功封印任意鬼魂", "ghost_caught", 100, 30),
    DailyQuest("dq_iching",  "问卦一次",   "以易经起卦推演今日运势", "iching", 50, 0),
    DailyQuest("dq_case",    "接案探查",   "进入任一卷宗查阅案件", "case_enter", 80, 20)
)

@Composable
fun DailyQuestScreen(
    viewModel: GameStateViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val save by viewModel.playerSave.collectAsState()
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val dailyAvailable = viewModel.isDailyLingqiAvailable

    XScaffold(title = "每日修炼", onBack = onBack) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // 每日灵气签到
            DailyLingqiCard(dailyAvailable) {
                viewModel.claimDailyLingqi()
            }

            // 今日日期与天气提示
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.04f)) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("今日 $today", color = TextSub, fontSize = 12.sp)
                    Text("境界：${save.realm.display}", color = save.realm.color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("日常任务", color = GoldBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            todayQuests.forEach { quest ->
                val completed = quest.id in (save.completedCaseIds + save.toolsUsedSet + save.caughtGhostIds)
                DailyQuestCard(quest, completed) {
                    onNavigate(quest.target)
                }
            }

            // 今日已获修为统计
            Spacer(Modifier.height(4.dp))
            XCard(Modifier.fillMaxWidth()) {
                Text("今日修炼汇总", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatChip("总修为", save.totalXp.toString(), save.realm.color)
                    StatChip("鬼气", save.ghostEssence.toString(), Color(0xFF00FF9C))
                    StatChip("灵气", save.lingqi.toString(), Color(0xFF4FC3F7))
                    StatChip("声望", save.reputation.toString(), GoldBright)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DailyLingqiCard(available: Boolean, onClaim: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "daily")
    val glow by inf.animateFloat(0.3f, 0.9f,
        infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse), label = "dg")

    Surface(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        color = if (available) Color(0xFF1A1000) else Color.White.copy(alpha = 0.04f),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp, if (available) GoldBright.copy(alpha = glow) else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (available) "✦ 今日灵气未领取" else "今日灵气已领取",
                    color = if (available) GoldBright else TextSub,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(if (available) "每日签到 +50灵气，积少成多" else "明日再来",
                    color = TextSub, fontSize = 12.sp)
            }
            if (available) {
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold.copy(alpha = 0.2f), contentColor = GoldBright
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBright.copy(alpha = 0.7f))
                ) { Text("+50灵气", fontWeight = FontWeight.Bold) }
            } else {
                Text("✓", color = Jade, fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun DailyQuestCard(quest: DailyQuest, completed: Boolean, onGo: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        color = if (completed) Jade.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.04f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (completed) Jade.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (completed) "✦" else "◇",
                color = if (completed) Jade else TextSub, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(quest.title,
                    color = if (completed) Jade else TextMain,
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(quest.desc, color = TextSub, fontSize = 11.sp)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (quest.xpReward > 0)
                        RewardTag("+${quest.xpReward}修为", Gold.copy(alpha = 0.7f), GoldBright)
                    if (quest.essenceReward > 0)
                        RewardTag("+${quest.essenceReward}鬼气", Color(0xFF00FF9C).copy(alpha = 0.3f), Color(0xFF00FF9C))
                }
            }
            if (!completed) {
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = onGo,
                    colors = ButtonDefaults.textButtonColors(contentColor = Cinnabar)
                ) { Text("前往", fontSize = 13.sp) }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSub, fontSize = 10.sp)
    }
}

@Composable
private fun RewardTag(text: String, bg: Color, textColor: Color) {
    Surface(shape = RoundedCornerShape(5.dp), color = bg) {
        Text(text, color = textColor, fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
    }
}
