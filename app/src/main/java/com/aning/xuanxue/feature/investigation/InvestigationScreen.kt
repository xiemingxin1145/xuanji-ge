package com.aning.xuanxue.feature.investigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Canvas
import com.aning.xuanxue.feature.ghost.GhostRegistry
import com.aning.xuanxue.core.art.XuanBackgroundImage
import com.aning.xuanxue.core.art.ToolIcon
import kotlin.math.cos
import kotlin.math.sin

private val InkBlack   = Color(0xFF0D0A14)
private val InkPanel   = Color(0xFF161020)
private val GoldBright  = Color(0xFFFFD54F)
private val GoldDim     = Color(0xFFBFA460)
private val Cinnabar    = Color(0xFFE53935)
private val JadeGreen   = Color(0xFF66BB6A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestigationScreen(
    onBack: () -> Unit,
    vm: InvestigationViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    // 磁力计仅在取证阶段激活
    val compass by rememberCompassEngine(
        active = state.phase == InvestigationPhase.INVESTIGATE
    )

    // 罗盘探到 REVERSED 时，把证据回灌给 ViewModel
    LaunchedEffect(compass.producesEvidence) {
        if (state.phase == InvestigationPhase.INVESTIGATE &&
            compass.producesEvidence != null
        ) {
            vm.detectWith(XuanTool.LUOPAN)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("玄机阁 · 第一夜", color = GoldBright, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("‹ 退", color = GoldDim) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InkPanel)
            )
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            // P0：场景背景。有美术资源显图，无图回退深色渐变（治"黑乎乎"）
            XuanBackgroundImage(caseId = "城郊荒宅")

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state.phase) {
                    InvestigationPhase.BRIEFING    -> BriefingView(state, vm)
                    InvestigationPhase.INVESTIGATE -> InvestigateView(state, compass, vm)
                    InvestigationPhase.DEDUCE      -> DeduceView(state, vm)
                    InvestigationPhase.SEAL        -> SealView(state, vm)
                    InvestigationPhase.RESULT      -> ResultView(state, vm, onBack)
                }
            }
        }
    }
}

// ── 案情简报 ─────────────────────────────────
@Composable
private fun BriefingView(state: InvestigationState, vm: InvestigationViewModel) {
    Spacer(Modifier.height(20.dp))
    Text("📜", fontSize = 56.sp)
    Spacer(Modifier.height(12.dp))
    Text(state.sceneName, color = GoldBright, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(16.dp))
    Card(colors = CardDefaults.cardColors(containerColor = InkPanel)) {
        Text(
            "城郊有一处废弃宅院，入夜后常闻哭声、见鬼火。" +
                "村民求到玄机阁。你今夜要做的，不是莽撞捉鬼——\n\n" +
                "而是用法器探明：这宅子里，究竟是什么东西。\n\n" +
                "集齐三条灵异证据，判断鬼种，再以对应法器镇压。" +
                "认错鬼、用错法器，皆会遭其反扑。",
            color = Color(0xFFD7CCC8), fontSize = 15.sp,
            lineHeight = 24.sp, modifier = Modifier.padding(18.dp)
        )
    }
    Spacer(Modifier.height(24.dp))
    Button(
        onClick = { vm.startInvestigation() },
        colors = ButtonDefaults.buttonColors(containerColor = Cinnabar),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) { Text("入宅查案", fontSize = 17.sp, fontWeight = FontWeight.Bold) }
}

// ── 取证界面（罗盘 + 探测器 + 证据栏）──────────
@Composable
private fun InvestigateView(
    state: InvestigationState,
    compass: CompassReading,
    vm: InvestigationViewModel
) {
    // 罗盘
    LuopanWidget(compass)
    Spacer(Modifier.height(8.dp))
    AnomalyHint(compass.anomaly)

    Spacer(Modifier.height(20.dp))
    Divider(color = GoldDim.copy(alpha = 0.3f))
    Spacer(Modifier.height(16.dp))

    Text("其他法器探查", color = GoldDim, fontSize = 13.sp)
    Spacer(Modifier.height(10.dp))

    // 探测器按钮（罗盘自动，其他点击）
    val otherDetectors = XuanTool.detectors.filter { it != XuanTool.LUOPAN }
    var toast by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        otherDetectors.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { tool ->
                    DetectorButton(tool, Modifier.weight(1f)) {
                        val found = vm.detectWith(tool)
                        toast = found?.let { "采集到证据：${it.label}" }
                            ?: "${tool.label}：此处探不到异样。"
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }

    toast?.let {
        Spacer(Modifier.height(10.dp))
        Text(it, color = GoldBright, fontSize = 13.sp, textAlign = TextAlign.Center)
    }

    Spacer(Modifier.height(20.dp))
    EvidencePanel(state.collectedEvidence)

    Spacer(Modifier.height(16.dp))
    Text(
        "仍有嫌疑：${state.suspects.joinToString("、") { it.displayName }}",
        color = GoldDim, fontSize = 13.sp, textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(16.dp))
    OutlinedButton(
        onClick = { vm.forceDeduce() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (state.collectedEvidence.size >= 3) "证据已足 · 下判断"
            else "证据不足也要判断（风险自负）",
            color = if (state.collectedEvidence.size >= 3) GoldBright else GoldDim
        )
    }
}

// ── 风水罗盘组件（随磁力计转动）──────────────
@Composable
private fun LuopanWidget(compass: CompassReading) {
    // 平滑动画到目标角度
    val animatedHeading by animateFloatAsState(
        targetValue = compass.heading,
        animationSpec = tween(
            durationMillis = when (compass.anomaly) {
                CompassAnomaly.REVERSED -> 120   // 乱转：快
                CompassAnomaly.DEVIATE  -> 300
                else -> 600
            }
        ),
        label = "heading"
    )

    val ringColor = when (compass.anomaly) {
        CompassAnomaly.STABLE   -> GoldDim
        CompassAnomaly.FLUTTER  -> Color(0xFF42A5F5)
        CompassAnomaly.DEVIATE  -> Color(0xFFAB47BC)
        CompassAnomaly.REVERSED -> Cinnabar
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            val c = Offset(size.width / 2, size.height / 2)
            // 外环
            drawCircle(ringColor, r, c, style = Stroke(width = 6f))
            drawCircle(ringColor.copy(alpha = 0.4f), r * 0.78f, c, style = Stroke(width = 2f))
            drawCircle(ringColor.copy(alpha = 0.25f), r * 0.55f, c, style = Stroke(width = 2f))
            // 八卦方位刻度
            for (k in 0 until 24) {
                val ang = Math.toRadians(k * 15.0)
                val outer = r
                val inner = r * (if (k % 3 == 0) 0.82f else 0.9f)
                drawLine(
                    ringColor.copy(alpha = 0.6f),
                    Offset(c.x + inner * sin(ang).toFloat(), c.y - inner * cos(ang).toFloat()),
                    Offset(c.x + outer * sin(ang).toFloat(), c.y - outer * cos(ang).toFloat()),
                    strokeWidth = 2f
                )
            }
            // 指针（红北黑南），随 heading 旋转
            rotate(degrees = animatedHeading, pivot = c) {
                drawLine(Cinnabar, c, Offset(c.x, c.y - r * 0.7f), strokeWidth = 8f)
                drawLine(Color(0xFFECEFF1), c, Offset(c.x, c.y + r * 0.5f), strokeWidth = 6f)
            }
            drawCircle(GoldBright, 10f, c)
        }
        // 中央卦象
        if (compass.anomaly.bagua.isNotEmpty()) {
            Text(
                compass.anomaly.bagua,
                color = ringColor, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = 54.dp)
            )
        }
    }
    // 磁场读数
    Text(
        "磁场 ${compass.microTesla.toInt()}μT · 基线 ${compass.baseline.toInt()}μT",
        color = GoldDim, fontSize = 11.sp
    )
}

@Composable
private fun AnomalyHint(anomaly: CompassAnomaly) {
    val color = when (anomaly) {
        CompassAnomaly.STABLE   -> JadeGreen
        CompassAnomaly.FLUTTER  -> Color(0xFF42A5F5)
        CompassAnomaly.DEVIATE  -> Color(0xFFAB47BC)
        CompassAnomaly.REVERSED -> Cinnabar
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            "${anomaly.label} · ${anomaly.tip}",
            color = color, fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun DetectorButton(tool: XuanTool, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = InkPanel),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldDim.copy(alpha = 0.3f))
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToolIcon(toolId = tool.name.lowercase(), fallbackEmoji = tool.icon, size = 28)
            Text(tool.label, color = GoldBright, fontSize = 14.sp)
        }
    }
}

@Composable
private fun EvidencePanel(collected: Set<EvidenceType>) {
    Text("证据栏（${collected.size}/3）", color = GoldBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { i ->
            val ev = collected.elementAtOrNull(i)
            Card(
                Modifier.weight(1f).height(88.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (ev != null) ev.accentColor.copy(alpha = 0.18f) else InkPanel
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (ev != null) ev.accentColor else GoldDim.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    Modifier.fillMaxSize().padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (ev != null) {
                        Text(ev.icon, fontSize = 22.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(ev.label, color = ev.accentColor, fontSize = 11.sp, textAlign = TextAlign.Center)
                    } else {
                        Text("？", fontSize = 22.sp, color = GoldDim.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

// ── 判断界面 ─────────────────────────────────
@Composable
private fun DeduceView(state: InvestigationState, vm: InvestigationViewModel) {
    Spacer(Modifier.height(8.dp))
    Text("下 判 断", color = GoldBright, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))
    Text("根据证据，这宅中作祟的是——", color = GoldDim, fontSize = 14.sp)
    Spacer(Modifier.height(16.dp))

    EvidencePanel(state.collectedEvidence)
    Spacer(Modifier.height(20.dp))

    state.suspects.forEach { suspect ->
        val ghost = runCatching { GhostRegistry.byId(suspect.ghostId) }.getOrNull()
        Card(
            Modifier.fillMaxWidth().padding(vertical = 5.dp)
                .clickable { vm.accuse(suspect.ghostId) },
            colors = CardDefaults.cardColors(containerColor = InkPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldDim.copy(alpha = 0.4f))
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(suspect.displayName, color = GoldBright, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    ghost?.let {
                        Text(it.alias, color = GoldDim, fontSize = 12.sp)
                    }
                }
                Text("指认 ›", color = Cinnabar, fontSize = 14.sp)
            }
        }
    }
    if (state.suspects.size > 1) {
        Spacer(Modifier.height(10.dp))
        Text(
            "⚠ 仍有 ${state.suspects.size} 种可能，证据或许还不够。",
            color = Color(0xFFFFB74D), fontSize = 12.sp, textAlign = TextAlign.Center
        )
    }
}

// ── 处置界面 ─────────────────────────────────
@Composable
private fun SealView(state: InvestigationState, vm: InvestigationViewModel) {
    val accused = GhostCaseTable.pool().firstOrNull { it.ghostId == state.accusedGhostId }
    Spacer(Modifier.height(8.dp))
    Text("择 器 镇 压", color = GoldBright, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))
    Text("你认定它是【${accused?.displayName ?: "?"}】，该用何法器？", color = GoldDim, fontSize = 14.sp)
    Spacer(Modifier.height(20.dp))

    XuanTool.sealers.forEach { tool ->
        Card(
            Modifier.fillMaxWidth().padding(vertical = 5.dp)
                .clickable { vm.sealWith(tool) },
            colors = CardDefaults.cardColors(containerColor = InkPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, Cinnabar.copy(alpha = 0.4f))
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolIcon(toolId = tool.name.lowercase(), fallbackEmoji = tool.icon, size = 32)
                Column(Modifier.weight(1f)) {
                    Text(tool.label, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(tool.intro, color = GoldDim, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

// ── 结算界面 ─────────────────────────────────
@Composable
private fun ResultView(state: InvestigationState, vm: InvestigationViewModel, onBack: () -> Unit) {
    val win = state.success == true
    Spacer(Modifier.height(20.dp))
    Text(if (win) "✦" else "✕", fontSize = 64.sp, color = if (win) GoldBright else Cinnabar)
    Spacer(Modifier.height(12.dp))
    Text(
        if (win) "镇 压 成 功" else "处 置 失 败",
        color = if (win) GoldBright else Cinnabar,
        fontSize = 26.sp, fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(16.dp))
    Card(colors = CardDefaults.cardColors(containerColor = InkPanel)) {
        Text(
            state.resultMessage,
            color = Color(0xFFD7CCC8), fontSize = 14.sp, lineHeight = 22.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
    Spacer(Modifier.height(16.dp))
    if (win) {
        Text("获得 鬼气 ×${state.essenceGained} · 修为 ×${state.xpGained}",
            color = JadeGreen, fontSize = 15.sp)
    } else {
        Text("获得 修为 ×${state.xpGained}（虽败犹有所得）",
            color = GoldDim, fontSize = 14.sp)
    }
    Spacer(Modifier.height(28.dp))
    Button(
        onClick = { vm.newCase() },
        colors = ButtonDefaults.buttonColors(containerColor = Cinnabar),
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) { Text("再查一案", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
    Spacer(Modifier.height(10.dp))
    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
        Text("回阁休整", color = GoldDim)
    }
}
