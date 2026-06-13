package com.aning.xuanxue.feature.ghost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.art.ArtAssets
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.TextSub
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * 捉鬼成功揭晓演出（四阶段）：
 *   ① 场景背景淡入  ② 透明鬼影浮现  ③ 金色封印法阵爆发  ④ 真名揭晓
 *
 * 只读 ArtAssets，不改捉鬼状态机。缺图时各阶段自动跳过，不炸 UI。
 */
@Composable
fun GhostRevealCard(
    ghost: GhostType,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableStateOf(0) }
    LaunchedEffect(ghost.id) {
        phase = 0
        delay(150);  phase = 1
        delay(900);  phase = 2
        delay(700);  phase = 3
    }

    val sceneRes = ArtAssets.ghostSceneRes(ghost.id)
    val silhouetteRes = ArtAssets.ghostSilhouetteRes(ghost.id)

    val ghostAlpha by animateFloatAsState(
        targetValue = if (phase >= 1) (if (phase >= 2) 1f else 0.7f) else 0f,
        animationSpec = tween(800), label = "ga"
    )
    val ghostFloat by animateFloatAsState(
        targetValue = if (phase >= 1) 0f else 40f,
        animationSpec = tween(900, easing = EaseOutCubic), label = "gf"
    )
    val sealScale by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 120f), label = "ss"
    )
    val sealAlpha by animateFloatAsState(
        targetValue = if (phase == 2) 1f else if (phase >= 3) 0.35f else 0f,
        animationSpec = tween(500), label = "sa"
    )
    val spin by rememberInfiniteTransition(label = "spin").animateFloat(
        0f, 360f, infiniteRepeatable(tween(8000, easing = LinearEasing)), label = "sp"
    )

    Surface(
        modifier = modifier.fillMaxWidth().height(420.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0D0A14),
        border = BorderStroke(2.dp, ghost.rarity.color.copy(alpha = 0.6f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            // ① 场景背景
            if (sceneRes != null) {
                Image(
                    painter = painterResource(sceneRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.55f)
                )
            }
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(
                    Color(0x990D0A14), Color(0x550D0A14), Color(0xCC0D0A14)
                ))
            ))

            // ③ 封印法阵
            if (sealScale > 0f) {
                Canvas(Modifier.fillMaxSize()) {
                    val c = Offset(size.width/2, size.height/2)
                    val R = size.minDimension * 0.42f * sealScale
                    rotate(spin, c) {
                        drawCircle(GoldBright.copy(alpha = sealAlpha*0.8f), R, c, style = Stroke(3f))
                        drawCircle(GoldBright.copy(alpha = sealAlpha*0.5f), R*0.75f, c, style = Stroke(2f))
                        for (k in 0 until 8) {
                            val ang = Math.toRadians(k * 45.0)
                            drawLine(
                                GoldBright.copy(alpha = sealAlpha*0.6f),
                                Offset(c.x + R*0.75f*sin(ang).toFloat(), c.y - R*0.75f*cos(ang).toFloat()),
                                Offset(c.x + R*sin(ang).toFloat(), c.y - R*cos(ang).toFloat()),
                                strokeWidth = 2f
                            )
                        }
                    }
                }
            }

            // ② 鬼影浮现
            if (silhouetteRes != null) {
                Image(
                    painter = painterResource(silhouetteRes),
                    contentDescription = ghost.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxHeight(0.82f)
                        .offset(y = ghostFloat.dp)
                        .alpha(ghostAlpha)
                )
            }

            // ④ 真名揭晓
            AnimatedVisibility(
                visible = phase >= 3,
                enter = fadeIn(tween(500)) + expandVertically(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    Modifier.fillMaxWidth().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xEE0D0A14)))
                    ).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✦ 镇 压 成 功 ✦", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(ghost.name, color = ghost.rarity.color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(ghost.alias, color = TextSub, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF00FF9C).copy(alpha = 0.15f)) {
                            Text("鬼气 +${ghost.dropEssence}", color = Color(0xFF00FF9C), fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = ghost.rarity.color.copy(alpha = 0.15f)) {
                            Text("修为 +${ghost.dropXp}", color = ghost.rarity.color, fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
