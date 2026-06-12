package com.aning.xuanxue.feature.daily

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aning.xuanxue.core.store.PlayerViewModel
import com.aning.xuanxue.feature.cultivation.*
import com.aning.xuanxue.ui.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

data class DailyTask(
    val id: String,
    val title: String,
    val desc: String,
    val xpReward: Long,
    val lingqiReward: Int,
    val icon: String,
    val checkCondition: (PlayerSave) -> Boolean
)

val dailyTasks = listOf(
    DailyTask("dt_almanac", "查阅今日黄历", "打开黄历查看今日宜忌", 10L, 20, "📅") { save ->
        "almanac" in save.toolsUsedSet
    },
    DailyTask("dt_compass", "以罗盘定位天地", "使用罗盘感知方位", 10L, 15, "🧭") { save ->
        "compass" in save.toolsUsedSet
    },
    DailyTask("dt_iching", "起卦问今日吉凶", "以易经推演一卦", 15L, 10, "☯") { save ->
        "iching" in save.toolsUsedSet
    },
    DailyTask("dt_ghost", "捉住一只鬼魂", "进入捉鬼模式捉住任意鬼魂", 30L, 0, "👻") { save ->
        save.ghostsCaught > 0
    },
    DailyTask("dt_resonance", "触发玄机共鸣", "三才齐备，触发共鸣感应", 20L, 25, "✦") { save ->
        save.epicResonanceCount > 0
    }
)

@Composable
fun DailyTaskScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val playerVm: PlayerViewModel = viewModel()
    val save by playerVm.playerSave.collectAsState()
    val checkedIn by playerVm.todayCheckedIn.collectAsState()

    val today = remember {
        SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINESE).format(Date())
    }

    val inf = rememberInfiniteTransition(label = "daily")
    val glow by inf.animateFloat(0.3f, 0.8f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "dg")

    XScaffold(title = "每日修行", onBack = onBack) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // 日期与签到
            Surface(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0A0510),
                border = BorderStroke(1.5.dp, GoldBright.copy(alpha = glow))
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(today, color = TextSub, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (checkedIn) "今日已签到 ✦" else "点击领取每日灵气",
                        color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { if (!checkedIn) playerVm.onDailyCheckIn() },
                        enabled = !checkedIn,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldBright.copy(alpha = 0.15f),
                            contentColor = GoldBright,
                            disabledContainerColor = Color.White.copy(alpha = 0.05f),
                            disabledContentColor = TextSub
                        ),
                        border = BorderStroke(1.dp, GoldBright.copy(alpha = if (checkedIn) 0.2f else 0.7f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            if (checkedIn) "已领取今日灵气" else "☽ 签到领取 50~120 灵气",
                            fontSize = 15.sp, modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // 今日任务列表
            Text("今日修行任务", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            dailyTasks.forEach { task ->
                val done = task.checkCondition(save)
                DailyTaskCard(task, done, onNavigate)
            }

            // 连签奖励（7日连签）
            val streak = calculateStreak(save)
            StreakCard(streak)

            // 功德栏（今日全完成）
            val allDone = dailyTasks.all { it.checkCondition(save) }
            AnimatedVisibility(allDone, enter = fadeIn() + slideInVertically()) {
                Surface(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    color = Jade.copy(alpha = 0.1f),
                    border = BorderStroke(1.5.dp, Jade.copy(alpha = 0.7f))
                ) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("☯ 今日修行功德圆满", color = Jade, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("五行均衡，天地感应，道法自然", color = TextSub, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DailyTaskCard(task: DailyTask, done: Boolean, onNavigate: (String) -> Unit) {
    val route = when (task.id) {
        "dt_almanac"    -> "almanac"
        "dt_compass"    -> "compass"
        "dt_iching"     -> "iching"
        "dt_ghost"      -> "ghost_hunt"
        "dt_resonance"  -> "xuanji_resonance_demo"
        else -> null
    }
    Surface(
        Modifier.fillMaxWidth().clickable(enabled = !done && route != null) { route?.let(onNavigate) },
        shape = RoundedCornerShape(14.dp),
        color = if (done) Jade.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, if (done) Jade.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f))
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(task.icon, fontSize = 24.sp, modifier = Modifier.width(36.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, color = if (done) TextSub else TextMain, fontSize = 15.sp,
                    fontWeight = if (done) FontWeight.Normal else FontWeight.SemiBold)
                Text(task.desc, color = TextSub, fontSize = 11.sp)
            }
            if (done) {
                Text("✓", color = Jade, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            } else {
                Column(horizontalAlignment = Alignment.End) {
                    if (task.xpReward > 0) Text("+${task.xpReward}修为", color = Gold, fontSize = 10.sp)
                    if (task.lingqiReward > 0) Text("+${task.lingqiReward}灵气", color = Color(0xFF4FC3F7), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun StreakCard(streak: Int) {
    XCard(Modifier.fillMaxWidth()) {
        Text("连日修行", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val days = listOf("一" to 10, "二" to 20, "三" to 35, "四" to 50, "五" to 70, "六" to 100, "七" to 200)
            days.forEachIndexed { i, (label, reward) ->
                val reached = i < streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(34.dp).clip(RoundedCornerShape(8.dp))
                            .background(if (reached) GoldBright.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (reached) "✦" else label, color = if (reached) GoldBright else TextSub, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(3.dp))
                    Text("+$reward", color = if (reached) GoldBright else TextSub, fontSize = 9.sp)
                    Text("灵气", color = TextSub, fontSize = 8.sp)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("已连续修行 $streak 日", color = TextSub, fontSize = 11.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
    }
}

private fun calculateStreak(save: PlayerSave): Int = save.casesCompleted.coerceAtMost(7)
