package com.aning.xuanxue.core.art

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

/**
 * 玄机阁 · 美术显示组件（P0 基础设施）
 *
 * 三个组件全部内建 fallback：
 *   有美术资源 → 显示真实图片
 *   无资源     → 回退到现有的程序化/占位渲染，绝不炸 UI、绝不留黑
 *
 * 页面只管调用，传 id 即可。换图、补图都在 ArtAssets 集中处理。
 */

// ─────────────────────────────────────────────
// 1. 背景图：有图显图，无图回退渐变/自定义占位
// ─────────────────────────────────────────────
@Composable
fun XuanBackgroundImage(
    caseId: String,
    modifier: Modifier = Modifier,
    fallback: @Composable BoxScope.() -> Unit = { DefaultDarkBackdrop() }
) {
    val res = ArtAssets.caseBackgroundRes(caseId)
    Box(modifier.fillMaxSize()) {
        if (res != null) {
            Image(
                painter = painterResource(res),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            fallback()
        }
    }
}

/** 缺省深色氛围背景（替代"黑乎乎"）——墨蓝渐变 + 暗角 */
@Composable
fun BoxScope.DefaultDarkBackdrop() {
    Box(
        Modifier
            .matchParentSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A1428),
                        Color(0xFF0D0A14),
                        Color(0xFF060409)
                    )
                )
            )
    )
}

// ─────────────────────────────────────────────
// 2. 鬼怪立绘：有图显透明PNG（带飘动+闪烁），无图回退Canvas占位
// ─────────────────────────────────────────────
@Composable
fun GhostArtwork(
    ghostId: String,
    modifier: Modifier = Modifier,
    /** 是否未揭晓（任务①：捉鬼战斗中显示朦胧轮廓而非清晰立绘）*/
    obscured: Boolean = false,
    fallback: @Composable BoxScope.() -> Unit = { GhostSilhouettePlaceholder() }
) {
    val res = ArtAssets.ghostRes(ghostId)

    // 鬼魂飘动：缓慢上下浮动 + 明暗呼吸
    val transition = rememberInfiniteTransition(label = "ghost")
    val floatY by transition.animateFloat(
        initialValue = -6f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "floatY"
    )
    val breath by transition.animateFloat(
        initialValue = 0.62f, targetValue = 0.92f,
        animationSpec = infiniteRepeatable(tween(1900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breath"
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        if (res != null) {
            Image(
                painter = painterResource(res),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = floatY.dp)
                    .alpha(if (obscured) breath * 0.55f else breath)
            )
        } else {
            fallback()
        }
    }
}

/** 缺省鬼影占位——朦胧惨白椭圆轮廓 */
@Composable
fun BoxScope.GhostSilhouettePlaceholder() {
    val transition = rememberInfiniteTransition(label = "ph")
    val a by transition.animateFloat(
        0.25f, 0.5f,
        infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "a"
    )
    Box(
        Modifier
            .align(Alignment.Center)
            .size(140.dp, 200.dp)
            .alpha(a)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFB8C4D0),
                        Color(0xFF6A7480).copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
    )
}

// ─────────────────────────────────────────────
// 3. 法器图标：有图显图，无图回退 emoji
// ─────────────────────────────────────────────
@Composable
fun ToolIcon(
    toolId: String,
    fallbackEmoji: String,
    modifier: Modifier = Modifier,
    size: Int = 48
) {
    val res = ArtAssets.toolRes(toolId)
    Box(modifier.size(size.dp), contentAlignment = Alignment.Center) {
        if (res != null) {
            Image(
                painter = painterResource(res),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(fallbackEmoji, fontSize = (size * 0.6).sp)
        }
    }
}
