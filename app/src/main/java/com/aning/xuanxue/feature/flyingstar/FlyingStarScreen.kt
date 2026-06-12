package com.aning.xuanxue.feature.flyingstar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import java.util.Calendar

private fun luckColor(luck: Int): Color = when {
    luck > 0 -> Jade
    luck < 0 -> Cinnabar
    else -> GoldBright
}

/** 星色：旺星金、生气玉、五黄二黑朱砂、余者米白 */
private fun starColor(star: Int, yun: Int): Color = when {
    star == yun -> GoldBright
    star == (yun % 9) + 1 -> Jade
    star == 5 || star == 2 -> Cinnabar
    else -> TextMain
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlyingStarScreen(onBack: () -> Unit) {
    val defaultYun = remember { yunOfYear(Calendar.getInstance().get(Calendar.YEAR)) }
    var yun by remember { mutableIntStateOf(defaultYun) }
    var sittingName by remember { mutableStateOf("子") }
    var useReplacement by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var sheetGrid by remember { mutableStateOf<Int?>(null) }

    val result = remember(yun, sittingName, useReplacement) {
        FlyingStarCalculator.compute(yun, sittingName, useReplacement)
    }
    val yunInfo = remember(yun) { YUN_LIST.first { it.num == yun } }

    XScaffold(title = "玄空飞星 · 三元理气", onBack = onBack) { padding ->
        ScrollColumn(padding) {

            // ---- 元运选择 ----
            SectionTitle("元运")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..9).forEach { n ->
                    val sel = n == yun
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) Gold else InkSurface2)
                            .border(1.dp, Gold.copy(alpha = if (sel) 0f else 0.3f), RoundedCornerShape(8.dp))
                            .clickable { yun = n },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$n", color = if (sel) Ink else TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(
                "${yunInfo.yuan} · ${yunInfo.starName}运 · ${yunInfo.startYear}–${yunInfo.endYear}",
                color = TextSub, fontSize = 12.sp
            )

            // ---- 坐山选择 + 替卦 ----
            SectionTitle("坐向")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    OutlinedButton(
                        onClick = { menuOpen = true },
                        border = BorderStroke(1.dp, Gold)
                    ) { Text("坐 ${result.sitting.name} 山", color = Gold) }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        FlyingStarCalculator.mountainNames().forEach { name ->
                            DropdownMenuItem(
                                text = { Text("$name 山") },
                                onClick = { sittingName = name; menuOpen = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text("向 ${result.facing.name}", color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("兼向起星（替卦）", color = TextMain, fontSize = 13.sp)
                Spacer(Modifier.width(8.dp))
                Switch(checked = useReplacement, onCheckedChange = { useReplacement = it })
            }
            if (useReplacement) {
                Text(
                    "替星口诀采用通行《青囊》挨星诀，无常、章氏等派略有出入，仅供参考。",
                    color = TextSub, fontSize = 11.sp
                )
            }

            // ---- 格局 ----
            XCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("格局", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text(result.pattern, color = luckColor(result.patternLuck), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(6.dp))
                Text(result.patternDesc, color = TextMain, fontSize = 13.sp)
                Text(
                    "坐山 ${result.sitting.name}(${result.sitting.gua}宫) · 向首 ${result.facing.name}(${result.facing.gua}宫)" +
                        " · 山星${result.shanCenter}入中${if (result.shanForward) "顺" else "逆"}飞" +
                        " · 向星${result.xiangCenter}入中${if (result.xiangForward) "顺" else "逆"}飞",
                    color = TextSub, fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
                result.extraNotes.forEach {
                    Text("· $it", color = Jade, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // ---- 九宫盘 ----
            SectionTitle("九宫飞星盘（上南下北）")
            val cells = result.palaceStars()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (c in 0 until 3) {
                            val grid = r * 3 + c
                            PalaceCell(
                                ps = cells[grid],
                                yun = yun,
                                isSit = grid == result.sittingGrid,
                                isFace = grid == result.facingGrid,
                                modifier = Modifier.weight(1f),
                                onClick = { if (cells[grid].palace.gua != "中") sheetGrid = grid }
                            )
                        }
                    }
                }
            }

            // ---- 图例 ----
            XCard(Modifier.fillMaxWidth()) {
                Text("读盘", color = GoldBright, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("每宫左上为山星（管人丁），右上为向星（管财禄），下中为运星（地盘）。", color = TextSub, fontSize = 12.sp)
                Spacer(Modifier.height(2.dp))
                Row {
                    Text("旺星", color = GoldBright, fontSize = 12.sp); Text("／", color = TextSub, fontSize = 12.sp)
                    Text("生气", color = Jade, fontSize = 12.sp); Text("／", color = TextSub, fontSize = 12.sp)
                    Text("二黑五黄等凶星", color = Cinnabar, fontSize = 12.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text("※ 三元玄空为传统堪舆理气之学，断语引自《紫白诀》《玄空秘旨》等，作研习与文化参考，吉凶须配峦头环境论断。", color = TextSub, fontSize = 11.sp)
            }
        }
    }

    sheetGrid?.let { grid ->
        PalaceSheet(ps = result.palaceStars()[grid], yun = yun) { sheetGrid = null }
    }
}

@Composable
private fun PalaceCell(
    ps: PalaceStars,
    yun: Int,
    isSit: Boolean,
    isFace: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val isCenter = ps.palace.gua == "中"
    val borderColor = when {
        isSit -> Cinnabar
        isFace -> Jade
        else -> Gold.copy(alpha = 0.25f)
    }
    Box(
        modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(InkSurface)
            .border(if (isSit || isFace) 2.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .then(if (isCenter) Modifier else Modifier.clickable(onClick = onClick))
            .padding(6.dp)
    ) {
        // 方位 + 坐/向标
        Text(
            ps.palace.dir + when { isSit -> " 坐"; isFace -> " 向"; else -> "" },
            color = when { isSit -> Cinnabar; isFace -> Jade; else -> TextSub },
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        // 山星左、向星右
        Row(Modifier.align(Alignment.Center).padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${ps.shan}", color = starColor(ps.shan, yun), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${ps.xiang}", color = starColor(ps.xiang, yun), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        // 运星下中
        Text(
            "${ps.yun}",
            color = TextSub,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PalaceSheet(ps: PalaceStars, yun: Int, onDismiss: () -> Unit) {
    val combo = comboOf(ps.shan, ps.xiang)
    val gua = guaOf(ps.shan, ps.xiang)
    val shanInfo = STARS[ps.shan]
    val xiangInfo = STARS[ps.xiang]

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = InkSurface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 16.dp)
        ) {
            SectionTitle("${ps.palace.dir} · ${ps.palace.gua}宫")
            Spacer(Modifier.height(10.dp))

            KV("山星", "${ps.shan} ${shanInfo?.name ?: ""}（${starPhase(ps.shan, yun)}）", valueColor = starColor(ps.shan, yun))
            shanInfo?.let { KV("　", "旺：${it.wang}") ; KV("　", "衰：${it.shuai}") }
            Spacer(Modifier.height(6.dp))
            KV("向星", "${ps.xiang} ${xiangInfo?.name ?: ""}（${starPhase(ps.xiang, yun)}）", valueColor = starColor(ps.xiang, yun))
            xiangInfo?.let { KV("　", "旺：${it.wang}") ; KV("　", "衰：${it.shuai}") }
            KV("运星", "${ps.yun}（地盘）")

            Spacer(Modifier.height(10.dp))
            if (gua != null) {
                KV("山向合卦", "${gua}卦（山${ps.shan}下、向${ps.xiang}上）")
            }
            Spacer(Modifier.height(6.dp))
            combo?.let {
                XCard(Modifier.fillMaxWidth()) {
                    Text("${ps.shan}${ps.xiang} 组合", color = luckColor(it.luck), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(it.text, color = TextMain, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("※ 文化研习参考，断语须结合本宫峦头形势与流年飞星综合论断。", color = TextSub, fontSize = 11.sp)
        }
    }
}
