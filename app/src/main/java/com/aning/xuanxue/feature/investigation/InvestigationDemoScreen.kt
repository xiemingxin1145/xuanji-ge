package com.aning.xuanxue.feature.investigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.InkSurface
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XScaffold

/**
 * 三证调查 Demo screen.
 *
 * It proves the core loop before AR / camera / model rendering is connected:
 * unknown target -> scan with detector tools -> collect 3 clues -> choose seal tool.
 */
@Composable
fun InvestigationDemoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var session by remember { mutableStateOf(InvestigationSessionEngine.startRandom()) }
    var log by remember { mutableStateOf("未知目标已出现。先用探测法器取证，不要急着处置。") }
    val revealedName = if (session.canIdentify) session.target.displayName else "未知灵体"

    XScaffold(title = "三证调查", onBack = onBack) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Ink)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(6.dp))
            TargetPanel(session = session, revealedName = revealedName, log = log)
            EvidencePanel(session = session)
            CandidatePanel(session = session)
            ToolPanel(
                title = "探测法器",
                tools = XuanTool.detectors,
                enabled = !session.sealed && !session.failed,
                onClick = { tool ->
                    when (val result = InvestigationSessionEngine.scan(session, tool)) {
                        is InvestigationStepResult.Collected -> {
                            session = result.session
                            log = "${tool.label} 有反应：${result.evidence.label}。"
                            XuanSound.play(context, XuanSound.Effect.Bell)
                        }
                        is InvestigationStepResult.NoSignal -> {
                            session = result.session
                            log = "${tool.label} 暂无有效反应，换一件法器再试。"
                            XuanSound.play(context, XuanSound.Effect.Warning)
                        }
                    }
                }
            )
            ToolPanel(
                title = "处置法器",
                tools = XuanTool.sealers,
                enabled = !session.sealed && !session.failed,
                onClick = { tool ->
                    when (val result = InvestigationSessionEngine.trySeal(session, tool)) {
                        is SealAttemptResult.Success -> {
                            session = result.session
                            log = "判断正确，${tool.label} 生效，案件完成。"
                            XuanSound.play(context, XuanSound.Effect.Seal)
                        }
                        is SealAttemptResult.WrongTool -> {
                            session = result.session
                            log = "法器不合。正确方向应是：${result.correctTool.label}。"
                            XuanSound.play(context, XuanSound.Effect.GhostNear)
                        }
                        is SealAttemptResult.NotEnoughEvidence -> {
                            session = result.session
                            log = "证据不足，至少收齐三条线索再处置。"
                            XuanSound.play(context, XuanSound.Effect.Warning)
                        }
                    }
                }
            )
            Button(
                onClick = {
                    session = InvestigationSessionEngine.startRandom()
                    log = "新目标已出现。重新开始三证调查。"
                    XuanSound.play(context, XuanSound.Effect.Open)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Gold.copy(alpha = 0.18f), contentColor = GoldBright),
                border = BorderStroke(1.dp, Gold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("重新开案", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TargetPanel(session: InvestigationSession, revealedName: String, log: String) {
    val border = when {
        session.sealed -> Jade
        session.failed -> Cinnabar
        session.canIdentify -> GoldBright
        else -> Color(0xFF7B2FBE)
    }
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, border.copy(alpha = 0.55f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(revealedName, color = border, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("调查进度：${session.progressText} · 尝试 ${session.attempts} 次", color = TextSub, fontSize = 12.sp)
            Text(log, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
            if (session.sealed) Text("结案：成功", color = Jade, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            if (session.failed) Text("结案：失败，需要复盘证据与法器", color = Cinnabar, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EvidencePanel(session: InvestigationSession) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = InkSurface) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("已收集证据", color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (session.collected.isEmpty()) {
                Text("暂无证据。优先用罗盘、风铃、铜镜等探测法器。", color = TextSub, fontSize = 12.sp)
            } else {
                session.collected.forEach { e -> EvidenceRow(e) }
            }
        }
    }
}

@Composable
private fun EvidenceRow(evidence: EvidenceType) {
    Surface(shape = RoundedCornerShape(10.dp), color = evidence.accentColor.copy(alpha = 0.12f)) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(evidence.icon, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(evidence.label, color = evidence.accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(evidence.hint, color = TextSub, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun CandidatePanel(session: InvestigationSession) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = InkSurface) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("候选范围", color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                if (session.candidates.size == 1) "已基本锁定目标。" else "当前仍有 ${session.candidates.size} 种可能。",
                color = TextSub,
                fontSize = 12.sp
            )
            session.candidates.forEach { p ->
                Text("· ${if (session.canIdentify) p.displayName else "未知类型"}  /  处置：${if (session.canIdentify) p.weaknessTool.label else "未明"}", color = TextMain, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ToolPanel(title: String, tools: List<XuanTool>, enabled: Boolean, onClick: (XuanTool) -> Unit) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = InkSurface) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            tools.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { tool ->
                        Surface(
                            Modifier.weight(1f).clickable(enabled = enabled) { onClick(tool) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = if (enabled) 0.06f else 0.025f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = if (enabled) 0.12f else 0.05f))
                        ) {
                            Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tool.icon, fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(tool.label, color = if (enabled) TextMain else TextSub, fontSize = 12.sp, textAlign = TextAlign.Center)
                                Text(tool.sensorHint, color = TextSub, fontSize = 9.sp, textAlign = TextAlign.Center, maxLines = 2)
                            }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
