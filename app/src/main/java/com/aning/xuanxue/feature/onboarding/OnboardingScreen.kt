package com.aning.xuanxue.feature.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aning.xuanxue.ui.*
import kotlin.math.*

private data class OnboardPage(
    val title: String,
    val subtitle: String,
    val body: String,
    val accentColor: Color,
    val symbol: String
)

private val pages = listOf(
    OnboardPage(
        title = "最后一个道士",
        subtitle = "THE LAST TAOIST",
        body = "上古神祇陨落，群魔趁势而起。\n\n世间道法几近失传，只剩你，手持残破的玄机阁牌匾，在现代都市的夹缝中护守最后的秩序。",
        accentColor = Color(0xFFD62828),
        symbol = "☯"
    ),
    OnboardPage(
        title = "接案·调查·结案",
        subtitle = "CASE SYSTEM",
        body = "委托人带着光怪陆离的遭遇找上门来。\n\n旧楼哭声、百年茶庄、深夜路灯……每一个案件都是真实分支叙事。你的选择决定结局。",
        accentColor = Color(0xFF7B2FBE),
        symbol = "☷"
    ),
    OnboardPage(
        title = "三才共鸣",
        subtitle = "TIANSHI · DILI · RENHE",
        body = "黄历定天时，罗盘测地利，八字算人和。\n\n三才数值实时影响你捉鬼的成功率、案件的走向，以及玄机共鸣的等级——从微弱到爆发。",
        accentColor = Color(0xFFFFD700),
        symbol = "☰"
    ),
    OnboardPage(
        title = "捉鬼·图鉴·养成",
        subtitle = "GHOST HUNT",
        body = "游魂、厉鬼、九尾狐魅、古神残骸……\n\n八种出自《山海经》的鬼怪在城市角落游荡。捉住它们，解锁真实的民俗典故，让道行一步步突破境界。",
        accentColor = Color(0xFF00FF9C),
        symbol = "☵"
    ),
    OnboardPage(
        title = "玄机阁，开门迎客",
        subtitle = "BEGIN YOUR JOURNEY",
        body = "道法自然，伏魔除妖。\n\n你已是这个时代最后的道士。\n\n——接下来，一切由你决定。",
        accentColor = Color(0xFFFFD700),
        symbol = "✦"
    )
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val current = pages[page]

    val inf = rememberInfiniteTransition(label = "ob")
    val rot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart), label = "r")
    val pulse by inf.animateFloat(0.4f, 1f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "p")

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // 背景符文
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f; val cy = size.height * 0.35f; val r = size.minDimension * 0.38f
            drawCircle(current.accentColor.copy(alpha = 0.05f), r * 1.8f, Offset(cx, cy))
            rotate(rot, Offset(cx, cy)) {
                repeat(8) { i ->
                    val a = Math.toRadians(i * 45.0)
                    drawLine(current.accentColor.copy(alpha = 0.12f),
                        Offset(cx, cy),
                        Offset(cx + r * cos(a).toFloat(), cy + r * sin(a).toFloat()), 1.5f)
                }
                drawCircle(current.accentColor.copy(alpha = 0.18f), r, Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f))
            }
        }

        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(60.dp))

            // 符号
            AnimatedContent(current.symbol, label = "sym",
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) }
            ) { sym ->
                Text(sym, color = current.accentColor.copy(alpha = pulse),
                    fontSize = 72.sp, fontWeight = FontWeight.Thin)
            }

            Spacer(Modifier.height(24.dp))

            // 内容区
            AnimatedContent(page, label = "content",
                transitionSpec = { (fadeIn(tween(500)) + slideInHorizontally { it / 3 }) togetherWith
                        (fadeOut(tween(300)) + slideOutHorizontally { -it / 3 })
                }
            ) { _ ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(current.title, color = current.accentColor,
                        fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(current.subtitle, color = TextSub, fontSize = 12.sp, letterSpacing = 3.sp)
                    Spacer(Modifier.height(24.dp))
                    Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.05f)) {
                        Text(current.body, color = TextMain, fontSize = 15.sp,
                            lineHeight = 26.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // 进度点
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.indices.forEach { i ->
                        Box(Modifier.size(if (i == page) 20.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (i == page) current.accentColor else Color.White.copy(alpha = 0.2f)))
                    }
                }

                // 按钮
                Button(
                    onClick = { if (page < pages.size - 1) page++ else onComplete() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = current.accentColor.copy(alpha = 0.2f),
                        contentColor = current.accentColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, current.accentColor)
                ) {
                    Text(if (page < pages.size - 1) "下一页" else "开始修行",
                        fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }

                if (page < pages.size - 1) {
                    TextButton(onClick = onComplete) {
                        Text("跳过", color = TextSub, fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
