package com.aning.xuanxue.feature.ghost

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.core.store.PlayerViewModel
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

sealed class HuntState {
    object Idle : HuntState()
    data class Spawned(val ghost: GhostType, val pos: Offset, val hp: Float = 1f) : HuntState()
    data class Locked(val ghost: GhostType, val pos: Offset, val sealCharge: Float = 0f) : HuntState()
    data class Caught(val ghost: GhostType, val isRare: Boolean) : HuntState()
    data class Fled(val ghost: GhostType) : HuntState()
}

@Composable
fun GhostHuntScreen(onBack: () -> Unit, playerVm: PlayerViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var huntState by remember { mutableStateOf<HuntState>(HuntState.Idle) }
    val playerSave by playerVm.playerSave.collectAsStateWithLifecycle()
    val resonance by playerVm.resonance.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf("感应天地气息，等待灵体显形……") }

    LaunchedEffect(huntState) {
        if (huntState is HuntState.Idle) {
            delay((3000L..7000L).random())
            val ghost = GhostRegistry.all.filter { it.catchDifficulty <= 5 }.random()
            val pos = Offset(Random.nextFloat() * 280f + 40f, Random.nextFloat() * 300f + 80f)
            huntState = HuntState.Spawned(ghost, pos)
            message = "感应到一缕【未知灵体】！快速点击锁定！"
            XuanSound.play(context, XuanSound.Effect.Click)
            scope.launch {
                delay(10000L)
                if (huntState is HuntState.Spawned) {
                    huntState = HuntState.Fled(ghost)
                    message = "那灵体消散了……再等等"
                    delay(2000)
                    huntState = HuntState.Idle
                }
            }
        }
    }

    LaunchedEffect(huntState) {
        if (huntState is HuntState.Locked) {
            val difficulty = (huntState as HuntState.Locked).ghost.catchDifficulty
            val chargeSpeed = (10 - difficulty) * 0.008f + 0.004f
            while (huntState is HuntState.Locked) {
                delay(50)
                val cur = huntState as? HuntState.Locked ?: break
                val nextCharge = cur.sealCharge + chargeSpeed * (resonance.multiplier * 0.4f)
                if (nextCharge >= 1f) {
                    val caught = Random.nextFloat() < (0.5f + resonance.total * 0.004f)
                    huntState = if (caught) {
                        XuanSound.play(context, XuanSound.Effect.Open)
                        HuntState.Caught(cur.ghost, cur.ghost.rarity.ordinal >= 2)
                    } else HuntState.Fled(cur.ghost)
                } else {
                    huntState = cur.copy(sealCharge = nextCharge)
                }
            }
            when (val result = huntState) {
                is HuntState.Caught -> {
                    scope.launch { playerVm.onGhostCaught(result.ghost.name, result.ghost.dropEssence, result.ghost.dropEssence.toLong() * 10L) }
                    message = "✦ 成功镇压【${result.ghost.name}】！获得鬼气 ×${result.ghost.dropEssence}"
                    delay(2800)
                    huntState = HuntState.Idle
                    message = "感应天地气息，等待灵体显形……"
                }
                is HuntState.Fled -> {
                    message = "那灵体挣脱了！符咒能量不足……"
                    delay(2000)
                    huntState = HuntState.Idle
                }
                else -> Unit
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF050510))) {
        GhostHuntCanvas(
            huntState = huntState,
            resonance = resonance,
            onTapGhost = { tap ->
                val state = huntState
                if (state is HuntState.Spawned) {
                    val dx = state.pos.x - tap.x
                    val dy = state.pos.y - tap.y
                    if (sqrt(dx * dx + dy * dy) < 80f) {
                        huntState = HuntState.Locked(state.ghost, state.pos)
                        message = "锁定！正在凝聚符咒能量……"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(480.dp).padding(top = 80.dp)
        )

        (huntState as? HuntState.Caught)?.let { caught ->
            GhostRevealCard(
                ghost = caught.ghost,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 22.dp)
            )
        }

        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.07f)) {
                    Text("← 返回", color = TextSub, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onBack).padding(horizontal = 12.dp, vertical = 6.dp))
                }
                Text("捉鬼行动", color = Color(0xFF00FF9C), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Column(horizontalAlignment = Alignment.End) {
                    Text("已捉：${playerSave.ghostsCaught}", color = GoldBright, fontSize = 13.sp)
                    Text("鬼气：${playerSave.ghostEssence}", color = Color(0xFF00FF9C), fontSize = 11.sp)
                }
            }
            ResonanceBar(resonance)
        }

        Column(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val state = huntState
            if (state is HuntState.Locked || state is HuntState.Spawned) {
                val ghost = when (state) {
                    is HuntState.Locked -> state.ghost
                    is HuntState.Spawned -> state.ghost
                    else -> null
                }
                ghost?.let { GhostInfoCard(it, state) }
            }
            MessageBar(message)
        }
    }
}

@Composable
private fun ResonanceBar(resonance: XuanjiResonance.Result) {
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

@Composable
private fun MessageBar(message: String) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, Color(0xFF00FF9C).copy(alpha = 0.3f))
    ) {
        Text(message, color = Color(0xFF00FF9C), fontSize = 13.sp, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center, lineHeight = 20.sp)
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
    val time by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart), label = "time")
    val ghostBob by inf.animateFloat(-8f, 8f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob")

    Canvas(modifier.pointerInput(huntState) { detectTapGestures { onTapGhost(Offset(it.x, it.y)) } }) {
        val cw = size.width
        val ch = size.height
        repeat(30) { i ->
            val px = (i * 137.5f) % cw
            val py = ((i * 83.7f + time * ch * 0.3f) % ch)
            val alpha = 0.04f + sin(time * 6.28f + i * 0.8f).toFloat().coerceAtLeast(0f) * 0.06f
            drawCircle(Color(0xFF7B2FBE).copy(alpha = alpha), 2f, Offset(px, py))
        }

        val cx = cw / 2f
        val cy = ch * 0.3f
        val runeR = 80f
        rotate(time * 180f, Offset(cx, cy)) {
            repeat(6) { i ->
                val a = Math.toRadians(i * 60.0)
                drawLine(Color(0xFF4A0080).copy(alpha = 0.25f), Offset(cx, cy), Offset(cx + runeR * cos(a).toFloat(), cy + runeR * sin(a).toFloat()), 1.5f)
            }
            drawCircle(Color(0xFF4A0080).copy(alpha = 0.15f), runeR)
        }

        when (val state = huntState) {
            is HuntState.Spawned -> drawGhostEntity(state.pos.copy(y = state.pos.y + ghostBob), state.ghost, 1f, time)
            is HuntState.Locked -> {
                val lockR = 55f + sin(time * 18.84f).toFloat() * 6f
                drawCircle(Color(0xFFFF6B00).copy(alpha = 0.7f), lockR, state.pos, style = Stroke(2.5f))
                drawArc(
                    color = Color(0xFFFFD700).copy(alpha = 0.85f),
                    startAngle = -90f,
                    sweepAngle = state.sealCharge * 360f,
                    useCenter = false,
                    topLeft = Offset(state.pos.x - lockR - 8f, state.pos.y - lockR - 8f),
                    size = Size(lockR * 2 + 16f, lockR * 2 + 16f),
                    style = Stroke(4f)
                )
                drawGhostEntity(state.pos.copy(y = state.pos.y + ghostBob * 0.3f), state.ghost, 0.7f, time)
            }
            is HuntState.Caught -> {
                val r = 30f + time * 120f
                drawCircle(Color(0xFFFFD700).copy(alpha = (1f - time).coerceAtLeast(0f) * 0.6f), r, Offset(cx, cy * 1.2f))
            }
            else -> Unit
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGhostEntity(pos: Offset, ghost: GhostType, opacity: Float, time: Float) {
    val c = ghost.rarity.color
    val r = 28f + sin(time * 9.42f).toFloat() * 4f
    drawCircle(c.copy(alpha = 0.12f * opacity), r * 2.2f, pos)
    drawCircle(c.copy(alpha = 0.25f * opacity), r * 1.4f, pos)
    drawCircle(c.copy(alpha = 0.6f * opacity), r, pos)
    drawCircle(Color.White.copy(alpha = 0.15f * opacity), r * 0.45f, pos)
    when (ghost.rarity) {
        GhostRarity.COMMON -> repeat(3) { i -> drawCircle(c.copy(alpha = 0.15f * opacity * (1f - i * 0.3f)), r * (0.7f - i * 0.2f), Offset(pos.x, pos.y + (i + 1) * 12f)) }
        GhostRarity.UNCOMMON -> repeat(2) { i ->
            val a = Math.toRadians((-30.0 + i * 60.0))
            drawLine(c.copy(alpha = 0.7f * opacity), pos, Offset(pos.x + 30f * cos(a).toFloat(), pos.y - 20f), 3f)
        }
        GhostRarity.RARE -> rotate(time * 180f, pos) { repeat(6) { i ->
            val a = Math.toRadians(i * 60.0)
            drawLine(c.copy(alpha = 0.5f * opacity), pos, Offset(pos.x + r * 1.8f * cos(a).toFloat(), pos.y + r * 1.8f * sin(a).toFloat()), 1.5f)
        } }
        GhostRarity.EPIC -> {
            drawCircle(c.copy(alpha = 0.3f * opacity), r * 1.8f, pos, style = Stroke(2f))
            drawCircle(c.copy(alpha = 0.2f * opacity), r * 2.4f, pos, style = Stroke(1f))
        }
        GhostRarity.LEGENDARY -> repeat(3) { ring -> drawCircle(c.copy(alpha = (0.25f - ring * 0.05f) * opacity), r * (1.6f + ring * 0.5f), pos, style = Stroke(2f - ring * 0.4f)) }
    }
}

@Composable
private fun GhostInfoCard(ghost: GhostType, state: HuntState) {
    val sealCharge = (state as? HuntState.Locked)?.sealCharge ?: 0f
    val revealed = state is HuntState.Caught
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ghost.rarity.color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, ghost.rarity.color.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (revealed) ghost.name else "未知灵体", color = ghost.rarity.color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (revealed) "${ghost.rarity.label} · ${ghost.element.label}属性 · 难度${"★".repeat(ghost.catchDifficulty)}" else "气息不明 · 属性未知 · 凶险难测",
                        color = TextSub,
                        fontSize = 10.sp
                    )
                }
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF00FF9C).copy(alpha = 0.15f)) {
                    Text(if (revealed) "鬼气+${ghost.dropEssence}" else "鬼气 ???", color = Color(0xFF00FF9C), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
            if (sealCharge > 0f) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("封印进度", color = TextSub, fontSize = 11.sp)
                    Text("${(sealCharge * 100).toInt()}%", color = GoldBright, fontSize = 11.sp)
                }
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.1f))) {
                    Box(Modifier.fillMaxWidth(sealCharge).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(Brush.horizontalGradient(listOf(Color(0xFFFF6B00), GoldBright))))
                }
            } else {
                Spacer(Modifier.height(4.dp))
                Text("克制：${ghost.weaknessTool}", color = TextSub, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
    }
}
