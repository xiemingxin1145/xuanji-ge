package com.aning.xuanxue.feature.xuanqi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.feature.ai.PendingAiPromptStore
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.InkSurface
import com.aning.xuanxue.ui.InkSurface2
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold

private data class XuanqiCategory(
    val title: String,
    val sub: String,
    val icon: ImageVector,
    val accent: Color,
    val entries: List<String>
)

private val categories = listOf(
    XuanqiCategory(
        title = "山海经图鉴",
        sub = "异兽 · 神人 · 海外诸国 · 昆仑体系",
        icon = Icons.Filled.Terrain,
        accent = GoldBright,
        entries = listOf("九尾狐", "夔牛", "烛龙", "英招", "毕方", "精卫", "相柳", "饕餮")
    ),
    XuanqiCategory(
        title = "神怪志",
        sub = "精怪 · 妖魅 · 山魈 · 水怪 · 夜行异闻",
        icon = Icons.Filled.Forest,
        accent = Jade,
        entries = listOf("狐仙", "山魈", "水鬼", "宅灵", "夜游神", "黄皮子", "蛇灵", "槐树精")
    ),
    XuanqiCategory(
        title = "玄幻设定库",
        sub = "境界 · 宗门 · 灵根 · 丹药 · 法宝 · 阵法",
        icon = Icons.Filled.AutoAwesome,
        accent = Cinnabar,
        entries = listOf("炼气", "筑基", "金丹", "元婴", "灵根", "法宝", "丹药", "秘境")
    ),
    XuanqiCategory(
        title = "民俗异闻库",
        sub = "节令 · 禁忌 · 梦兆 · 婚丧 · 地方传说",
        icon = Icons.Filled.Book,
        accent = Gold,
        entries = listOf("门神", "灶君", "头七", "夜路忌", "梦兆", "立春禁忌", "中元节", "压岁")
    ),
    XuanqiCategory(
        title = "道教神谱",
        sub = "三清 · 四御 · 雷部 · 斗部 · 城隍土地",
        icon = Icons.Filled.Shield,
        accent = GoldBright,
        entries = listOf("三清", "四御", "雷部", "斗姆", "真武", "城隍", "土地", "灶君")
    ),
    XuanqiCategory(
        title = "奇门异术库",
        sub = "符箓文化 · 法器 · 步罡 · 科仪 · 阵图",
        icon = Icons.Filled.Psychology,
        accent = Jade,
        entries = listOf("符箓", "桃木剑", "铜钱", "罗盘", "步罡", "科仪", "印诀", "阵图")
    )
)

@Composable
fun XuanqiScreen(
    onBack: () -> Unit,
    onOpenAi: () -> Unit
) {
    val context = LocalContext.current
    XScaffold(title = "玄奇志", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("玄奇志 · 东方玄奇宇宙")
                Spacer(Modifier.height(8.dp))
                Text(
                    "这里不是单纯算命工具，而是玄机阁的世界观资料库：山海经、神怪民俗、玄幻设定、道教神谱、奇门异术都将收纳在这里。",
                    color = TextMain,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "当前 v1.8 先搭骨架，后续逐步补图鉴、插画、出处、象征意义和问玄师解读。",
                    color = TextSub,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            XuanqiHero()

            categories.forEach { category ->
                CategoryCard(category) {
                    XuanSound.play(context, XuanSound.Effect.Open)
                    PendingAiPromptStore.set(buildCategoryPrompt(category))
                    onOpenAi()
                }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("后续计划")
                Spacer(Modifier.height(8.dp))
                Text("· v1.9：山海经异兽图鉴第一批", color = TextMain, fontSize = 13.sp)
                Text("· v2.0：图鉴插图、人物图、模块头图、玄奇志搜索", color = TextMain, fontSize = 13.sp)
                Text("· v2.1：每个条目支持收藏、问玄师、民俗出处、玄幻设定改写", color = TextMain, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("所有内容仅作传统文化、民俗故事、文学设定与娱乐参考。", color = TextSub, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun XuanqiHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.verticalGradient(listOf(InkSurface2, InkSurface))),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Gold.copy(alpha = 0.12f), radius = size.minDimension * 0.48f, center = center)
            drawCircle(Cinnabar.copy(alpha = 0.09f), radius = size.minDimension * 0.30f, center = center)
            drawCircle(Jade.copy(alpha = 0.08f), radius = size.minDimension * 0.18f, center = center)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("山海 · 神怪 · 玄幻", color = GoldBright, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("把东方奇闻，收进一座玄机阁", color = TextSub, fontSize = 12.sp)
        }
    }
}

@Composable
private fun CategoryCard(category: XuanqiCategory, onAsk: () -> Unit) {
    XCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(category.accent.copy(alpha = 0.16f))
                    .border(BorderStroke(1.dp, category.accent.copy(alpha = 0.45f)), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = category.title, tint = category.accent)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(category.title, color = GoldBright, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(category.sub, color = TextSub, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(category.entries.joinToString(" · "), color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(category.accent.copy(alpha = 0.12f))
                .clickable(onClick = onAsk)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("问玄师：如何扩展这个栏目", color = category.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun buildCategoryPrompt(category: XuanqiCategory): String = buildString {
    appendLine("【玄奇志栏目扩展】")
    appendLine("栏目：${category.title}")
    appendLine("方向：${category.sub}")
    appendLine("样例条目：${category.entries.joinToString("、")}")
    appendLine()
    appendLine("请为玄机阁 APP 设计这个栏目：")
    appendLine("1. 栏目定位")
    appendLine("2. 适合收录的条目分类")
    appendLine("3. 每个条目的字段结构")
    appendLine("4. 适合做成图鉴、故事、问玄师解读的玩法")
    appendLine("5. 注意合规表达：传统文化、民俗故事、文学设定和娱乐参考，不做现实效果承诺。")
}
