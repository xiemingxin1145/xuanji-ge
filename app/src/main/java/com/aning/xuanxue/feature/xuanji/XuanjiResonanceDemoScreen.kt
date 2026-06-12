package com.aning.xuanxue.feature.xuanji


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.*

/**
 * 玄机共鸣测试演示界面
 * 零自主创建，用于快速验证天时·地利·人和系统
 * 后期可替换为真实数据接入
 */
@Composable
fun XuanjiResonanceDemoScreen(onBack: () -> Unit) {
    var tianShi by remember { mutableStateOf(75) }
    var diLi by remember { mutableStateOf(68) }
    var renHe by remember { mutableStateOf(82) }

    val result = remember(tianShi, diLi, renHe) {
        XuanjiResonance.calculate(
            tianShiRaw = tianShi,
            diLiRaw = diLi,
            renHeRaw = renHe
        )
    }

    XScaffold(
        title = "玄机共鸣测试",
        onBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 说明
            Text(
                "此界面用于演示核心引擎。实际使用时会自动从黄历、罗盘、八字读取真实数据。",
                color = TextSub,
                fontSize = 13.sp
            )

            // 输入区
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = InkSurface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("手动调节参数（测试用）", color = GoldBright, fontWeight = FontWeight.SemiBold)

                    SliderWithLabel("天时（黄历）", tianShi, onValueChange = { tianShi = it.toInt() })
                    SliderWithLabel("地利（罗盘）", diLi, onValueChange = { diLi = it.toInt() })
                    SliderWithLabel("人和（八字/姓名）", renHe, onValueChange = { renHe = it.toInt() })
                }
            }

            // 结果展示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = InkSurface),
                border = androidx.compose.foundation.BorderStroke(2.dp, when (result.level) {
                    XuanjiResonance.Level.EPIC -> Color(0xFFD62828)
                    XuanjiResonance.Level.STRONG -> Color(0xFFF77F00)
                    XuanjiResonance.Level.MODERATE -> Color(0xFFF4D35E)
                    else -> Gold.copy(alpha = 0.5f)
                })
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("玄机共鸣等级", color = TextSub, fontSize = 14.sp)
                        Text(
                            result.level.displayName,
                            color = when (result.level) {
                                XuanjiResonance.Level.EPIC -> Color(0xFFD62828)
                                XuanjiResonance.Level.STRONG -> Color(0xFFF77F00)
                                XuanjiResonance.Level.MODERATE -> Color(0xFFF4D35E)
                                else -> TextMain
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "综合评分：${result.total} / 100",
                        color = TextMain,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        result.mythicDesc,
                        color = TextMain,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "游戏效果：${result.gameEffect}",
                        color = Jade,
                        fontSize = 14.sp
                    )

                    Text(
                        "倍率：×${result.multiplier}",
                        color = GoldBright,
                        fontSize = 14.sp
                    )
                }
            }

            // 说明
            Text(
                "零提示：后期会把滑块替换成真实数据源（黄历每日自动计算 + 罗盘实时指向 + 八字羁绊匹配）。现在先验证引擎是否正常工作。",
                color = TextSub.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SliderWithLabel(
    label: String,
    value: Int,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextMain)
            Text("$value", color = GoldBright, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = GoldBright,
                activeTrackColor = Gold,
                inactiveTrackColor = Gold.copy(alpha = 0.3f)
            )
        )
    }
}