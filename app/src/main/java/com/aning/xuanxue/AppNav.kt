package com.aning.xuanxue

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aning.xuanxue.feature.ai.AiChatScreen
import com.aning.xuanxue.feature.ai.AiSettingsScreen
import com.aning.xuanxue.feature.ai.PendingAiPromptStore
import com.aning.xuanxue.feature.almanac.AlmanacScreen
import com.aning.xuanxue.feature.bazi.BaziScreen
import com.aning.xuanxue.feature.compass.CompassScreen
import com.aning.xuanxue.feature.dream.DreamScreen
import com.aning.xuanxue.feature.flyingstar.FlyingStarScreen
import com.aning.xuanxue.feature.guide.GuideScreen
import com.aning.xuanxue.feature.iching.IChingScreen
import com.aning.xuanxue.feature.knowledge.KnowledgeScreen
import com.aning.xuanxue.feature.mountain.MountainOracleScreen
import com.aning.xuanxue.feature.name.NameScreen
import com.aning.xuanxue.feature.seal.SealScreen
import com.aning.xuanxue.feature.status.FeatureStatusScreen
import com.aning.xuanxue.feature.talisman.TalismanScreen
import com.aning.xuanxue.feature.wellness.WellnessScreen
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.min

@Composable
fun AppNav() {
    val nav = rememberNavController()

    fun openAiWithPrompt(prompt: String) {
        PendingAiPromptStore.set(prompt)
        nav.navigate("ai")
    }

    NavHost(navController = nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                nav.navigate("home") { popUpTo("splash") { inclusive = true } }
            }
        }
        composable("home") { HomeScreen(nav::navigate) }
        composable("status") { FeatureStatusScreen(onBack = { nav.popBackStack() }) }
        composable("guide") { GuideScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("seal") { SealScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("knowledge") { KnowledgeScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("talisman") { TalismanScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("dream") { DreamScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("wellness") { WellnessScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("compass") { CompassScreen(onBack = { nav.popBackStack() }) }
        composable("flyingstar") { FlyingStarScreen(onBack = { nav.popBackStack() }) }
        composable("mountain") {
            MountainOracleScreen(
                onBack = { nav.popBackStack() },
                onOpenFlyingStar = { nav.navigate("flyingstar") }
            )
        }
        composable("bazi") { BaziScreen(onBack = { nav.popBackStack() }) }
        composable("iching") { IChingScreen(onBack = { nav.popBackStack() }) }
        composable("almanac") { AlmanacScreen(onBack = { nav.popBackStack() }) }
        composable("name") { NameScreen(onBack = { nav.popBackStack() }) }
        composable("ai") { AiChatScreen(onBack = { nav.popBackStack() }, onSettings = { nav.navigate("ai_settings") }) }
        composable("ai_settings") { AiSettingsScreen(onBack = { nav.popBackStack() }) }
    }
}

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1600)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = min(cx, cy) * 0.72f
            drawCircle(Gold.copy(alpha = 0.08f), radius = r)
            drawCircle(Gold.copy(alpha = 0.05f), radius = r * 0.68f)
            drawCircle(Cinnabar.copy(alpha = 0.06f), radius = r * 0.42f)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("玄机阁", color = Cinnabar, fontSize = 52.sp, fontWeight = FontWeight.Bold, letterSpacing = 8.sp)
            Spacer(Modifier.height(8.dp))
            Text("XUAN JI GE", color = Gold.copy(alpha = 0.75f), fontSize = 14.sp, letterSpacing = 6.sp)
            Spacer(Modifier.height(32.dp))
            TaijiSymbol(symbolSize = 92.dp)
            Spacer(Modifier.height(36.dp))
            Text("一部手机 · 观天时 · 测地气 · 解人心", color = TextSub.copy(alpha = 0.7f), fontSize = 13.sp)
        }
    }
}

@Composable
fun TaijiSymbol(symbolSize: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "taiji")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "taijiRot"
    )
    Canvas(modifier.size(symbolSize)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = min(cx, cy)
        rotate(rotation) {
            drawCircle(GoldBright, radius = r)
            drawCircle(Ink, radius = r * 0.48f, center = androidx.compose.ui.geometry.Offset(cx, cy - r * 0.5f))
            drawCircle(GoldBright, radius = r * 0.48f, center = androidx.compose.ui.geometry.Offset(cx, cy + r * 0.5f))
            drawCircle(Cinnabar, radius = r * 0.12f, center = androidx.compose.ui.geometry.Offset(cx, cy - r * 0.5f))
            drawCircle(Ink, radius = r * 0.12f, center = androidx.compose.ui.geometry.Offset(cx, cy + r * 0.5f))
        }
    }
}

private data class Entry(
    val route: String,
    val title: String,
    val sub: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(go: (String) -> Unit) {
    val entries = listOf(
        Entry("status", "新版功能清单", "确认最新版 · 测试重点", Icons.Filled.Verified),
        Entry("guide", "玄门向导", "今日问玄 · 先问再测", Icons.Filled.AutoAwesome),
        Entry("seal", "我的印记", "五行印 · 八卦印 · 今日档案", Icons.Filled.AutoAwesome),
        Entry("knowledge", "玄门资料库", "道教 · 民俗 · 五行 · 风水", Icons.Filled.MenuBook),
        Entry("talisman", "今日符卡", "抽卡 · 印记 · 行动提醒", Icons.Filled.AutoAwesome),
        Entry("dream", "梦境记录", "解梦 · 情绪 · 民俗象意", Icons.Filled.NightsStay),
        Entry("wellness", "五行养生", "日课 · 呼吸 · 情绪调节", Icons.Filled.Spa),
        Entry("compass", "风水罗盘", "二十四山 · 八卦方位", Icons.Filled.Explore),
        Entry("flyingstar", "玄空飞星", "三元九运 · 飞星排盘", Icons.Filled.Apps),
        Entry("mountain", "二十四山向", "坐山向首 · 元龙断法", Icons.Filled.Explore),
        Entry("bazi", "八字排盘", "四柱 · 神煞 · 大运流年", Icons.Filled.GridView),
        Entry("iching", "易经起卦", "六十四卦 · 动爻", Icons.Filled.Casino),
        Entry("almanac", "老黄历", "宜忌 · 冲煞 · 吉神", Icons.Filled.CalendarMonth),
        Entry("name", "姓名五行", "缺补 · 起名参考", Icons.Filled.Spa),
        Entry("ai", "AI 玄师", "可插拔大模型 · 解卦问事", Icons.Filled.AutoAwesome)
    )

    XScaffold(title = "玄机阁") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            CentralXuanVisual(onClick = { go("guide") })
            TodayHeader()
            entries.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { entry ->
                        GridCard(entry, Modifier.weight(1f)) { go(entry.route) }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "传统文化娱乐参考 · 现实建议优先",
                color = TextSub,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CentralXuanVisual(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "homeVisual")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(32000, easing = LinearEasing), RepeatMode.Restart),
        label = "homeRot"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.05f)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().padding(24.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = min(cx, cy) * 0.82f
                rotate(rotation, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                    drawCircle(Gold.copy(alpha = 0.16f), radius = r)
                    drawCircle(Gold.copy(alpha = 0.12f), radius = r * 0.72f)
                    drawCircle(Cinnabar.copy(alpha = 0.08f), radius = r * 0.42f)
                    for (i in 0 until 8) {
                        rotate(i * 45f, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                            drawCircle(Gold.copy(alpha = 0.08f), radius = r * 0.04f, center = androidx.compose.ui.geometry.Offset(cx, cy - r * 0.82f))
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("今日问玄", color = GoldBright, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("先问所求 · 再入工具 · 最后问 AI", color = TextSub, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun TodayHeader() {
    val info = remember {
        val c = Calendar.getInstance()
        val solar = Solar.fromYmdHms(
            c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)
        )
        val lunar = solar.lunar
        Triple(
            "${solar.year}-${"%02d".format(solar.month)}-${"%02d".format(solar.day)}  星期${solar.weekInChinese}",
            "农历${lunar.monthInChinese}月${lunar.dayInChinese}  ${lunar.yearInGanZhi}${lunar.yearShengXiao}年 · ${lunar.dayInGanZhi}日",
            lunar.dayYi.take(4).joinToString(" ") to lunar.dayJi.take(4).joinToString(" ")
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(info.first, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(info.second, color = TextMain, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            Row {
                Text("宜 ", color = Jade, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(info.third.first.ifBlank { "—" }, color = TextMain, fontSize = 13.sp)
            }
            Spacer(Modifier.height(2.dp))
            Row {
                Text("忌 ", color = Cinnabar, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(info.third.second.ifBlank { "—" }, color = TextMain, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GridCard(entry: Entry, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = InkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(Gold.copy(alpha = 0.25f), Gold.copy(alpha = 0.08f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(entry.icon, contentDescription = entry.title, tint = GoldBright)
            }
            Column {
                Text(entry.title, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(entry.sub, color = TextSub, fontSize = 11.sp)
            }
        }
    }
}
