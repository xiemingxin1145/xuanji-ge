package com.aning.xuanxue.feature.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aning.xuanxue.ui.*
import kotlinx.coroutines.launch
import kotlin.math.*

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val body: String,
    val accentColor: Color,
    val symbol: String
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "最后一个道士",
        subtitle = "道法自然 · 伏魔除妖",
        body = "乱世将至，妖魔横行。\n\n你是玄机阁最后一位传人，游走于现代都市的光怪陆离之间，以古老的术法维系着三界的平衡。",
        accentColor = Color(0xFFD62828),
        symbol = "☯"
    ),
    OnboardingPage(
        title = "三才推演",
        subtitle = "天时 · 地利 · 人和",
        body = "以黄历感应天时，以罗盘定位地利，以八字通晓人和。\n\n三才齐备，玄机共鸣激发——凶险之事，方有胜算。",
        accentColor = Color(0xFFFFD700),
        symbol = "卦"
    ),
    OnboardingPage(
        title = "接案伏魔",
        subtitle = "阴阳录 · 志怪卷宗",
        body = "委托人从四面八方而来，带着各自的执念与冤屈。\n\n每一桩案件，都是一段真实的民俗传说。破案之道，在于理解而非消灭。",
        accentColor = Color(0xFF7B2FBE),
        symbol = "鬼"
    ),
    OnboardingPage(
        title = "道行修炼",
        subtitle = "凡人 → 真仙",
        body = "捉鬼积累鬼气，结案获得声望，使用法器精进五行。\n\n从凡人到练气，从筑基到金丹——每一步境界突破，都有新的天地。",
        accentColor = Color(0xFF00FF9C),
        symbol = "仙"
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage
    val page = onboardingPages[currentPage]

    val inf = rememberInfiniteTransition(label = "ob")
    val rot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Restart), label = "obr")

    Box(Modifier.fillMaxSize().background(Color(0xFF050510))) {
        // 背景符文旋转
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f; val cy = size.height * 0.35f; val r = size.minDimension * 0.38f
            drawCircle(page.accentColor.copy(alpha = 0.06f), r * 1.8f, Offset(cx, cy))
            rotate(rot, Offset(cx, cy)) {
                repeat(8) { i ->
                    val a = Math.toRadians(i * 45.0)
                    drawLine(
                        page.accentColor.copy(alpha = 0.12f),
                        Offset(cx, cy),
                        Offset(cx + r * cos(a).toFloat(), cy + r * sin(a).toFloat()), 1f
                    )
                }
                drawCircle(page.accentColor.copy(alpha = 0.08f), r * 0.55f, Offset(cx, cy))
            }
        }

        Column(Modifier.fillMaxSize()) {
            // 大符文标志
            Box(
                Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(currentPage, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "symbol") { idx ->
                    Text(
                        onboardingPages[idx].symbol,
                        fontSize = 96.sp,
                        color = onboardingPages[idx].accentColor.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Thin
                    )
                }
            }

            // 分页内容
            HorizontalPager(pagerState, Modifier.weight(1f)) { idx ->
                val p = onboardingPages[idx]
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(p.title, color = p.accentColor, fontSize = 28.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(6.dp))
                    Text(p.subtitle, color = p.accentColor.copy(alpha = 0.6f), fontSize = 13.sp,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, p.accentColor.copy(alpha = 0.25f))
                    ) {
                        Text(p.body, color = TextMain, fontSize = 15.sp, lineHeight = 26.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
                    }
                }
            }

            // 底部导航
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 页码指示器
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(onboardingPages.size) { i ->
                        val w by animateDpAsState(if (i == currentPage) 24.dp else 8.dp, label = "dot")
                        Box(Modifier.height(8.dp).width(w).clip(RoundedCornerShape(4.dp))
                            .background(if (i == currentPage) page.accentColor else page.accentColor.copy(alpha = 0.25f)))
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (currentPage > 0) {
                        OutlinedButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(currentPage - 1) } },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, page.accentColor.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("上一步", color = TextSub)
                        }
                    } else { Spacer(Modifier.weight(1f)) }
                    Button(
                        onClick = {
                            if (currentPage < onboardingPages.size - 1) {
                                scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                            } else { onDone() }
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = page.accentColor.copy(alpha = 0.2f),
                            contentColor = page.accentColor),
                        border = BorderStroke(1.dp, page.accentColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            if (currentPage == onboardingPages.size - 1) "踏上道途 ✦" else "下一步",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (currentPage < onboardingPages.size - 1) {
                    Spacer(Modifier.height(12.dp))
                    Text("跳过", color = TextSub, fontSize = 12.sp,
                        modifier = Modifier.clickable(onClick = onDone))
                }
            }
        }
    }
}
