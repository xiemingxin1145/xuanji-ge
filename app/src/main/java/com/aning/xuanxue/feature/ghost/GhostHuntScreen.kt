package com.aning.xuanxue.feature.ghost

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

// ─── 捉鬼状态机 ──────────────────────────────────
sealed class HuntState {
    object Idle : HuntState()
    data class Spawned(val ghost: GhostType, val pos: Offset, val hp: Float = 1f) : HuntState()
    data class Locked(val ghost: GhostType, val pos: Offset, val sealCharge: Float = 0f) : HuntState()
    data class Caught(val ghost: GhostType, val isRare: Boolean) : HuntState()
    data class Fled(val ghost: GhostType) : HuntState()
}

@Composable
fun GhostHuntScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var huntState by remember { mutableStateOf<HuntState>(HuntState.Idle) }
    var totalCaught by remember { mutableStateOf(0) }
    var ghostEssence by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("感应天地气息，等待鬼魂显形……") }
    var resonance by remember { mutableStateOf(XuanjiResonance.calculate(60, 55, 50)) }

    val screenW = 360f; val screenH = 500f

    // 自动生成鬼魂
    LaunchedEffect(huntState) {
        if (huntState is HuntState.Idle) {
            delay((3000L..7000L).random())
            val pool = GhostRegistry.all.filter { it.catchDifficulty <= 5 }
            val ghost = pool.random()
            val pos = Offset(
                Random.nextFloat() * 280f + 40f,
                Random.nextFloat() * 300f + 80f
            )
            huntState = HuntState.Spawned(ghost, pos)
            message = "感应到【${ghost.name}】！快速点击锁定！"
            XuanSound.play(context, XuanSound.Effect.Click)
            // 鬼魂10秒后逃跑
            scope.launch {
                delay(10000L)
                if (huntState is HuntState.Spawned) {
                    huntState = HuntState.Fled(ghost)
                    message = "【${ghost.name}】消散了……再等等"
                    delay(2000)
                    huntState = HuntState.Idle
                }
            }
        }
    }

    // 锁定后自动充能
    LaunchedEffect(huntState) {
        if (huntState is HuntState.Locked) {
            val locked = huntState as HuntState.Locked
            val difficulty = locked.ghost.catchDifficulty
            val chargeSpeed = (10 - difficulty) * 0.008f + 0.004f
            while (huntState is HuntState.Locked) {
                delay(50)
                val cur = huntState as? HuntState.Locked ?: break
                val newCharge = cur.sealCharge + chargeSpeed * (resonance.multiplier * 0.4f)
                if (newCharge >= 1f) {
                    val caught = Random.nextFloat() < (0.5f + resonance.total * 0.004f)
                    huntState = if (caught) {
                        XuanSound.play(context, XuanSound.Effect.Open)
                        HuntState.Caught(cur.ghost, cur.ghost.rarity.ordinal >= 2)
                    } else {
                        HuntState.Fled(cur.ghost)
                    }
                } else {
                    huntState = cur.copy(sealCharge = newCharge)
                }
            }
            if (huntState is HuntState.Caught) {
                val caught = huntState as HuntState.Caught
                totalCaught++
                ghostEssence += caught.ghost.dropEssence
                message = "✦ 成功镇压【${caught.ghost.name}】！获得鬼气 ×${caught.ghost.dropEssence}"
                delay(2500)
                huntState = HuntState.Idle
                message = "感应天地气息，等待鬼魂显形……"
            } else if (huntState is HuntState.Fled) {
                val fled = huntState as HuntState.Fled
                message = "【${fled.ghost.name}】挣脱了！符咒能量不足……"
                delay(2000)
                huntState = HuntState.Idle
            }
        }
    }

    Box(
        Modifier.fillMaxSize().background(Color(0xFF050510))
    ) {
        // 捉鬼游玩区
        GhostHuntCanvas(
            huntState = huntState,
            resonance = resonance,
            onTapGhost = { pos ->
                val state = huntState
                if (state is HuntState.Spawned) {
                    val dx = state.pos.x - pos.x; val dy = state.pos.y - pos.y
                    if (sqrt(dx*dx + dy*dy) < 80f) {
                        huntState = HuntState.Locked(state.ghost, state.pos)
                        message = "锁定！正在凝聚符咒能量……"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(480.dp).padding(top = 80.dp)
        )

        // 顶栏
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.07f)) {
                    Text("← 返回", color = TextSub, fontSize = 13.sp,
                        modifier = Modifier.clickable(onClick = onBack).padding(horizontal = 12.dp, vertical = 6.dp))
                }
                Text("捉鬼行动", color = Color(0xFF00FF9C), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Column(horizontalAlignment = Alignment.End) {
                    Text("已捉：$totalCaught", color = GoldBright, fontSize = 13.sp)
                    Text("鬼气：$ghostEssence", color = Color(0xFF00FF9C), fontSize = 11.sp)
                }
            }

            // 玄机共鸣状态条
            Surface(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color(0xFF7B2FBE).copy(alpha = 0.4f))
            ) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("玄机共鸣：", color = TextSub, fontSize = 11.sp)
                    Text(resonance.level.displayName, color = Color(0xFF7B2FBE), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text("捕获率 ×${String.format("%.1f", 0.5f + resonance.total * 0.004f)}", color = TextSub, fontSize = 11.sp)
                }
            }
        }

        // 底部信息栏
        Column(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 当前鬼怪信息
            val state = huntState
            if (state is HuntState.Locked || state is HuntState.Spawned) {
                val ghost = when(state) {
                    is HuntState.Locked -> state.ghost
                    is HuntState.Spawned -> state.ghost
                    else -> null
                }
                ghost?.let {
                    GhostInfoCard(it, state)
                }
            }

            // 消息提示
            Surface(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, Color(0xFF00FF9C).copy(alpha = 0.3f))
            ) {
                Text(
                    message, color = Color(0xFF00FF9C), fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun GhostHuntCanvas(
    huntState: HuntState,
    resonance: XuanjiResonance.Result,
    onTapGhost: (Offset) -> Unit,
    modifier: Modifier
) {
    val inf = rememberInfiniteTransition(label = "huntCanvas")
    val time by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart), label = "ht")
    val ghostBob by inf.animateFloat(-8f, 8f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gb")

    Canvas(
        modifier.pointerInput(huntState) {
            detectTapGestures { tapOffset ->
                // 将屏幕坐标换算（近似）
                onTapGhost(Offset(tapOffset.x, tapOffset.y))
            }
        }
    ) {
        val cw = size.width; val ch = size.height

        // 背景地脉粒子
        repeat(30) { i ->
            val px = (i * 137.5f) % cw
            val py = ((i * 83.7f + time * ch * 0.3f) % ch)
            val alpha = 0.04f + sin(time * 6.28f + i * 0.8f).toFloat().coerceAtLeast(0f) * 0.06f
            drawCircle(Color(0xFF7B2FBE).copy(alpha = alpha), 2f, Offset(px, py))
        }

        // 符文光轮（背景装饰）
        val cx = cw / 2f; val cy = ch * 0.3f
        val runeR = 80f
        rotate(time * 360f * 0.5f, Offset(cx, cy)) {
            repeat(6) { i ->
                val a = Math.toRadians(i * 60.0)
                drawLine(
                    Color(0xFF4A0080).copy(alpha = 0.25f),
                    Offset(cx, cy),
                    Offset(cx + runeR * cos(a).toFloat(), cy + runeR * sin(a).toFloat()),
                    1.5f
                )
            }
            drawCircle(Color(0xFF4A0080).copy(alpha = 0.15f), runeR)
        }

        // 鬼魂实体
        when (val state = huntState) {
            is HuntState.Spawned -> {
                drawGhostEntity(state.pos.copy(y = state.pos.y + ghostBob), state.ghost, 1f, time)
            }
            is HuntState.Locked -> {
                // 锁定环
                val lockR = 55f + sin(time * 6.28f * 3).toFloat() * 6f
                drawCircle(Color(0xFFFF6B00).copy(alpha = 0.7f), lockR,
                    state.pos, style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f))
                rotate(time * 360f * 2f, state.pos) {
                    repeat(4) { i ->
                        val a = Math.toRadians(i * 90.0)
                        drawCircle(Color(0xFFFF6B00).copy(alpha = 0.9f), 5f,
                            Offset(state.pos.x + lockR * cos(a).toFloat(), state.pos.y + lockR * sin(a).toFloat()))
                    }
                }
                // 充能进度弧
                drawArc(
                    color = Color(0xFFFFD700).copy(alpha = 0.85f),
                    startAngle = -90f,
                    sweepAngle = state.sealCharge * 360f,
                    useCenter = false,
                    topLeft = Offset(state.pos.x - lockR - 8f, state.pos.y - lockR - 8f),
                    size = Size(lockR * 2 + 16f, lockR * 2 + 16f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(4f)
                )
                drawGhostEntity(state.pos.copy(y = state.pos.y + ghostBob * 0.3f), state.ghost, 0.7f, time)
            }
            is HuntState.Caught -> {
                // 封印爆炸效果
                val burstR = 30f + time * 120f
                drawCircle(Color(0xFFFFD700).copy(alpha = (1f - time).coerceAtLeast(0f) * 0.6f),
                    burstR, Offset(cx, cy * 1.2f))
                drawCircle(Color.White.copy(alpha = (1f - time).coerceAtLeast(0f) * 0.3f),
                    burstR * 0.5f, Offset(cx, cy * 1.2f))
            }
            else -> {}
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGhostEntity(
    pos: Offset, ghost: GhostType, opacity: Float, time: Float
) {
    val baseColor = ghost.rarity.color
    val r = 28f + sin(time * 6.28f * 1.5f).toFloat() * 4f

    // 鬼体光晕
    drawCircle(baseColor.copy(alpha = 0.12f * opacity), r * 2.2f, pos)
    drawCircle(baseColor.copy(alpha = 0.25f * opacity), r * 1.4f, pos)
    drawCircle(baseColor.copy(alpha = 0.6f * opacity), r, pos)
    drawCircle(Color.White.copy(alpha = 0.15f * opacity), r * 0.45f, pos)

    // 根据稀有度绘制不同形态
    when (ghost.rarity) {
        GhostRarity.COMMON -> {
            // 简单圆形游魂，带尾迹
            repeat(3) { i ->
                val tailY = pos.y + (i + 1) * 12f
                drawCircle(baseColor.copy(alpha = 0.15f * opacity * (1f - i * 0.3f)), r * (0.7f - i * 0.2f), Offset(pos.x, tailY))
            }
        }
        GhostRarity.UNCOMMON -> {
            // 厉鬼，带角状突起
            repeat(2) { i ->
                val a = Math.toRadians((-30.0 + i * 60.0))
                drawLine(baseColor.copy(alpha = 0.7f * opacity), pos,
                    Offset(pos.x + 30f * cos(a).toFloat(), pos.y - 30f * sin(a.toFloat() + abs(a.toFloat()))),
                    3f)
            }
        }
        GhostRarity.RARE -> {
            // 旋转光环
            rotate(time * 180f, pos) {
                repeat(6) { i ->
                    val a = Math.toRadians(i * 60.0)
                    drawLine(baseColor.copy(alpha = 0.5f * opacity), pos,
                        Offset(pos.x + r * 1.8f * cos(a).toFloat(), pos.y + r * 1.8f * sin(a).toFloat()), 1.5f)
                }
            }
        }
        GhostRarity.EPIC -> {
            // 多层光环 + 粒子
            drawCircle(baseColor.copy(alpha = 0.3f * opacity), r * 1.8f, pos,
                style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
            drawCircle(baseColor.copy(alpha = 0.2f * opacity), r * 2.4f, pos,
                style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
        }
        GhostRarity.LEGENDARY -> {
            // 传说级：多重旋转层
            repeat(3) { ring ->
                rotate(time * (120f + ring * 60f) * (if (ring % 2 == 0) 1f else -1f), pos) {
                    drawCircle(baseColor.copy(alpha = (0.25f - ring * 0.05f) * opacity),
                        r * (1.6f + ring * 0.5f), pos,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(2f - ring * 0.4f))
                }
            }
        }
    }
}

@Composable
private fun GhostInfoCard(ghost: GhostType, state: HuntState) {
    val sealCharge = (state as? HuntState.Locked)?.sealCharge ?: 0f
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ghost.rarity.color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, ghost.rarity.color.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(ghost.name, color = ghost.rarity.color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("${ghost.rarity.label} · ${ghost.element.label}属性 · 难度${"★".repeat(ghost.catchDifficulty)}", color = TextSub, fontSize = 10.sp)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF00FF9C).copy(alpha = 0.15f)) {
                    Text("鬼气+${ghost.dropEssence}", color = Color(0xFF00FF9C), fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
            if (sealCharge > 0f) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("封印进度", color = TextSub, fontSize = 11.sp)
                    Text("${(sealCharge * 100).toInt()}%", color = GoldBright, fontSize = 11.sp)
                }
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(5.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.1f))) {
                    Box(Modifier.fillMaxWidth(sealCharge).fillMaxHeight()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF6B00), GoldBright))))
                }
            } else {
                Spacer(Modifier.height(4.dp))
                Text("克制：${ghost.weaknessTool}", color = TextSub, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
    }
}
