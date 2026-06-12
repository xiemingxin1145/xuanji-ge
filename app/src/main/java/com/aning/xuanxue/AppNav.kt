package com.aning.xuanxue

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
import com.aning.xuanxue.feature.almanac.AlmanacScreen
import com.aning.xuanxue.feature.bazi.BaziScreen
import com.aning.xuanxue.feature.compass.CompassScreen
import com.aning.xuanxue.feature.iching.IChingScreen
import com.aning.xuanxue.feature.name.NameScreen
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import kotlinx.coroutines.delay
import java.util.Calendar

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "splash") {
        composable("splash") { SplashScreen { nav.navigate("home") { popUpTo("splash") { inclusive = true } } } }
        composable("home") { HomeScreen(nav::navigate) }
        composable("compass") { CompassScreen(onBack = { nav.popBackStack() }) }
        composable("bazi") { BaziScreen(onBack = { nav.popBackStack() }) }
        composable("iching") { IChingScreen(onBack = { nav.popBackStack() }) }
        composable("almanac") { AlmanacScreen(onBack = { nav.popBackStack() }) }
        composable("name") { NameScreen(onBack = { nav.popBackStack() }) }
        composable("ai") {
            AiChatScreen(
                onBack = { nav.popBackStack() },
                onSettings = { nav.navigate("ai_settings") }
            )
        }
        composable("ai_settings") { AiSettingsScreen(onBack = { nav.popBackStack() }) }
    }
}

// ==================== Splash 启动页（玄门仪式感） ====================
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink),
        contentAlignment = Alignment.Center
    ) {
        // 背景暗纹八卦
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val r = min(cx, cy) * 0.72f
            val paint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = Gold.copy(alpha = 0.08f).toArgb()
                strokeWidth = 1.5f
                style = android.graphics.Paint.Style.STROKE
            }
            for (i in 0 until 8) {
                val ang = i * 45f
                rotate(degrees = ang, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                    drawContext.canvas.nativeCanvas.drawCircle(cx, cy, r, paint)
                }
            }
            // 细微同心圆
            for (k in 1..5) {
                drawContext.canvas.nativeCanvas.drawCircle(cx, cy, r * (0.3f + k * 0.12f), paint.apply { alpha = (30 + k * 8) })
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 朱砂印章风格标题
            Text(
                "玄机阁",
                color = Cinnabar,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "XUAN JI GE",
                color = Gold.copy(alpha = 0.7f),
                fontSize = 14.sp,
                letterSpacing = 6.sp
            )
            Spacer(Modifier.height(32.dp))

            // 简化太极
            TaijiSymbol(symbolSize = 92.dp, modifier = Modifier)

            Spacer(Modifier.height(40.dp))
            Text(
                "测着玩 · 图个吉利 · 信则有",
                color = TextSub.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
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
        ), label = "taijiRot"
    )

    Canvas(modifier.size(symbolSize)) {
        val cx = this.size.width / 2
        val cy = this.size.height / 2
        val r = min(cx, cy)

        rotate(degrees = rotation, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
            val paint = android.graphics.Paint().apply { isAntiAlias = true }

            // 太极阴阳
            paint.color = GoldBright.toArgb()
            drawContext.canvas.nativeCanvas.drawCircle(cx, cy, r, paint)

            paint.color = Ink.toArgb()
            val path = android.graphics.Path().apply {
                moveTo(cx, cy - r)
                arcTo(cx - r, cy - r, cx + r, cy + r, -90f, 180f, false)
                close()
            }
            drawContext.canvas.nativeCanvas.drawPath(path, paint)

            // 小圆
            paint.color = GoldBright.toArgb()
            drawContext.canvas.nativeCanvas.drawCircle(cx, cy - r * 0.5f, r * 0.5f, paint)
            paint.color = Ink.toArgb()
            drawContext.canvas.nativeCanvas.drawCircle(cx, cy + r * 0.5f, r * 0.5f, paint)

            paint.color = Cinnabar.toArgb()
            drawContext.canvas.nativeCanvas.drawCircle(cx, cy - r * 0.5f, r * 0.18f, paint)
            drawContext.canvas.nativeCanvas.drawCircle(cx, cy + r * 0.5f, r * 0.18f, paint)
        }
    }
}

// ==================== 首页（升级中央视觉） ====================
private data class Entry(
    val route: String,
    val title: String,
    val sub: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(go: (String) -> Unit) {
    val entries = listOf(
        Entry("compass", "风水罗盘", "二十四山 · 八卦方位", Icons.Filled.Explore),
        Entry("bazi", "八字排盘", "四柱 · 五行 · 十神", Icons.Filled.GridView),
        Entry("iching", "易经起卦", "六十四卦 · 动爻", Icons.Filled.Casino),
        Entry("almanac", "老黄历", "宜忌 · 冲煞 · 吉神", Icons.Filled.MenuBook),
        Entry("name", "姓名五行", "缺补 · 起名参考", Icons.Filled.Spa),
        Entry("ai", "AI 玄师", "可插拔大模型 · 解卦问事", Icons.Filled.AutoAwesome),
    )

    XScaffold(title = "玄机阁") { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // 中央动态太极 + 罗盘视觉（仪式感核心）
            CentralXuanVisual(
                onClick = { go("compass") },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(24.dp))
            )

            TodayHeader()

            // 六宫格
            val rows = entries.chunked(2)
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { e ->
                        GridCard(e, Modifier.weight(1f)) { go(e.route) }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "测着玩 · 图个吉利 · 信则有",
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
private fun CentralXuanVisual(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "homeVisual")
    val rot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "slowRot"
    )

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = InkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().padding(24.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val r = min(cx, cy) * 0.82f

                rotate(degrees = rot, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                    // 外圈暗金
                    val ring = android.graphics.Paint().apply {
                        isAntiAlias = true
                        color = Gold.copy(alpha = 0.25f).toArgb()
                        strokeWidth = 2.5f
                        style = android.graphics.Paint.Style.STROKE
                    }
                    drawContext.canvas.nativeCanvas.drawCircle(cx, cy, r, ring)
                    drawContext.canvas.nativeCanvas.drawCircle(cx, cy, r * 0.78f, ring.apply { strokeWidth = 1.5f; alpha = 60 })

                    // 八卦暗纹
                    val baguaPaint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        color = Gold.copy(alpha = 0.15f).toArgb()
                        textSize = r * 0.09f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val bagua = listOf("乾", "兑", "离", "震", "巽", "坎", "艮", "坤")
                    bagua.forEachIndexed { i, name ->
                        val ang = i * 45f
                        rotate(degrees = ang, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                            drawContext.canvas.nativeCanvas.drawText(name, cx, cy - r * 0.88f + baguaPaint.textSize / 3, baguaPaint)
                        }
                    }

                    // 简化太极核心
                    val taijiPaint = android.graphics.Paint().apply { isAntiAlias = true }
                    taijiPaint.color = GoldBright.toArgb()
                    drawContext.canvas.nativeCanvas.drawCircle(cx, cy, r * 0.42f, taijiPaint)

                    taijiPaint.color = Ink.toArgb()
                    val half = android.graphics.Path().apply {
                        moveTo(cx, cy - r * 0.42f)
                        arcTo(cx - r * 0.42f, cy - r * 0.42f, cx + r * 0.42f, cy + r * 0.42f, -90f, 180f, false)
                        close()
                    }
                    drawContext.canvas.nativeCanvas.drawPath(half, taijiPaint)

                    // 朱砂小点
                    taijiPaint.color = Cinnabar.toArgb()
                    drawContext.canvas.nativeCanvas.drawCircle(cx, cy - r * 0.21f, r * 0.09f, taijiPaint)
                    taijiPaint.color = GoldBright.toArgb()
                    drawContext.canvas.nativeCanvas.drawCircle(cx, cy + r * 0.21f, r * 0.09f, taijiPaint)
                }
            }

            // 叠加文字提示
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(110.dp))
                Text("点击进入风水罗盘", color = Gold.copy(alpha = 0.6f), fontSize = 12.sp)
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
        val yi = lunar.dayYi.take(4).joinToString(" ")
        val ji = lunar.dayJi.take(4).joinToString(" ")
        Triple(
            "${solar.year}-${"%02d".format(solar.month)}-${"%02d".format(solar.day)}  ${solar.weekInChinese.let { "星期$it" }}",
            "农历${lunar.monthInChinese}月${lunar.dayInChinese}  ${lunar.yearInGanZhi}${lunar.yearShengXiao}年 · ${lunar.dayInGanZhi}日",
            yi to ji
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = InkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
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
private fun GridCard(e: Entry, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = InkSurface
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(Gold.copy(alpha = 0.25f), Gold.copy(alpha = 0.08f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(e.icon, contentDescription = e.title, tint = GoldBright)
            }
            Column {
                Text(e.title, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(e.sub, color = TextSub, fontSize = 11.sp)
            }
        }
    }
}
