package com.aning.xuanxue.feature.xuanhuang

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.aning.xuanxue.ui.*
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun StarChartScreen(onBack: () -> Unit) {
    val dayIndex = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val active = remember(dayIndex) { starMansions[dayIndex % starMansions.size] }
    XScaffold(title = "星宿天图", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            StarSkyCanvas(active)
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("今日值宿")
                Spacer(Modifier.height(8.dp))
                KV("星宿", active.name, GoldBright)
                KV("方位", active.direction, Jade)
                KV("五行", active.element, TextMain)
                Spacer(Modifier.height(10.dp))
                Text(active.desc, color = TextMain, fontSize = 14.sp, lineHeight = 21.sp)
            }
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("玩法说明")
                Spacer(Modifier.height(8.dp))
                Text("星宿天图用于增强宇宙星空感。后续可以和黄历、飞星、每日任务、山海图鉴联动，形成每天不同的秘境入口。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
            starMansions.take(14).forEach { mansion ->
                XCard(Modifier.fillMaxWidth()) {
                    Text(mansion.name, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("${mansion.direction} · ${mansion.element}", color = TextSub, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun StarSkyCanvas(active: StarMansion) {
    val infinite = rememberInfiniteTransition(label = "starChart")
    val rot by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(68000, easing = LinearEasing), RepeatMode.Restart),
        label = "starRot"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse),
        label = "starPulse"
    )
    Surface(
        modifier = Modifier.fillMaxWidth().height(330.dp),
        shape = RoundedCornerShape(26.dp),
        color = InkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
    ) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize().padding(16.dp)) {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF020511), Ink, Color(0xFF101927))))
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = min(size.width, size.height) * 0.40f
                rotate(rot, Offset(cx, cy)) {
                    repeat(4) { i ->
                        drawCircle(Gold.copy(alpha = 0.06f + i * 0.035f), radius = r * (0.7f + i * 0.28f), center = Offset(cx, cy))
                    }
                    repeat(28) { i ->
                        val angle = (i * 360f / 28f) * PI / 180f
                        val rr = r * (0.92f + (i % 5) * 0.055f)
                        val point = Offset(cx + cos(angle).toFloat() * rr, cy + sin(angle).toFloat() * rr)
                        drawCircle(if (i == active.index) Cinnabar.copy(alpha = pulse) else GoldBright.copy(alpha = 0.55f), if (i == active.index) 7f else 3f, point)
                    }
                }
            }
            Column(Modifier.align(Alignment.Center).padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("二十八宿", color = GoldBright, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Spacer(Modifier.height(8.dp))
                Text("今日值宿：${active.name}", color = TextMain, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Text("星河流转，天机入盘", color = TextSub, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun XuanTaskScreen(onBack: () -> Unit) {
    XScaffold(title = "玄门任务", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("每日循环")
                Spacer(Modifier.height(8.dp))
                Text("V2.1 加入轻游戏循环：每天看天时、测地脉、养命格、收图鉴、问玄师。后续接入本地存档后，可记录连续签到、灵气值和图鉴进度。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
            dailyTasks.forEachIndexed { index, task ->
                XCard(Modifier.fillMaxWidth()) {
                    Text("${index + 1}. ${task.title}", color = GoldBright, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(task.desc, color = TextMain, fontSize = 13.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("奖励：${task.reward}", color = Jade, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun IllustrationPromptScreen(onBack: () -> Unit) {
    XScaffold(title = "插图资源库", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("精美插图管线")
                Spacer(Modifier.height(8.dp))
                Text("这里先放绘图提示词模板。以后生成图片后，把图片放进 drawable 或 assets，再把这些占位卡替换成真实插图。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }
            illustrationPrompts.forEach { prompt ->
                XCard(Modifier.fillMaxWidth()) {
                    Text(prompt.title, color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(prompt.usage, color = Jade, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(prompt.prompt, color = TextMain, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}

private data class StarMansion(val index: Int, val name: String, val direction: String, val element: String, val desc: String)

private val starMansions = listOf(
    StarMansion(0, "角宿", "东方青龙", "木", "角宿开张，适合立新局、启新事。"),
    StarMansion(1, "亢宿", "东方青龙", "金", "亢宿高昂，宜收束心神。"),
    StarMansion(2, "氐宿", "东方青龙", "土", "氐宿入地，重根基、家宅、整理。"),
    StarMansion(3, "房宿", "东方青龙", "日", "房宿明堂，利沟通、约定、筹划。"),
    StarMansion(4, "心宿", "东方青龙", "月", "心宿主火，宜静心。"),
    StarMansion(5, "尾宿", "东方青龙", "火", "尾宿有势，适合推进长期目标。"),
    StarMansion(6, "箕宿", "东方青龙", "水", "箕宿动风，适合清理、出行、变通。"),
    StarMansion(7, "斗宿", "北方玄武", "木", "斗宿主衡，宜校准计划、复盘验算。"),
    StarMansion(8, "牛宿", "北方玄武", "金", "牛宿重耕，宜耐心积累。"),
    StarMansion(9, "女宿", "北方玄武", "土", "女宿细密，宜修整关系与细节。"),
    StarMansion(10, "虚宿", "北方玄武", "日", "虚宿藏机，宜思考。"),
    StarMansion(11, "危宿", "北方玄武", "月", "危宿提醒风险，宜谨慎验真。"),
    StarMansion(12, "室宿", "北方玄武", "火", "室宿成屋，利家宅、布局、定计划。"),
    StarMansion(13, "壁宿", "北方玄武", "水", "壁宿如藏书，利学习、资料整理。"),
    StarMansion(14, "奎宿", "西方白虎", "木", "奎宿文气，利写作、表达、作品。"),
    StarMansion(15, "娄宿", "西方白虎", "金", "娄宿收纳，利分类、归档。"),
    StarMansion(16, "胃宿", "西方白虎", "土", "胃宿主养，宜休整身心。"),
    StarMansion(17, "昴宿", "西方白虎", "日", "昴宿锐利，宜决断。"),
    StarMansion(18, "毕宿", "西方白虎", "月", "毕宿成网，适合收尾和验收。"),
    StarMansion(19, "觜宿", "西方白虎", "火", "觜宿主言，宜慎言。"),
    StarMansion(20, "参宿", "西方白虎", "水", "参宿分明，利竞争和判断。"),
    StarMansion(21, "井宿", "南方朱雀", "木", "井宿通达，利交流、人脉、开源。"),
    StarMansion(22, "鬼宿", "南方朱雀", "金", "鬼宿神秘，宜解梦、问玄、查隐情。"),
    StarMansion(23, "柳宿", "南方朱雀", "土", "柳宿柔动，适合调整表达方式。"),
    StarMansion(24, "星宿", "南方朱雀", "日", "星宿当明，利曝光、展示、发布。"),
    StarMansion(25, "张宿", "南方朱雀", "月", "张宿扩张，利推广、布置。"),
    StarMansion(26, "翼宿", "南方朱雀", "火", "翼宿飞扬，适合创意和行动。"),
    StarMansion(27, "轸宿", "南方朱雀", "水", "轸宿收车，利复盘、修正、收尾。")
)

private data class XuanTask(val title: String, val desc: String, val reward: String)

private val dailyTasks = listOf(
    XuanTask("观天时", "进入天时神谕，查看今日宜忌与干支。", "灵气 +10 · 天时经验 +1"),
    XuanTask("测地脉", "进入寻龙定脉或山海地脉，校准今日方位。", "地脉碎片 +1"),
    XuanTask("养命格", "进入命格成长，查看今日五行偏向。", "命格经验 +8"),
    XuanTask("收图鉴", "进入山海图鉴，解锁或复习一张神话卡。", "图鉴熟练度 +1"),
    XuanTask("问玄师", "进入师尊问道，询问今日文化参考建议。", "玄师羁绊 +1")
)

private data class IllustrationPrompt(val title: String, val usage: String, val prompt: String)

private val illustrationPrompts = listOf(
    IllustrationPrompt("天地玄黄首页主视觉", "启动页 / 首页顶部", "东方玄幻神话风，天地玄黄主题，宇宙星空旋转，山川河流在下方延展，金色法阵与地脉线发光，玄黑暗金朱砂配色，高级手游界面背景，精美插画。"),
    IllustrationPrompt("山海地脉地图", "山海地脉页面", "上古山海世界地图，群山连绵，大河奔流，五方地脉节点发出金色光芒，国风玄幻，细节丰富。"),
    IllustrationPrompt("星宿天图", "星宿天图页面", "深邃宇宙星空，二十八宿星图，北斗与星轨，金色星线连接，古代天文浑仪和八卦纹理，暗蓝黑金色调。"),
    IllustrationPrompt("师尊问道立绘", "AI 玄师页面", "东方玄幻师尊角色，玄黑道袍，金色符文，背后星盘与山海云雾，气质神秘温和，半身立绘，手游卡面质量。"),
    IllustrationPrompt("青龙图鉴卡", "山海图鉴神兽卡", "东方青龙神兽，盘绕在云海与雷泽之上，鳞片带青玉光泽，周围有木系灵气和金色符线，山海神话风。")
)
