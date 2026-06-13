package com.aning.xuanxue.feature.ghost

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.aning.xuanxue.ui.*
import com.aning.xuanxue.feature.investigation.GhostCaseTable
import com.aning.xuanxue.feature.investigation.XuanTool
import com.aning.xuanxue.core.art.ArtAssets
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * 鬼怪图鉴 — 山海经来源，民俗典故解说
 */
@Composable
fun GhostBestiaryScreen(onBack: () -> Unit, caughtIds: Set<String> = emptySet()) {
    XScaffold(title = "鬼怪图鉴", onBack = onBack) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                "民俗鬼怪卡册 · 已接入场景插图 · 点开查看三证与克制法器。",
                color = TextSub, fontSize = 12.sp
            )
            GhostRarity.entries.forEach { rarity ->
                val ghosts = GhostRegistry.byRarity(rarity)
                if (ghosts.isNotEmpty()) {
                    RaritySection(rarity, ghosts, caughtIds)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RaritySection(rarity: GhostRarity, ghosts: List<GhostType>, caughtIds: Set<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(rarity.color))
            Text(rarity.label, color = rarity.color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        ghosts.forEach { ghost ->
            val caught = ghost.id in caughtIds
            GhostBestiaryCard(ghost, caught)
        }
    }
}

@Composable
private fun GhostBestiaryCard(ghost: GhostType, caught: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        color = if (caught) ghost.rarity.color.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.035f),
        border = BorderStroke(1.dp, if (caught) ghost.rarity.color.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GhostThumb(ghost, caught, Modifier.size(width = 78.dp, height = 98.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(ghost.name, color = if (caught) ghost.rarity.color else TextMain,
                            fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text(ghost.alias, color = TextSub, fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        RarityBadge(ghost.rarity)
                        ElementBadge(ghost.element)
                        DifficultyBadge(ghost.catchDifficulty)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (ArtAssets.ghostSceneRes(ghost.id) != null) "已接入插图 · 点击展开" else "暂无插图 · 使用程序化占位",
                        color = if (ArtAssets.ghostSceneRes(ghost.id) != null) Gold.copy(alpha = 0.85f) else TextSub,
                        fontSize = 10.sp
                    )
                }
                Text(if (expanded) "▲" else "▼", color = TextSub, fontSize = 12.sp)
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))
                GhostSceneBanner(ghost, caught)
                Surface(shape = RoundedCornerShape(8.dp), color = Gold.copy(alpha = 0.08f)) {
                    Text("📜 ${ghost.origin}", color = Gold.copy(alpha = 0.8f), fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(ghost.description, color = TextMain, fontSize = 13.sp, lineHeight = 21.sp)
                Spacer(Modifier.height(10.dp))
                EvidenceChips(ghost)
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Cinnabar.copy(alpha = 0.08f)) {
                    Text("⚔ 克制：${ghost.weaknessTool}", color = Cinnabar.copy(alpha = 0.9f), fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                if (caught) {
                    Spacer(Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Jade.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Jade.copy(alpha = 0.4f))) {
                        Column(Modifier.padding(10.dp)) {
                            Text("▶ 民俗典故（已解锁）", color = Jade, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(ghost.loreUnlock, color = TextMain, fontSize = 12.sp, lineHeight = 19.sp)
                        }
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                    Text("🔒 捉住此鬼后解锁民俗典故", color = TextSub, fontSize = 11.sp)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF00FF9C).copy(alpha = 0.12f)) {
                        Text("鬼气 +${ghost.dropEssence}", color = Color(0xFF00FF9C), fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = ghost.rarity.color.copy(alpha = 0.12f)) {
                        Text("修为 +${ghost.dropXp}", color = ghost.rarity.color, fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GhostThumb(ghost: GhostType, caught: Boolean, modifier: Modifier) {
    val res = ArtAssets.ghostSceneRes(ghost.id)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, ghost.rarity.color.copy(alpha = 0.45f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (res != null) {
                Image(
                    painter = painterResource(res),
                    contentDescription = ghost.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(if (caught) 1f else 0.82f)
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to Color(0xAA0D0A14)
                        )
                    )
                )
            } else {
                GhostIcon(ghost, Modifier.size(48.dp))
            }
        }
    }
}

@Composable
private fun EvidenceChips(ghost: GhostType) {
    val profile = remember(ghost.id) {
        GhostCaseTable.profiles.firstOrNull { it.ghostId == ghost.id }
    }
    if (profile == null) return
    val evidences = remember(ghost.id) { profile.evidences.toList() }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("三证定鬼", color = GoldBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            evidences.forEach { evidence ->
                val detector = XuanTool.detectors
                    .firstOrNull { evidence in it.detects }?.label ?: "—"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Gold.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.28f))
                ) {
                    Column(Modifier.widthIn(min = 96.dp, max = 142.dp).padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Text("${evidence.icon} ${evidence.label}", color = GoldBright, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(2.dp))
                        Text(detector, color = TextSub, fontSize = 9.sp)
                    }
                }
            }
        }
        Text(
            "克星：${profile.weaknessTool.label}　收齐三证再镇压，避免误判反噬。",
            color = TextSub,
            fontSize = 10.sp,
            lineHeight = 15.sp
        )
    }
}

@Composable
private fun GhostSceneBanner(ghost: GhostType, caught: Boolean) {
    val res = ArtAssets.ghostSceneRes(ghost.id) ?: return
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, ghost.rarity.color.copy(alpha = if (caught) 0.85f else 0.55f))
        ) {
            Box {
                Image(
                    painter = painterResource(res),
                    contentDescription = ghost.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(if (caught) 1f else 0.88f)
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.65f to Color.Transparent,
                            1f to Color(0xDD0D0A14)
                        )
                    )
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, ghost.rarity.color.copy(alpha = 0.7f))
                ) {
                    Text(if (caught) "已收服" else "未收服", color = ghost.rarity.color, fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text(
                    "${ghost.name}  ${ghost.alias}",
                    color = Color.White,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun GhostIcon(ghost: GhostType, modifier: Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val cx = size.width / 2f; val cy = size.height / 2f; val r = size.minDimension * 0.4f
        drawCircle(ghost.rarity.color.copy(alpha = 0.15f), r * 1.3f, Offset(cx, cy))
        drawCircle(ghost.rarity.color.copy(alpha = 0.6f), r * 0.7f, Offset(cx, cy))
        drawCircle(Color.Black.copy(alpha = 0.4f), r * 0.4f, Offset(cx, cy))
        drawCircle(ghost.element.let { el ->
            when (el) {
                GhostElement.WATER  -> Color(0xFF4FC3F7)
                GhostElement.FIRE   -> Color(0xFFEF5350)
                GhostElement.WOOD   -> Color(0xFF66BB6A)
                GhostElement.METAL  -> Color(0xFFFFD700)
                GhostElement.EARTH  -> Color(0xFFBCAAA4)
            }
        }.copy(alpha = 0.7f), r * 0.22f, Offset(cx, cy))
    }
}

@Composable
private fun RarityBadge(rarity: GhostRarity) {
    Surface(shape = RoundedCornerShape(5.dp), color = rarity.color.copy(alpha = 0.15f)) {
        Text(rarity.label, color = rarity.color, fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
    }
}

@Composable
private fun ElementBadge(element: GhostElement) {
    Surface(shape = RoundedCornerShape(5.dp), color = Color.White.copy(alpha = 0.07f)) {
        Text("${element.symbol}${element.label}", color = TextSub, fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
    }
}

@Composable
private fun DifficultyBadge(difficulty: Int) {
    Surface(shape = RoundedCornerShape(5.dp), color = Cinnabar.copy(alpha = 0.1f)) {
        Text("★".repeat(difficulty.coerceAtMost(5)), color = Cinnabar.copy(alpha = 0.8f), fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
    }
}
