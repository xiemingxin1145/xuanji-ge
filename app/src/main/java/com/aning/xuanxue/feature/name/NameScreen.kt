package com.aning.xuanxue.feature.name

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import java.util.Calendar

private val WU_COLORS = mapOf(
    "木" to Color(0xFF6FA68A), "火" to Color(0xFFB5413A), "土" to Color(0xFFC9A86A),
    "金" to Color(0xFFD9D2C5), "水" to Color(0xFF6E8FB5)
)

// 五行常用起名字库
private val NAME_LIB = mapOf(
    "木" to "林 森 楠 桐 桦 松 柏 梓 榆 柳 荣 萱 蕊 笙 茉 莉 菲 芸 蓁 棠".split(" "),
    "火" to "炎 烨 煜 灿 烁 晗 旭 昭 晖 晨 炜 灵 彤 晴 昕 晟 焕 暖 烽 朗".split(" "),
    "土" to "坤 培 垚 城 峰 岳 屹 峻 岚 玮 瑶 瑾 璧 圭 维 怡 韵 嵘 山 砚".split(" "),
    "金" to "鑫 铭 钧 锐 锋 钰 铮 锦 钟 铎 镇 钢 锌 铠 钦 锴 玺 鋆 钲 铉".split(" "),
    "水" to "江 海 洋 涛 浩 泽 润 渊 沛 沐 沁 泓 涵 淼 清 澄 漪 霖 雯 霁".split(" "),
)

private fun ganWu(c: Char) = when (c) {
    '甲', '乙' -> "木"; '丙', '丁' -> "火"; '戊', '己' -> "土"; '庚', '辛' -> "金"; '壬', '癸' -> "水"; else -> ""
}
private fun zhiWu(c: Char) = when (c) {
    '寅', '卯' -> "木"; '巳', '午' -> "火"; '辰', '戌', '丑', '未' -> "土"
    '申', '酉' -> "金"; '子', '亥' -> "水"; else -> ""
}

@Composable
fun NameScreen(onBack: () -> Unit) {
    val now = remember { Calendar.getInstance() }
    var surname by remember { mutableStateOf("") }
    var y by remember { mutableStateOf(now.get(Calendar.YEAR).toString()) }
    var mo by remember { mutableStateOf((now.get(Calendar.MONTH) + 1).toString()) }
    var d by remember { mutableStateOf(now.get(Calendar.DAY_OF_MONTH).toString()) }
    var h by remember { mutableStateOf(now.get(Calendar.HOUR_OF_DAY).toString()) }
    var counts by remember { mutableStateOf<Map<String, Int>?>(null) }
    var err by remember { mutableStateOf<String?>(null) }

    XScaffold(title = "姓名五行", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("生辰 · 取名补益")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it.take(2) },
                    label = { Text("姓（选填）", fontSize = 12.sp) },
                    singleLine = true,
                    colors = goldFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumField(y, { y = it }, "年", Modifier.weight(1.4f))
                    NumField(mo, { mo = it }, "月", Modifier.weight(1f))
                    NumField(d, { d = it }, "日", Modifier.weight(1f))
                    NumField(h, { h = it }, "时", Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        err = null
                        try {
                            val ec = Solar.fromYmdHms(y.toInt(), mo.toInt(), d.toInt(), h.toInt(), 0, 0).lunar.eightChar
                            val all = ec.year + ec.month + ec.day + ec.time
                            val map = linkedMapOf("木" to 0, "火" to 0, "土" to 0, "金" to 0, "水" to 0)
                            all.forEachIndexed { i, c ->
                                val w = if (i % 2 == 0) ganWu(c) else zhiWu(c)
                                if (w.isNotEmpty()) map[w] = map[w]!! + 1
                            }
                            counts = map
                        } catch (e: Exception) {
                            err = "日期有误，请检查输入"; counts = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                ) { Text("分析五行", fontWeight = FontWeight.Bold) }
                if (err != null) {
                    Spacer(Modifier.height(8.dp)); Text(err!!, color = Cinnabar, fontSize = 13.sp)
                }
            }

            counts?.let { c -> Result(c, surname) }
        }
    }
}

@Composable
private fun Result(counts: Map<String, Int>, surname: String) {
    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("五行分布")
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
    }

    val minN = counts.values.min()
    val weak = counts.filter { it.value == minN }.keys.toList()
    XCard(Modifier.fillMaxWidth()) {
        SectionTitle("补益建议")
        Spacer(Modifier.height(8.dp))
        Text(
            "八字中最弱者为「${weak.joinToString("、")}」，取名可优先选用该五行属性的字以作补益。",
            color = TextMain, fontSize = 14.sp, lineHeight = 21.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "注：此为简化版“缺即补”思路；严格喜用神须结合日主旺衰、调候与格局，仅供取名参考。",
            color = TextSub, fontSize = 11.sp, lineHeight = 16.sp
        )
    }

    weak.forEach { w ->
        XCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(30.dp)
                        .background(WU_COLORS[w]!!.copy(alpha = 0.2f), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) { Text(w, color = WU_COLORS[w]!!, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(8.dp))
                Text("属$w 推荐用字", color = GoldBright, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            FlowChips(NAME_LIB[w] ?: emptyList(), surname)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowChips(chars: List<String>, surname: String) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chars.forEach { ch ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = InkSurface2,
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.25f))
            ) {
                Text(
                    if (surname.isNotBlank()) "$surname$ch" else ch,
                    color = TextMain, fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
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
        colors = goldFieldColors(),
        modifier = modifier
    )
}

@Composable
private fun goldFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Gold, unfocusedBorderColor = TextSub.copy(alpha = 0.5f),
    focusedLabelColor = Gold, unfocusedLabelColor = TextSub,
    focusedTextColor = TextMain, unfocusedTextColor = TextMain, cursorColor = Gold
)
