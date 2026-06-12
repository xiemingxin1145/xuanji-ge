package com.aning.xuanxue.feature.case_engine

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// ─── 大气氛围色 ───────────────────────────────────────────────
private fun atmosphereColors(a: Atmosphere): Pair<Color, Color> = when (a) {
    Atmosphere.MYSTERIOUS  -> Color(0xFF1A0A2E) to Color(0xFF7B2FBE)
    Atmosphere.TENSE       -> Color(0xFF1A0000) to Color(0xFFD62828)
    Atmosphere.EERIE       -> Color(0xFF001A10) to Color(0xFF00C896)
    Atmosphere.CALM        -> Color(0xFF0A0A14) to Color(0xFF4A90D9)
    Atmosphere.TRIUMPHANT  -> Color(0xFF1A1000) to Color(0xFFFFD700)
}

// ─── 案件列表入口 ────────────────────────────────────────────
@Composable
fun CaseListScreen(onBack: () -> Unit, onEnterCase: (String) -> Unit) {
    val cases = CaseRepository.allCases
    XScaffold(title = "接案卷宗", onBack = onBack) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "玄机阁阁主，今有卷宗呈上，请择案而接。",
                color = TextSub, fontSize = 13.sp, lineHeight = 20.sp
            )
            cases.forEach { case -> CaseCard(case, onClick = { onEnterCase(case.meta.id) }) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CaseCard(case: CaseFull, onClick: () -> Unit) {
    val meta = case.meta
    val inf = rememberInfiniteTransition(label = "caseGlow")
    val glowA by inf.animateFloat(0.25f, 0.65f,
        infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse), label = "g")
    val (bgColor, accentColor) = atmosphereColors(case.scenes.first().atmosphere)

    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = glowA))
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(meta.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(meta.subtitle, color = accentColor.copy(alpha = 0.9f), fontSize = 12.sp)
                }
                DifficultyDots(meta.difficulty, accentColor)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("委托人：", color = TextSub, fontSize = 12.sp)
                Text(meta.clientName, color = GoldBright, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
            Text(meta.clientDesc, color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = accentColor.copy(alpha = 0.12f)) {
                Text(
                    "结案奖励：${meta.rewardDesc}",
                    color = accentColor,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
            if (meta.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    meta.tags.take(4).forEach { tag ->
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.07f)) {
                            Text("# $tag", color = TextSub, fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyDots(difficulty: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { i ->
            Box(
                Modifier.size(8.dp).clip(RoundedCornerShape(4.dp))
                    .background(if (i < difficulty) color else color.copy(alpha = 0.2f))
            )
        }
    }
}

// ─── 案件游玩主界面 ────────────────────────────────────────
@Composable
fun CasePlayScreen(
    caseId: String,
    onBack: () -> Unit,
    onNavigateTool: (String) -> Unit
) {
    val case = remember { CaseRepository.getCase(caseId) }
    var progress by remember { mutableStateOf(CaseProgress(caseId, case.startScene.id)) }
    var textVisible by remember { mutableStateOf(false) }
    var choicesVisible by remember { mutableStateOf(false) }
    var resonanceResult by remember { mutableStateOf<XuanjiResonance.Result?>(null) }

    val currentScene = remember(progress.currentSceneId) {
        case.sceneById(progress.currentSceneId)
    }
    val (bgColor, accentColor) = atmosphereColors(currentScene.atmosphere)
    val context = LocalContext.current

    LaunchedEffect(progress.currentSceneId) {
        textVisible = false
        choicesVisible = false
        resonanceResult = XuanjiResonance.calculate(progress.tianShi, progress.diLi, progress.renHe)
        delay(200)
        textVisible = true
        delay(800)
        choicesVisible = true
        XuanSound.play(context, XuanSound.Effect.Click)
    }

    Box(Modifier.fillMaxSize().background(bgColor)) {
        // 背景粒子动画
        AtmosphereParticles(accentColor, currentScene.atmosphere)

        Column(Modifier.fillMaxSize()) {
            // 顶栏
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, null, tint = Color.White.copy(alpha = 0.7f))
                }
                Spacer(Modifier.weight(1f))
                Text(case.meta.title, color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                // 当前玄机共鸣状态
                resonanceResult?.let { r ->
                    Surface(shape = RoundedCornerShape(10.dp), color = accentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))) {
                        Text(
                            r.level.displayName,
                            color = accentColor,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
            ) {
                // NPC 立绘区（几何风格）
                currentScene.speakerName?.let { name ->
                    AnimatedVisibility(textVisible, enter = fadeIn(tween(600))) {
                        NpcPortrait(name, accentColor, currentScene.atmosphere)
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // 叙事文本
                AnimatedVisibility(textVisible, enter = fadeIn(tween(800)) + slideInVertically { it / 4 }) {
                    NarrativeBlock(currentScene.narrative, currentScene.dialogue,
                        currentScene.speakerName, accentColor)
                }

                Spacer(Modifier.height(20.dp))

                // 工具提示
                currentScene.toolPrompt?.let { prompt ->
                    AnimatedVisibility(choicesVisible, enter = fadeIn(tween(400))) {
                        ToolPromptBanner(prompt, currentScene.toolRequired, accentColor) { tool ->
                            onNavigateTool(tool.route)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // 结局展示
                if (currentScene.isEnding) {
                    AnimatedVisibility(choicesVisible, enter = fadeIn(tween(600))) {
                        EndingCard(currentScene.endingType, accentColor)
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f),
                            contentColor = accentColor),
                        border = BorderStroke(1.dp, accentColor)
                    ) {
                        Text("返回卷宗", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // 选项按钮
                if (!currentScene.isEnding) {
                    AnimatedVisibility(choicesVisible, enter = fadeIn(tween(500)) + slideInVertically { it / 2 }) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            currentScene.choices.forEachIndexed { _, choice ->
                                val meetsResonance = resonanceResult != null &&
                                    resonanceResult!!.total >= choice.minResonance
                                ChoiceButton(
                                    choice = choice,
                                    accentColor = accentColor,
                                    meetsResonance = meetsResonance,
                                    onClick = {
                                        if (meetsResonance) {
                                            progress = progress.copy(
                                                currentSceneId = choice.nextSceneId,
                                                xuanqiEarned = progress.xuanqiEarned + choice.rewardXuanqi
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun NarrativeBlock(
    narrative: String,
    dialogue: String?,
    speakerName: String?,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (narrative.isNotBlank()) {
            Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.06f)) {
                Text(
                    narrative,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 15.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        if (!dialogue.isNullOrBlank() && speakerName != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("「$speakerName」", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(dialogue, color = Color.White, fontSize = 15.sp, lineHeight = 25.sp)
                }
            }
        }
    }
}

@Composable
private fun ToolPromptBanner(
    prompt: String,
    tool: ToolAction?,
    accentColor: Color,
    onUseTool: (ToolAction) -> Unit
) {
    val inf = rememberInfiniteTransition(label = "toolBanner")
    val pulse by inf.animateFloat(0.4f, 0.9f,
        infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse), label = "p")
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A0A00),
        border = BorderStroke(1.dp, Gold.copy(alpha = pulse))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("▶ 推演提示", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(prompt, color = TextMain, fontSize = 13.sp, lineHeight = 19.sp)
            }
            tool?.let { t ->
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { onUseTool(t) },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold.copy(alpha = 0.18f),
                        contentColor = GoldBright),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(t.label, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ChoiceButton(
    choice: CaseChoice,
    accentColor: Color,
    meetsResonance: Boolean,
    onClick: () -> Unit
) {
    val locked = !meetsResonance
    val borderAlpha = if (locked) 0.2f else 0.7f

    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .clickable(enabled = !locked, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (locked) Color.White.copy(alpha = 0.04f) else accentColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = borderAlpha))
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    choice.label,
                    color = if (locked) TextSub else Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (choice.desc.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(choice.desc, color = TextSub, fontSize = 12.sp)
                }
                if (choice.minResonance > 0) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (locked) "⚠ 需玄机共鸣值 ≥${choice.minResonance}" else "✓ 共鸣达标",
                        color = if (locked) Cinnabar.copy(alpha = 0.8f) else Jade,
                        fontSize = 11.sp
                    )
                }
            }
            if (choice.rewardXuanqi > 0) {
                Surface(shape = RoundedCornerShape(6.dp), color = Gold.copy(alpha = 0.15f)) {
                    Text("+${choice.rewardXuanqi}玄气", color = GoldBright, fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
        }
    }
}

@Composable
private fun EndingCard(endingType: EndingType?, accentColor: Color) {
    val (label, color) = when (endingType) {
        EndingType.GOOD     -> "圆满结案 ✦" to Color(0xFFFFD700)
        EndingType.BAD      -> "结案存疑 ✗" to Color(0xFFD62828)
        EndingType.HIDDEN   -> "隐秘结局 ◈" to Color(0xFF00FF9C)
        null                -> "案件完结" to accentColor
    }
    val inf = rememberInfiniteTransition(label = "ending")
    val glow by inf.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse), label = "eg")
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(2.dp, color.copy(alpha = glow))
    ) {
        Text(
            label,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
        )
    }
}

@Composable
private fun NpcPortrait(name: String, accentColor: Color, atmosphere: Atmosphere) {
    val inf = rememberInfiniteTransition(label = "portrait")
    val rot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart), label = "pr")
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Canvas(Modifier.size(56.dp)) {
            val cx = size.width / 2f; val cy = size.height / 2f; val r = size.minDimension / 2f
            drawCircle(accentColor.copy(alpha = 0.18f), r)
            rotate(rot) {
                repeat(8) { i ->
                    val angle = i * 45.0
                    val rx = cx + r * 0.68f * cos(Math.toRadians(angle)).toFloat()
                    val ry = cy + r * 0.68f * sin(Math.toRadians(angle)).toFloat()
                    drawCircle(accentColor.copy(alpha = 0.45f), 3.5f, Offset(rx, ry))
                }
            }
            drawCircle(accentColor.copy(alpha = 0.8f), r * 0.32f)
            drawCircle(Color.Black, r * 0.22f)
        }
        Column {
            Text(name, color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                when (atmosphere) {
                    Atmosphere.TENSE       -> "情绪激动"
                    Atmosphere.EERIE       -> "神色惊惧"
                    Atmosphere.MYSTERIOUS  -> "欲言又止"
                    Atmosphere.CALM        -> "语气平稳"
                    Atmosphere.TRIUMPHANT  -> "如释重负"
                },
                color = TextSub, fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun AtmosphereParticles(accentColor: Color, atmosphere: Atmosphere) {
    val particleCount = if (atmosphere == Atmosphere.TRIUMPHANT) 40 else 20
    val particles = remember {
        List(particleCount) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 8000f + 4000f)
        }
    }
    val inf = rememberInfiniteTransition(label = "particles")
    val time by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart), label = "pt")
    Canvas(Modifier.fillMaxSize()) {
        particles.forEach { (px, py, speed) ->
            val normSpeed = speed / 12000f
            val y = ((py + time * normSpeed * 3) % 1f) * size.height
            val x = px * size.width
            val alpha = if (atmosphere == Atmosphere.TENSE) 0.12f else 0.08f
            drawCircle(accentColor.copy(alpha = alpha), 2.5f, Offset(x, y))
        }
    }
}
