package com.aning.xuanxue.feature.xuanhuang

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.InkSurface
import com.aning.xuanxue.ui.InkSurface2
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.KV
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold
import com.nlf.calendar.Solar
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * 玄机阁 V2.0：天地玄黄入口。
 * 目标：把工具、神话、动态特效、轻游戏成长统一成一个可继续扩展的宇宙骨架。
 */
private data class XuanhuangSnapshot(
    val solarText: String,
    val lunarText: String,
    val ganZhiText: String,
    val yiText: String,
    val jiText: String,
    val mainElement: String,
    val tianShi: Int,
    val diLi: Int,
    val renHe: Int,
    val resonance: XuanjiResonance.Result
)

private fun todaySnapshot(): XuanhuangSnapshot {
    val c = Calendar.getInstance()
    val solar = Solar.fromYmdHms(
        c.get(Calendar.YEAR),
        c.get(Calendar.MONTH) + 1,
        c.get(Calendar.DAY_OF_MONTH),
        c.get(Calendar.HOUR_OF_DAY),
        c.get(Calendar.MINUTE),
        c.get(Calendar.SECOND)
    )
    val lunar = solar.lunar
    val dayGan = lunar.dayInGanZhi.firstOrNull() ?: '甲'
    val element = elementOfDayGan(dayGan)
    val tianShi = XuanjiResonance.estimateTianShiFromAlmanac(lunar.dayYi.size, lunar.dayJi.size)
    val diLi = (58 + solar.day % 21).coerceIn(35, 92)
    val renHe = when (element) {
        "木" -> 76
        "火" -> 72
        "土" -> 68
        "金" -> 64
        else -> 70
    }
    val resonance = XuanjiResonance.calculate(tianShi, diLi, renHe)
    return XuanhuangSnapshot(
        solarText = "${solar.year}年${solar.month}月${solar.day}日 · 星期${solar.weekInChinese}",
        lunarText = "农历${lunar.monthInChinese}月${lunar.dayInChinese}",
        ganZhiText = "${lunar.yearInGanZhi}年 ${lunar.monthInGanZhi}月 ${lunar.dayInGanZhi}日",
        yiText = lunar.dayYi.take(6).joinToString("、").ifBlank { "谨慎行事" },
        jiText = lunar.dayJi.take(6).joinToString("、").ifBlank { "无明显忌项" },
        mainElement = element,
        tianShi = tianShi,
        diLi = diLi,
        renHe = renHe,
        resonance = resonance
    )
}

private fun elementOfDayGan(gan: Char): String = when (gan) {
    '甲', '乙' -> "木"
    '丙', '丁' -> "火"
    '戊', '己' -> "土"
    '庚', '辛' -> "金"
    else -> "水"
}

private fun elementColor(element: String): Color = when (element) {
    "木" -> Jade
    "火" -> Cinnabar
    "土" -> Gold
    "金" -> Color(0xFFD9D2C5)
    else -> Color(0xFF6E8FB5)
}

@Composable
fun XuanhuangDashboardScreen(onBack: () -> Unit, go: (String) -> Unit) {
    val snapshot = remember { todaySnapshot() }
    XScaffold(title = "天地玄黄", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XuanhuangSkyPanel(snapshot)

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日玄象")
                Spacer(Modifier.height(10.dp))
                Text(snapshot.resonance.mythicDesc, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
                Spacer(Modifier.height(10.dp))
                KV("玄气值", "${snapshot.resonance.total} / 100 · ${snapshot.resonance.level.displayName}", GoldBright)
                KV("主五行", snapshot.mainElement, elementColor(snapshot.mainElement))
                KV("天时", snapshot.tianShi.toString(), GoldBright)
                KV("地利", snapshot.diLi.toString(), Jade)
                KV("人和", snapshot.renHe.toString(), TextMain)
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("V2.0 四大入口")
                Spacer(Modifier.height(12.dp))
                PortalButton("山海地脉", "山川河流 · 地脉发光 · 方位秘境") { go("earth_vein") }
                PortalButton("命格成长", "木火土金水 · 属性养成 · 今日共鸣") { go("fate_growth") }
                PortalButton("山海图鉴", "神兽 · 神祇 · 法器 · 秘境收集") { go("shanhai_atlas") }
                PortalButton("玄算验真", "黄历 / 罗盘 / 八字 / 飞星步骤验算") { go("xuan_verify") }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("游戏化总线")
                Spacer(Modifier.height(8.dp))
                Text("每天打开 APP，天时、地利、人和会自动生成今日玄气；工具结果不再只是文字，而会驱动地脉、命格、图鉴、任务和 AI 玄师剧情。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun PortalButton(title: String, sub: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = InkSurface2
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(sub, color = TextSub, fontSize = 12.sp)
        }
    }
}

@Composable
private fun XuanhuangSkyPanel(snapshot: XuanhuangSnapshot) {
    val infinite = rememberInfiniteTransition(label = "xuanhuangSky")
    val rot by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(52000, easing = LinearEasing), RepeatMode.Restart),
        label = "skyRot"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.52f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
        label = "skyPulse"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
            .clip(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        color = InkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.35f + pulse * 0.2f))
    ) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height * 0.44f
                val r = min(size.width, size.height) * 0.42f

                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF050712), Ink, Color(0xFF1C1B24))
                    )
                )

                // 宇宙星轨
                rotate(rot, Offset(cx, cy)) {
                    repeat(5) { i ->
                        drawCircle(
                            color = Gold.copy(alpha = 0.06f + i * 0.025f),
                            radius = r * (0.62f + i * 0.19f),
                            center = Offset(cx, cy)
                        )
                    }
                    repeat(28) { i ->
                        val angle = (i * 360f / 28f) * PI / 180f
                        val rr = r * (0.78f + (i % 4) * 0.08f)
                        drawCircle(
                            color = if (i % 7 == 0) Cinnabar.copy(alpha = 0.85f) else GoldBright.copy(alpha = 0.65f),
                            radius = if (i % 7 == 0) 4.2f else 2.4f,
                            center = Offset(cx + cos(angle).toFloat() * rr, cy + sin(angle).toFloat() * rr)
                        )
                    }
                }

                // 山川河流
                val mountainY = size.height * 0.73f
                repeat(7) { i ->
                    val x = size.width * i / 6f
                    drawLine(
                        color = Gold.copy(alpha = 0.18f),
                        start = Offset(x - size.width * 0.12f, mountainY + (i % 2) * 16f),
                        end = Offset(x + size.width * 0.12f, mountainY - 52f - (i % 3) * 18f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Gold.copy(alpha = 0.14f),
                        start = Offset(x + size.width * 0.12f, mountainY - 52f - (i % 3) * 18f),
                        end = Offset(x + size.width * 0.28f, mountainY + 12f),
                        strokeWidth = 4f
                    )
                }
                repeat(3) { i ->
                    val y = size.height * (0.78f + i * 0.055f)
                    drawLine(
                        color = Color(0xFF4FC3F7).copy(alpha = 0.20f + pulse * 0.18f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y - 28f + i * 12f),
                        strokeWidth = 5f
                    )
                }

                // 地脉光点
                repeat(9) { i ->
                    val x = size.width * (0.1f + i * 0.1f)
                    val y = size.height * (0.72f + (i % 3) * 0.055f)
                    drawCircle(GoldBright.copy(alpha = pulse), radius = 5f + i % 3, center = Offset(x, y))
                }
            }

            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 28.dp, start = 18.dp, end = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("天地玄黄", color = GoldBright, fontSize = 30.sp, fontWeight = FontWeight.Bold, letterSpacing = 5.sp)
                Spacer(Modifier.height(6.dp))
                Text("观天时 · 测地脉 · 演命格 · 入山海", color = TextSub, fontSize = 12.sp, textAlign = TextAlign.Center)
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                color = Ink.copy(alpha = 0.78f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.25f))
            ) {
                Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(snapshot.solarText, color = GoldBright, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("${snapshot.lunarText} · ${snapshot.ganZhiText}", color = TextMain, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(6.dp))
                    Text("玄气 ${snapshot.resonance.total} · ${snapshot.resonance.level.displayName}", color = elementColor(snapshot.mainElement), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EarthVeinScreen(onBack: () -> Unit) {
    XScaffold(title = "山海地脉", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("山川河流 · 地脉演变")
                Spacer(Modifier.height(8.dp))
                Text("这里是 V2.0 的地利视觉层：未来会接入罗盘角度，自动点亮正北坎水、正东震木、正南离火、西方兑金等不同地脉。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
            EarthVeinCanvas()
            listOf(
                "北冥寒水 · 坎水玄脉 · 主潜藏、智慧、静观",
                "东荒雷泽 · 震木龙脉 · 主生发、行动、开局",
                "南离火域 · 朱雀天脉 · 主灵感、名声、决断",
                "西岳金川 · 白虎金脉 · 主收束、规则、攻伐",
                "中州玄土 · 九宫地脉 · 主根基、稳定、承载"
            ).forEach { text ->
                XCard(Modifier.fillMaxWidth()) { Text(text, color = TextMain, fontSize = 14.sp, lineHeight = 20.sp) }
            }
        }
    }
}

@Composable
private fun EarthVeinCanvas() {
    val infinite = rememberInfiniteTransition(label = "earthVein")
    val pulse by infinite.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "veinPulse"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(22.dp),
        color = InkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
    ) {
        Canvas(Modifier.fillMaxSize().padding(18.dp)) {
            val w = size.width
            val h = size.height
            drawRect(Brush.verticalGradient(listOf(Color(0xFF07111A), InkSurface)))
            repeat(6) { i ->
                val baseX = w * i / 5f
                drawLine(Gold.copy(alpha = 0.22f), Offset(baseX - 80f, h * 0.72f), Offset(baseX + 35f, h * 0.28f - i * 5f), 5f)
                drawLine(Gold.copy(alpha = 0.14f), Offset(baseX + 35f, h * 0.28f - i * 5f), Offset(baseX + 130f, h * 0.72f), 5f)
            }
            repeat(4) { i ->
                val y = h * (0.62f + i * 0.07f)
                drawLine(Color(0xFF4FC3F7).copy(alpha = 0.25f + pulse * 0.28f), Offset(0f, y), Offset(w, y - 36f + i * 18f), 6f)
            }
            val nodes = listOf(
                Offset(w * 0.18f, h * 0.68f), Offset(w * 0.34f, h * 0.54f), Offset(w * 0.52f, h * 0.61f),
                Offset(w * 0.70f, h * 0.45f), Offset(w * 0.84f, h * 0.68f)
            )
            nodes.zipWithNext().forEach { (a, b) -> drawLine(GoldBright.copy(alpha = pulse), a, b, 4f) }
            nodes.forEachIndexed { i, p ->
                drawCircle(if (i == 2) Cinnabar.copy(alpha = pulse) else GoldBright.copy(alpha = pulse), 9f + i % 2 * 3f, p)
                drawCircle(Gold.copy(alpha = 0.18f), 28f, p)
            }
        }
    }
}

@Composable
fun FateGrowthScreen(onBack: () -> Unit) {
    val snapshot = remember { todaySnapshot() }
    val values = listOf(
        "木 · 悟性" to if (snapshot.mainElement == "木") 86 else 58,
        "火 · 灵感" to if (snapshot.mainElement == "火") 84 else 62,
        "土 · 根基" to if (snapshot.mainElement == "土") 82 else 66,
        "金 · 决断" to if (snapshot.mainElement == "金") 80 else 60,
        "水 · 智慧" to if (snapshot.mainElement == "水") 88 else 64
    )
    XScaffold(title = "命格成长", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("命格五行")
                Spacer(Modifier.height(8.dp))
                Text("今日主五行为「${snapshot.mainElement}」。以后八字排盘、姓名五行、符卡任务都会反哺这里，形成长期成长属性。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
            values.forEach { (name, value) -> EnergyBar(name, value) }
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日增益")
                Spacer(Modifier.height(8.dp))
                Text(snapshot.resonance.gameEffect, color = Jade, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun EnergyBar(name: String, value: Int) {
    XCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = GoldBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("$value", color = TextMain, fontSize = 15.sp)
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(InkSurface2)
        ) {
            Box(
                Modifier
                    .fillMaxWidth((value / 100f).coerceIn(0.05f, 1f))
                    .fillMaxHeight()
                    .background(Brush.horizontalGradient(listOf(Gold, GoldBright)))
            )
        }
    }
}

@Composable
fun ShanhaiAtlasScreen(onBack: () -> Unit) {
    val items = listOf(
        "青龙" to "东方震木 · 生发开局 · 地脉守护",
        "白虎" to "西方兑金 · 决断攻伐 · 规则之刃",
        "朱雀" to "南方离火 · 灵感声名 · 火域神鸟",
        "玄武" to "北方坎水 · 潜藏智慧 · 寒水玄甲",
        "应龙" to "行云布雨 · 山海天脉 · 高阶图鉴",
        "烛龙" to "开目为昼 · 闭目为夜 · 宇宙星空",
        "陆吾" to "昆仑神司 · 九部天门 · 秘境守将",
        "河图洛书" to "数理根基 · 九宫演算 · 玄算核心"
    )
    XScaffold(title = "山海图鉴", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("神话收集")
                Spacer(Modifier.height(8.dp))
                Text("先做文字图鉴和卡面占位，后续每个条目都能替换成精美插图、解锁动画、AI 背景故事。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
            items.forEachIndexed { index, item ->
                XCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Brush.verticalGradient(listOf(Gold.copy(alpha = 0.34f), Cinnabar.copy(alpha = 0.16f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.first, color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(item.second, color = TextSub, fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun XuanSuanVerifyScreen(onBack: () -> Unit) {
    val snapshot = remember { todaySnapshot() }
    XScaffold(title = "玄算验真", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日数据")
                Spacer(Modifier.height(8.dp))
                KV("公历", snapshot.solarText)
                KV("农历", snapshot.lunarText)
                KV("干支", snapshot.ganZhiText)
                KV("宜", snapshot.yiText, Jade)
                KV("忌", snapshot.jiText, Cinnabar)
            }
            VerifyBlock("天时验算", listOf(
                "读取老黄历今日宜忌数量。",
                "基础分 50；每个宜项加分，每个忌项扣分。",
                "今日天时 = ${snapshot.tianShi}。"
            ))
            VerifyBlock("地利验算", listOf(
                "当前版本先以日期生成稳定测试值。",
                "后续接入风水罗盘角度、二十四山、磁场精度。",
                "今日地利 = ${snapshot.diLi}。"
            ))
            VerifyBlock("人和验算", listOf(
                "读取今日日干，映射木火土金水主五行。",
                "后续接入用户八字、姓名五行、命格档案。",
                "今日人和 = ${snapshot.renHe}，主五行 = ${snapshot.mainElement}。"
            ))
            VerifyBlock("玄气总分", listOf(
                "天时权重 40%，地利权重 30%，人和权重 30%。",
                "公式：玄气 = 天时×0.4 + 地利×0.3 + 人和×0.3。",
                "今日玄气 = ${snapshot.resonance.total}，等级 = ${snapshot.resonance.level.displayName}。"
            ))
        }
    }
}

@Composable
private fun VerifyBlock(title: String, lines: List<String>) {
    XCard(Modifier.fillMaxWidth()) {
        SectionTitle(title)
        Spacer(Modifier.height(8.dp))
        lines.forEachIndexed { i, line ->
            Text("${i + 1}. $line", color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}
