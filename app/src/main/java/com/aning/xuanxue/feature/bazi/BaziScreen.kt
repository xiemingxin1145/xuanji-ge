package com.aning.xuanxue.feature.bazi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import com.nlf.calendar.EightChar
import com.nlf.calendar.Solar
import java.util.Calendar

private val WU_COLORS = mapOf(
    "木" to Color(0xFF6FA68A), "火" to Color(0xFFB5413A), "土" to Color(0xFFC9A86A),
    "金" to Color(0xFFD9D2C5), "水" to Color(0xFF6E8FB5)
)

private fun ganWu(c: Char) = when (c) {
    '甲', '乙' -> "木"; '丙', '丁' -> "火"; '戊', '己' -> "土"; '庚', '辛' -> "金"; '壬', '癸' -> "水"; else -> ""
}
private fun zhiWu(c: Char) = when (c) {
    '寅', '卯' -> "木"; '巳', '午' -> "火"; '辰', '戌', '丑', '未' -> "土"
    '申', '酉' -> "金"; '子', '亥' -> "水"; else -> ""
}

@Composable
fun BaziScreen(onBack: () -> Unit) {
    val now = remember { Calendar.getInstance() }
    var y by remember { mutableStateOf(now.get(Calendar.YEAR).toString()) }
    var mo by remember { mutableStateOf((now.get(Calendar.MONTH) + 1).toString()) }
    var d by remember { mutableStateOf(now.get(Calendar.DAY_OF_MONTH).toString()) }
    var h by remember { mutableStateOf(now.get(Calendar.HOUR_OF_DAY).toString()) }
    var male by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf<EightChar?>(null) }
    var err by remember { mutableStateOf<String?>(null) }

    XScaffold(title = "八字排盘", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("出生时间（公历）")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumField(y, { y = it }, "年", Modifier.weight(1.4f))
                    NumField(mo, { mo = it }, "月", Modifier.weight(1f))
                    NumField(d, { d = it }, "日", Modifier.weight(1f))
                    NumField(h, { h = it }, "时", Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { male = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (male) Gold else InkSurface2, contentColor = if (male) Ink else TextMain)
                    ) { Text("男命") }
                    Button(
                        onClick = { male = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (!male) Gold else InkSurface2, contentColor = if (!male) Ink else TextMain)
                    ) { Text("女命") }
                }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        err = null
                        try {
                            val solar = Solar.fromYmdHms(
                                y.toInt(), mo.toInt(), d.toInt(), h.toInt(), 0, 0
                            )
                            result = solar.lunar.eightChar
                        } catch (e: Exception) {
                            err = "日期有误，请检查输入"
                            result = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                ) { Text("排  盘", fontWeight = FontWeight.Bold) }
                if (err != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(err!!, color = Cinnabar, fontSize = 13.sp)
                }
            }

            result?.let { ec -> BaziResult(ec, male, y.toIntOrNull() ?: now.get(Calendar.YEAR)) }
        }
    }
}

@Composable
private fun NumField(value: String, onChange: (String) -> Unit, label: String, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = { s -> onChange(s.filter { it.isDigit() }.take(4)) },
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gold, unfocusedBorderColor = TextSub.copy(alpha = 0.5f),
            focusedLabelColor = Gold, unfocusedLabelColor = TextSub,
            focusedTextColor = TextMain, unfocusedTextColor = TextMain, cursorColor = Gold
        ),
        modifier = modifier
    )
}

@Composable
private fun BaziResult(ec: EightChar, male: Boolean, startYear: Int) {
    val pillars = listOf(
        Triple("年柱", ec.year, ec.yearShiShenGan to ec.yearHideGan),
        Triple("月柱", ec.month, ec.monthShiShenGan to ec.monthHideGan),
        Triple("日柱", ec.day, "日元" to ec.dayHideGan),
        Triple("时柱", ec.time, ec.timeShiShenGan to ec.timeHideGan),
    )
    val nayin = listOf(ec.yearNaYin, ec.monthNaYin, ec.dayNaYin, ec.timeNaYin)
    val kong = listOf(ec.yearXunKong, ec.monthXunKong, ec.dayXunKong, ec.timeXunKong)

    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("四柱八字")
        Spacer(Modifier.height(12.dp))
        Row {
            pillars.forEachIndexed { i, p ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(p.first, color = TextSub, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(p.third.first, color = GoldBright, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    GanZhi(p.second)
                    Spacer(Modifier.height(6.dp))
                    Text("藏 ${p.third.second.joinToString("")}", color = TextSub, fontSize = 11.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(nayin[i], color = TextSub, fontSize = 10.sp, textAlign = TextAlign.Center)
                    Text("空 ${kong[i]}", color = TextSub, fontSize = 10.sp)
                }
            }
        }
    }

    // 五行统计
    val all = (ec.year + ec.month + ec.day + ec.time)
    val counts = linkedMapOf("木" to 0, "火" to 0, "土" to 0, "金" to 0, "水" to 0)
    all.forEachIndexed { idx, c ->
        val w = if (idx % 2 == 0) ganWu(c) else zhiWu(c)
        if (w.isNotEmpty()) counts[w] = counts[w]!! + 1
    }
    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("五行统计")
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            counts.forEach { (w, n) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(44.dp)
                            .background(WU_COLORS[w]!!.copy(alpha = 0.18f), RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(w, color = WU_COLORS[w]!!, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(6.dp))
                    Text("$n", color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        val missing = counts.filter { it.value == 0 }.keys
        Spacer(Modifier.height(12.dp))
        Text(
            if (missing.isEmpty()) "五行俱全。" else "五行偏缺：${missing.joinToString("、")}（参考“姓名五行”补益）",
            color = if (missing.isEmpty()) Jade else Cinnabar, fontSize = 13.sp
        )
    }

    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("命主信息")
        Spacer(Modifier.height(8.dp))
        KV("性别", if (male) "男命" else "女命")
        KV("日元", "${ec.dayGan}（${ec.dayWuXing}）")
        KV("胎元", ec.taiYuan)
        KV("命宫", ec.mingGong)
        KV("身宫", ec.shenGong)
        Spacer(Modifier.height(6.dp))
        Text("注：八字仅供参考娱乐，喜用神需结合日主旺衰与调候，非简单“缺即补”。", color = TextSub, fontSize = 11.sp, lineHeight = 16.sp)
    }

    BaziProPanel(ec, male, startYear)
}

@Composable
private fun BaziProPanel(ec: EightChar, male: Boolean, startYear: Int) {
    val forward = remember(ec.year, male) { BaziProCalculator.luckForward(ec.year, male) }
    val luck = remember(ec.month, forward) { BaziProCalculator.majorLuck(ec.month, forward) }
    val hits = remember(ec.year, ec.month, ec.day, ec.time, ec.dayXunKong) {
        BaziProCalculator.shensha(ec.year, ec.month, ec.day, ec.time, ec.dayXunKong)
    }
    val flowYears = remember(startYear) { BaziProCalculator.flowYears(startYear, 10) }

    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("神煞速查")
        Spacer(Modifier.height(8.dp))
        if (hits.isEmpty()) {
            Text("未命中本版内置常用神煞。", color = TextSub, fontSize = 13.sp)
        } else {
            hits.take(16).forEach { hit ->
                Text(hit.name, color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("依据：${hit.basis} → ${hit.target}", color = Jade, fontSize = 12.sp)
                Text(hit.meaning, color = TextMain, fontSize = 12.sp, lineHeight = 18.sp)
                Text(hit.source, color = TextSub, fontSize = 10.sp)
                Spacer(Modifier.height(8.dp))
            }
        }
        Text("本版含天乙、文昌、禄神、羊刃、桃花、驿马、华盖、将星、旬空等常用项。", color = TextSub, fontSize = 11.sp)
    }

    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("大运序列")
        Spacer(Modifier.height(8.dp))
        KV("排法", if (forward) "阳男阴女顺排" else "阴男阳女逆排")
        KV("基准", "月柱 ${ec.month}")
        Spacer(Modifier.height(8.dp))
        luck.forEach { item ->
            Row(Modifier.fillMaxWidth()) {
                Text(item.ageRange, color = TextSub, fontSize = 12.sp, modifier = Modifier.width(72.dp))
                Text(item.pillar, color = GoldBright, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(52.dp))
                Text(item.direction, color = Jade, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("起运岁数须按出生后/前节气差精算，本版先列十年干支序列，供专业用户校正。", color = TextSub, fontSize = 11.sp, lineHeight = 16.sp)
    }

    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("流年十年")
        Spacer(Modifier.height(8.dp))
        flowYears.forEach { fy ->
            Row(Modifier.fillMaxWidth()) {
                Text("${fy.year}", color = TextSub, fontSize = 12.sp, modifier = Modifier.width(56.dp))
                Text(fy.pillar, color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp))
                Text("配大运原局合参", color = TextMain, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("流年不作单独断言，需与大运、原局、岁运并临、合冲刑害综合判断。", color = TextSub, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun GanZhi(gz: String) {
    if (gz.length < 2) { Text(gz, color = TextMain); return }
    val gan = gz[0]; val zhi = gz[1]
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(gan.toString(), color = WU_COLORS[ganWu(gan)] ?: TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(zhi.toString(), color = WU_COLORS[zhiWu(zhi)] ?: TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}
