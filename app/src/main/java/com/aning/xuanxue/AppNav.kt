package com.aning.xuanxue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aning.xuanxue.feature.ai.AiChatScreen
import com.aning.xuanxue.feature.ai.AiSettingsScreen
import com.aning.xuanxue.feature.almanac.AlmanacScreen
import com.aning.xuanxue.feature.bazi.BaziScreen
import com.aning.xuanxue.feature.compass.CompassScreen
import com.aning.xuanxue.feature.iching.IChingScreen
import com.aning.xuanxue.feature.name.NameScreen
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import java.util.Calendar

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav::navigate) }
        composable("compass") { CompassScreen(onBack = { nav.popBackStack() }) }
        composable("bazi") { BaziScreen(onBack = { nav.popBackStack() }) }
        composable("iching") { IChingScreen(onBack = { nav.popBackStack() }) }
        composable("almanac") { AlmanacScreen(onBack = { nav.popBackStack() }) }
        composable("name") { NameScreen(onBack = { nav.popBackStack() }) }
        composable("ai") {
            AiChatScreen(
                onBack = { nav.popBackStack() },
                onSettings = { nav.navigate("ai_settings") }
            )
        }
        composable("ai_settings") { AiSettingsScreen(onBack = { nav.popBackStack() }) }
    }
}

private data class Entry(
    val route: String,
    val title: String,
    val sub: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(go: (String) -> Unit) {
    val entries = listOf(
        Entry("compass", "风水罗盘", "二十四山 · 八卦方位", Icons.Filled.Explore),
        Entry("bazi", "八字排盘", "四柱 · 五行 · 十神", Icons.Filled.GridView),
        Entry("iching", "易经起卦", "六十四卦 · 动爻", Icons.Filled.Casino),
        Entry("almanac", "老黄历", "宜忌 · 冲煞 · 吉神", Icons.Filled.MenuBook),
        Entry("name", "姓名五行", "缺补 · 起名参考", Icons.Filled.Spa),
        Entry("ai", "AI 玄师", "可插拔大模型 · 解卦问事", Icons.Filled.AutoAwesome),
    )

    XScaffold(title = "玄机阁") { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TodayHeader()
            // 五宫格 (2 列)
            val rows = entries.chunked(2)
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { e ->
                        GridCard(e, Modifier.weight(1f)) { go(e.route) }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                "测着玩 · 图个吉利 · 信则有",
                color = TextSub,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun TodayHeader() {
    val info = remember {
        val c = Calendar.getInstance()
        val solar = Solar.fromYmdHms(
            c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)
        )
        val lunar = solar.lunar
        val yi = lunar.dayYi.take(4).joinToString(" ")
        val ji = lunar.dayJi.take(4).joinToString(" ")
        Triple(
            "${solar.year}-${"%02d".format(solar.month)}-${"%02d".format(solar.day)}  ${solar.weekInChinese.let { "星期$it" }}",
            "农历${lunar.monthInChinese}月${lunar.dayInChinese}  ${lunar.yearInGanZhi}${lunar.yearShengXiao}年 · ${lunar.dayInGanZhi}日",
            yi to ji
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = InkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(info.first, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(info.second, color = TextMain, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            Row {
                Text("宜 ", color = Jade, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(info.third.first.ifBlank { "—" }, color = TextMain, fontSize = 13.sp)
            }
            Spacer(Modifier.height(2.dp))
            Row {
                Text("忌 ", color = Cinnabar, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(info.third.second.ifBlank { "—" }, color = TextMain, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun GridCard(e: Entry, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = InkSurface
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(Gold.copy(alpha = 0.25f), Gold.copy(alpha = 0.08f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(e.icon, contentDescription = e.title, tint = GoldBright)
            }
            Column {
                Text(e.title, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(e.sub, color = TextSub, fontSize = 11.sp)
            }
        }
    }
}
