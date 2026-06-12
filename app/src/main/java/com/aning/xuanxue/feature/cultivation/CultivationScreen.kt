package com.aning.xuanxue.feature.cultivation

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aning.xuanxue.ui.*
import kotlin.math.*

@Composable
fun CultivationScreen(onBack: () -> Unit) {
    // 演示用固定存档，后期接DataStore
    val save = remember {
        PlayerSave(
            totalXp = 1200L,
            ghostEssence = 85,
            lingqi = 340,
            reputation = 15,
            casesCompleted = 1,
            ghostsCaught = 3,
            fiveElements = FiveElementStats(metal=12, wood=18, water=15, fire=10, earth=14)
        )
    }

    XScaffold(title = "道行修炼", onBack = onBack) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            RealmCard(save)
            ResourcesRow(save)
            FiveElementsCard(save.fiveElements)
            AchievementsPreview(save)
            DaoBaoSection()
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── 境界卡片 ─────────────────────────────────────
@Composable
private fun RealmCard(save: PlayerSave) {
    val realm = save.realm
    val inf = rememberInfiniteTransition(label = "realm")
    val rot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart), label = "rr")
    val pulse by inf.animateFloat(0.3f, 0.9f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "rp")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.Black,
        border = BorderStroke(1.5.dp, realm.color.copy(alpha = pulse))
    ) {
        Box(Modifier.fillMaxWidth().height(220.dp)) {
            // 背景旋转符纹
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width / 2f; val cy = size.height / 2f
                val r = size.minDimension * 0.42f
                drawCircle(realm.color.copy(alpha = 0.06f), r * 1.5f, Offset(cx, cy))
                rotate(rot, Offset(cx, cy)) {
                    repeat(8) { i ->
                        val a = i * 45.0
                        val x = cx + r * cos(Math.toRadians(a)).toFloat()
                        val y = cy + r * sin(Math.toRadians(a)).toFloat()
                        drawCircle(realm.color.copy(alpha = 0.35f), 4f, Offset(x, y))
                    }
                    drawCircle(realm.color.copy(alpha = 0.15f), r * 0.55f, Offset(cx, cy))
                    drawCircle(Ink, r * 0.38f, Offset(cx, cy))
                }
            }
            Column(
                Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("当前境界", color = TextSub, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(realm.display, color = realm.color, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(realm.subtitle, color = realm.color.copy(alpha = 0.7f), fontSize = 13.sp)
                }
                Column {
                    save.nextRealm?.let { next ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("距「${next.display}」", color = TextSub, fontSize = 12.sp)
                            Text("还需 ${save.xpToNextRealm} 修为", color = GoldBright, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.1f))) {
                            Box(
                                Modifier.fillMaxWidth(save.realmProgress).fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Brush.horizontalGradient(listOf(realm.color.copy(alpha = 0.6f), realm.color)))
                            )
                        }
                    } ?: Text("已至最高境界", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = realm.color.copy(alpha = 0.13f)) {
                        Text("✦ ${realm.unlocksAbility}", color = realm.color, fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

// ─── 资源栏 ───────────────────────────────────────
@Composable
private fun ResourcesRow(save: PlayerSave) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ResourceChip("鬼气", save.ghostEssence.toString(), Color(0xFF00FF9C), Modifier.weight(1f))
        ResourceChip("灵气", save.lingqi.toString(), Color(0xFF4FC3F7), Modifier.weight(1f))
        ResourceChip("声望", save.reputation.toString(), GoldBright, Modifier.weight(1f))
        ResourceChip("修为", save.totalXp.toString(), save.realm.color, Modifier.weight(1f))
    }
}

@Composable
private fun ResourceChip(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSub, fontSize = 10.sp)
        }
    }
}

// ─── 五行属性卡 ───────────────────────────────────
@Composable
private fun FiveElementsCard(stats: FiveElementStats) {
    val elements = listOf(
        Triple("金", stats.metal.toFloat(),  Color(0xFFFFD700)),
        Triple("木", stats.wood.toFloat(),   Color(0xFF66BB6A)),
        Triple("水", stats.water.toFloat(),  Color(0xFF4FC3F7)),
        Triple("火", stats.fire.toFloat(),   Color(0xFFEF5350)),
        Triple("土", stats.earth.toFloat(),  Color(0xFFBCAAA4))
    )
    val maxVal = elements.maxOf { it.second }.coerceAtLeast(1f)
    val dominant = stats.dominant()

    XCard(Modifier.fillMaxWidth()) {
        Text("五行属性", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text("主属性：$dominant ・ 总计 ${stats.total}", color = TextSub, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        // 五行雷达图（简化五边形）
        Canvas(Modifier.fillMaxWidth().height(150.dp)) {
            val cx = size.width / 2f; val cy = size.height / 2f
            val r = size.minDimension * 0.42f
            // 底网
            repeat(4) { ring ->
                val rr = r * (ring + 1) / 4f
                val pts = (0 until 5).map { i ->
                    val a = Math.toRadians(-90.0 + i * 72.0)
                    Offset(cx + rr * cos(a).toFloat(), cy + rr * sin(a).toFloat())
                }
                for (i in pts.indices) {
                    drawLine(Color.White.copy(alpha = 0.08f), pts[i], pts[(i+1) % 5], 1f)
                }
            }
            // 轴线
            (0 until 5).forEach { i ->
                val a = Math.toRadians(-90.0 + i * 72.0)
                drawLine(Color.White.copy(alpha = 0.12f), Offset(cx, cy),
                    Offset(cx + r * cos(a).toFloat(), cy + r * sin(a).toFloat()), 1f)
            }
            // 数据多边形
            val dataPts = elements.mapIndexed { i, (_, v, _) ->
                val a = Math.toRadians(-90.0 + i * 72.0)
                val rr = r * (v / maxVal)
                Offset(cx + rr * cos(a).toFloat(), cy + rr * sin(a).toFloat())
            }
            val path = Path().apply {
                dataPts.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
                close()
            }
            drawPath(path, Brush.radialGradient(
                listOf(Color(0xFFFFD700).copy(alpha = 0.35f), Color(0xFF4FC3F7).copy(alpha = 0.15f)),
                Offset(cx, cy), r
            ))
            drawPath(path, Color(0xFFFFD700).copy(alpha = 0.7f), style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
            // 标签
            elements.forEachIndexed { i, (name, _, color) ->
                val a = Math.toRadians(-90.0 + i * 72.0)
                drawCircle(color, 5f, Offset(cx + r * 1.15f * cos(a).toFloat(), cy + r * 1.15f * sin(a).toFloat()))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            elements.forEach { (name, val_, color) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(name, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(val_.toInt().toString(), color = TextSub, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─── 成就预览 ────────────────────────────────────
@Composable
private fun AchievementsPreview(save: PlayerSave) {
    XCard(Modifier.fillMaxWidth()) {
        Text("修炼成就", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(12.dp))
        val unlocked = AchievementRegistry.all.take(3)
        unlocked.forEach { ach ->
            val done = when(ach.id) {
                "first_case" -> save.casesCompleted >= 1
                "first_ghost" -> save.ghostsCaught >= 1
                else -> false
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (done) "✦" else "◇", color = if (done) GoldBright else TextSub, fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(ach.title, color = if (done) TextMain else TextSub, fontSize = 14.sp,
                        fontWeight = if (done) FontWeight.SemiBold else FontWeight.Normal)
                    Text(ach.desc, color = TextSub, fontSize = 11.sp)
                }
                if (done) Surface(shape = RoundedCornerShape(6.dp), color = Gold.copy(alpha = 0.15f)) {
                    Text("+${ach.xpReward}修为", color = GoldBright, fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}

// ─── 道法宝典 ────────────────────────────────────
@Composable
private fun DaoBaoSection() {
    val techniques = listOf(
        Triple("摄魂术", "以念力锁定游荡魂魄，降低捉鬼难度-2", false),
        Triple("五行相克", "根据鬼怪元素属性选择克制符咒，成功率+25%", false),
        Triple("天时借势", "黄历大吉之日行事，玄机共鸣自动+15", false),
        Triple("地脉感应", "罗盘灵敏度提升，隐藏煞气节点可见", true),
        Triple("三才归一", "天时地利人和全部达到60以上时，触发特殊推演", true)
    )
    XCard(Modifier.fillMaxWidth()) {
        Text("道法宝典", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text("修炼提升境界后自动解锁", color = TextSub, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        techniques.forEach { (name, desc, locked) ->
            Surface(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (locked) Color.White.copy(alpha = 0.03f) else Color(0xFF1A0A2E).copy(alpha = 0.8f),
                border = BorderStroke(1.dp, if (locked) Color.White.copy(alpha = 0.08f) else Color(0xFF7B2FBE).copy(alpha = 0.5f))
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (locked) "🔒" else "✦", fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(name, color = if (locked) TextSub else Color(0xFFCE93D8), fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold)
                        Text(desc, color = TextSub, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
