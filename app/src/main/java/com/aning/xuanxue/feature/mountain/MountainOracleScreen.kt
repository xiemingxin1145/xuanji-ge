package com.aning.xuanxue.feature.mountain

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.KV
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold

@Composable
fun MountainOracleScreen(
    onBack: () -> Unit,
    onOpenFlyingStar: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<MountainOracle?>(null) }
    val list = remember(query) { MountainOracleRepository.search(query) }

    XScaffold(title = "二十四山向断语", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("二十四山向索引")
                Spacer(Modifier.height(8.dp))
                Text(
                    "列山、向、度数、卦宫、元龙、玄空阴阳与实务检查项。断语只列规则，不替专业使用者下最终结论。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        selected = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("搜索：子、壬、坎、天元、顺飞、乾宫……", color = TextSub, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = TextSub.copy(alpha = 0.35f),
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain,
                        cursorColor = Gold
                    )
                )
            }

            if (selected == null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MountainOracleRepository.all.forEach { item ->
                        Button(
                            onClick = { selected = item },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                        ) { Text(item.mountain) }
                    }
                }

                Text("结果：${list.size} 山", color = TextSub, fontSize = 12.sp)
                list.forEach { item ->
                    XCard(Modifier.fillMaxWidth()) {
                        Row {
                            Text("${item.mountain}山${item.facing}向", color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Text(item.range, color = TextSub, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("${item.palace}宫 · ${item.yuanLong} · ${item.yinYang} · ${item.elementHint}", color = Jade, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(item.usage, color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { selected = item },
                            colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                        ) { Text("看专业断语") }
                    }
                }
            } else {
                val item = selected!!
                XCard(Modifier.fillMaxWidth()) {
                    Text("${item.mountain}山${item.facing}向", color = GoldBright, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(item.range, color = TextSub, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    KV("卦宫", item.palace)
                    KV("元龙", item.yuanLong)
                    KV("玄空阴阳", item.yinYang, valueColor = if (item.yinYang.startsWith("阳")) GoldBright else Jade)
                    KV("五行提示", item.elementHint)
                }

                XCard(Modifier.fillMaxWidth()) {
                    SectionTitle("山向断法")
                    Spacer(Modifier.height(8.dp))
                    Text(item.usage, color = TextMain, fontSize = 14.sp, lineHeight = 22.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(item.flyingStarHint, color = GoldBright, fontSize = 13.sp, lineHeight = 20.sp)
                }

                XCard(Modifier.fillMaxWidth()) {
                    SectionTitle("实务检查项")
                    Spacer(Modifier.height(8.dp))
                    item.cautions.forEach { c ->
                        Text("· $c", color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("说明：本库是专业索引，具体取断仍须配合元运、飞星、峦头、流年流月。", color = TextSub, fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onOpenFlyingStar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                    ) { Text("开飞星盘") }
                    Button(
                        onClick = { selected = null },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                    ) { Text("返回列表") }
                }
            }
        }
    }
}
