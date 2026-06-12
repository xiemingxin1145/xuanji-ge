package com.aning.xuanxue.feature.iching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import kotlin.random.Random

@Composable
fun IChingScreen(onBack: () -> Unit) {
    // 六爻 初->上
    var yaos by remember { mutableStateOf<List<Yao>?>(null) }

    XScaffold(title = "易经起卦", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                Text(
                    "心中默念所问之事，诚则灵。点“摇卦”以三钱六摇成卦。",
                    color = TextSub, fontSize = 13.sp, lineHeight = 19.sp
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        yaos = (0 until 6).map {
                            IChingData.tossLine { if (Random.nextBoolean()) 3 else 2 }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                ) { Text("摇  卦", fontWeight = FontWeight.Bold) }
            }

            yaos?.let { ys ->
                val ben = IChingData.lookup(ys.map { it.yang })
                val movingIdx = ys.mapIndexedNotNull { i, y -> if (y.moving) i else null }
                val bian = if (movingIdx.isEmpty()) null
                else IChingData.lookup(ys.map { if (it.moving) !it.yang else it.yang })

                XCard(Modifier.fillMaxWidth()) {
                    SectionTitle("卦象")
                    Spacer(Modifier.height(14.dp))
                    // 由上(上爻)到下(初爻)绘制
                    ys.reversed().forEachIndexed { revI, y ->
                        val pos = 5 - revI // 实际爻位 0..5
                        YaoRow(y, pos)
                        Spacer(Modifier.height(8.dp))
                    }
                }

                XCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("本卦  ", color = TextSub, fontSize = 14.sp)
                        Text(ben.name, color = GoldBright, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(ben.brief, color = TextMain, fontSize = 14.sp, lineHeight = 21.sp)
                }

                if (bian != null) {
                    val moveNames = movingIdx.joinToString("、") { yaoName(it) }
                    XCard(Modifier.fillMaxWidth()) {
                        Text("动爻：$moveNames", color = Cinnabar, fontSize = 13.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("变卦  ", color = TextSub, fontSize = 14.sp)
                            Text(bian.name, color = GoldBright, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(bian.brief, color = TextMain, fontSize = 14.sp, lineHeight = 21.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("本卦为现状，变卦示趋向；动爻处即变化所在。", color = TextSub, fontSize = 12.sp)
                    }
                } else {
                    XCard(Modifier.fillMaxWidth()) {
                        Text("六爻皆静，无动爻，以本卦卦辞断之。", color = TextSub, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun yaoName(pos: Int): String =
    listOf("初爻", "二爻", "三爻", "四爻", "五爻", "上爻")[pos]

@Composable
private fun YaoRow(y: Yao, pos: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(28.dp)) {
            Text(listOf("初", "二", "三", "四", "五", "上")[pos], color = TextSub, fontSize = 12.sp)
        }
        Spacer(Modifier.width(8.dp))
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val color = if (y.moving) Cinnabar else Gold
            if (y.yang) {
                Bar(color, Modifier.weight(1f))
            } else {
                Bar(color, Modifier.weight(1f))
                Spacer(Modifier.weight(0.18f))
                Bar(color, Modifier.weight(1f))
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.width(20.dp)) {
            if (y.moving) Text(if (y.yang) "○" else "×", color = Cinnabar, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Bar(color: Color, modifier: Modifier) {
    Box(
        modifier
            .height(14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
    )
}
