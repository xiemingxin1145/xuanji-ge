package com.aning.xuanxue.feature.almanac

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import java.util.Calendar

@Composable
fun AlmanacScreen(onBack: () -> Unit) {
    fun today(): Solar {
        val c = Calendar.getInstance()
        return Solar.fromYmd(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }

    var solar by remember { mutableStateOf(today()) }
    val lunar = remember(solar) { solar.lunar }

    XScaffold(title = "老黄历", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            // 日期导航
            XCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { solar = solar.next(-1) }) {
                        Icon(Icons.Filled.ChevronLeft, "前一天", tint = Gold)
                    }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${solar.year} 年 ${solar.month} 月 ${solar.day} 日",
                            color = GoldBright, fontSize = 20.sp, fontWeight = FontWeight.Bold
                        )
                        Text("星期${solar.weekInChinese} · ${solar.xingZuo}座", color = TextSub, fontSize = 13.sp)
                    }
                    IconButton(onClick = { solar = solar.next(1) }) {
                        Icon(Icons.Filled.ChevronRight, "后一天", tint = Gold)
                    }
                }
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick = { solar = today() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text("回到今天", color = Gold, fontSize = 13.sp) }
            }

            // 农历大字
            XCard(Modifier.fillMaxWidth()) {
                Text(
                    "农历${lunar.monthInChinese}月${lunar.dayInChinese}",
                    color = TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                KV("干支", "${lunar.yearInGanZhi}年 ${lunar.monthInGanZhi}月 ${lunar.dayInGanZhi}日")
                KV("生肖", "${lunar.yearShengXiao}年")
                KV("纳音", lunar.dayNaYin)
                val jq = lunar.jieQi
                if (jq.isNotBlank()) KV("节气", jq, GoldBright)
            }

            // 宜忌
            XCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("宜", color = Jade, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(8.dp))
                    Text(lunar.dayYi.joinToString(" ").ifBlank { "诸事不宜" }, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Gold.copy(alpha = 0.15f))
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("忌", color = Cinnabar, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(8.dp))
                    Text(lunar.dayJi.joinToString(" ").ifBlank { "诸事皆宜" }, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
                }
            }

            // 冲煞 / 值神
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("冲煞 · 值神")
                Spacer(Modifier.height(8.dp))
                KV("冲煞", "${lunar.dayChongDesc} 煞${lunar.daySha}")
                KV("值神", "${lunar.dayTianShen}（${lunar.dayTianShenLuck}）")
                KV("星宿", "${lunar.xiu}${lunar.zheng}${lunar.animal}")
                KV("彭祖", "${lunar.pengZuGan}；${lunar.pengZuZhi}")
            }

            // 吉神方位
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("吉神方位")
                Spacer(Modifier.height(8.dp))
                KV("喜神", lunar.dayPositionXiDesc, GoldBright)
                KV("财神", lunar.dayPositionCaiDesc, GoldBright)
                KV("福神", lunar.dayPositionFuDesc, GoldBright)
                KV("阳贵", lunar.dayPositionYangGuiDesc)
                KV("阴贵", lunar.dayPositionYinGuiDesc)
            }
        }
    }
}
