package com.aning.xuanxue.feature.home


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.*
import java.util.*

/**
 * 玄机阁 V2.0 动态玄黄天盘首页
 * 四层宇宙盘：天层（星空） + 地层（山川） + 人层（命格） + 玄层（神话）
 * 零自主实现第一版骨架
 */
@Composable
fun XuanHuangTianPanScreen(
    onNavigateToResonanceDemo: () -> Unit = {},
    onNavigateToCompass: () -> Unit = {},
    onNavigateToBazi: () -> Unit = {},
    onNavigateToAlmanac: () -> Unit = {},
    onNavigateToAi: () -> Unit = {}
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "tianpan")

    // 天层 - 星轨缓慢旋转
    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(120000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starRot"
    )

    // 简单玄气值演示（后期替换为真实计算）
    val demoXuanQi = remember {
        XuanjiResonance.calculate(
            tianShiRaw = 82,
            diLiRaw = 71,
            renHeRaw = 65
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        // ========== 天层：宇宙星空 ==========
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // 深空背景渐变
            drawRect(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A0E1A),
                        Color(0xFF05070F)
                    ),
                    center = Offset(cx, cy * 0.6f),
                    radius = size.width
                )
            )

            // 星轨 + 星辰（简化版）
            rotate(starRotation * 0.3f, pivot = Offset(cx, cy)) {
                for (i in 0 until 48) {
                    val angle = i * 7.5f
                    val radius = 120f + (i % 5) * 28f
                    val x = cx + kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * radius
                    val y = cy * 0.55f + kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat() * radius * 0.6f
                    drawCircle(
                        color = Gold.copy(alpha = 0.35f + (i % 3) * 0.12f),
                        radius = 1.8f + (i % 4) * 0.6f,
                        center = Offset(x, y)
                    )
                }
            }

            // 流年飞星（简化动态点）
            val flyX = cx + kotlin.math.cos((starRotation * 0.8f % 360) * Math.PI / 180).toFloat() * 180f
            val flyY = cy * 0.5f + kotlin.math.sin((starRotation * 0.8f % 360) * Math.PI / 180).toFloat() * 80f
            drawCircle(GoldBright.copy(alpha = 0.9f), radius = 3.5f, center = Offset(flyX, flyY))
        }

        // ========== 地层：山川地脉（简化轮廓） ==========
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val bottomY = size.height * 0.78f

            // 山脉轮廓
            val path = Path().apply {
                moveTo(0f, bottomY)
                lineTo(cx * 0.25f, bottomY - 140f)
                lineTo(cx * 0.45f, bottomY - 80f)
                lineTo(cx * 0.62f, bottomY - 190f)
                lineTo(cx * 0.78f, bottomY - 95f)
                lineTo(size.width, bottomY - 160f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path,
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A2A3A).copy(alpha = 0.85f),
                        Color(0xFF0F1A25)
                    )
                )
            )

            // 地脉发光线（简化）
            for (i in 0 until 5) {
                val x = cx * (0.2f + i * 0.15f)
                drawLine(
                    color = Jade.copy(alpha = 0.25f + (i % 2) * 0.15f),
                    start = Offset(x, bottomY - 60f - i * 12f),
                    end = Offset(x + 40f + i * 8f, bottomY + 30f),
                    strokeWidth = 2.5f
                )
            }
        }

        // ========== 内容层 ==========
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 80.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // 主标题
            Text(
                "今日玄象",
                color = GoldBright,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Text(
                "星河流转，地脉微明。你的命格正在与天地共鸣。",
                color = TextSub,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(32.dp))

            // 玄气值卡片
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = InkSurface,
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f))
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("今日玄气值", color = TextSub, fontSize = 13.sp)
                    Text(
                        "${demoXuanQi.total}",
                        color = GoldBright,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        demoXuanQi.mythicDesc,
                        color = TextMain,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        demoXuanQi.gameEffect,
                        color = Jade,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 快速入口（后期替换为动态按钮）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickEntryCard("寻龙定脉", "罗盘", onNavigateToCompass)
                QuickEntryCard("命格鉴定", "八字", onNavigateToBazi)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickEntryCard("天时神谕", "黄历", onNavigateToAlmanac)
                QuickEntryCard("师尊问道", "AI玄师", onNavigateToAi)
            }

            Spacer(Modifier.height(40.dp))

            Text(
                "V2.0 · 天地玄黄 · 动态天盘测试版",
                color = TextSub.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun QuickEntryCard(
    title: String,
    sub: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .height(92.dp),
        shape = RoundedCornerShape(16.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = GoldBright, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Text(sub, color = TextSub, fontSize = 12.sp)
        }
    }
}