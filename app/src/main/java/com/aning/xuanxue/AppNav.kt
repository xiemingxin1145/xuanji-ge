package com.aning.xuanxue

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.feature.ai.AiChatScreen
import com.aning.xuanxue.feature.ai.AiSettingsScreen
import com.aning.xuanxue.feature.ai.PendingAiPromptStore
import com.aning.xuanxue.feature.almanac.AlmanacScreen
import com.aning.xuanxue.feature.bazi.BaziScreen
import com.aning.xuanxue.feature.compass.CompassScreen
import com.aning.xuanxue.feature.dream.DreamScreen
import com.aning.xuanxue.feature.flyingstar.FlyingStarScreen
import com.aning.xuanxue.feature.guide.GuideScreen
import com.aning.xuanxue.feature.iching.IChingScreen
import com.aning.xuanxue.feature.knowledge.KnowledgeScreen
import com.aning.xuanxue.feature.mountain.MountainOracleScreen
import com.aning.xuanxue.feature.name.NameScreen
import com.aning.xuanxue.feature.seal.SealScreen
import com.aning.xuanxue.feature.status.FeatureStatusScreen
import com.aning.xuanxue.feature.talisman.TalismanScreen
import com.aning.xuanxue.feature.update.UpdateCenterScreen
import com.aning.xuanxue.feature.wellness.WellnessScreen
import com.aning.xuanxue.feature.xuanhuang.EarthVeinScreen
import com.aning.xuanxue.feature.xuanhuang.FateGrowthScreen
import com.aning.xuanxue.feature.xuanhuang.IllustrationPromptScreen
import com.aning.xuanxue.feature.xuanhuang.ShanhaiAtlasScreen
import com.aning.xuanxue.feature.xuanhuang.StarChartScreen
import com.aning.xuanxue.feature.xuanhuang.XuanSuanVerifyScreen
import com.aning.xuanxue.feature.xuanhuang.XuanTaskScreen
import com.aning.xuanxue.feature.xuanhuang.XuanhuangDashboardScreen
import com.aning.xuanxue.feature.xuanji.XuanjiResonanceDemoScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aning.xuanxue.core.state.GameStateViewModel
import com.aning.xuanxue.feature.case_engine.CaseListScreen
import com.aning.xuanxue.feature.daily.DailyQuestScreen
import com.aning.xuanxue.feature.onboarding.OnboardingScreen
import com.aning.xuanxue.feature.ar.ArScanScreen
import com.aning.xuanxue.feature.ghost.GhostHuntScreen
import com.aning.xuanxue.feature.ghost.GhostBestiaryScreen
import com.aning.xuanxue.feature.cultivation.CultivationScreen
import com.aning.xuanxue.feature.daily.DailyTaskScreen
import com.aning.xuanxue.feature.onboarding.OnboardingScreen
import com.aning.xuanxue.feature.ar.ARGhostScreen
import com.aning.xuanxue.core.store.PlayerViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aning.xuanxue.feature.case_engine.CasePlayScreen
import com.aning.xuanxue.feature.xuanqi.XuanqiScreen
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.min

@Composable
fun AppNav() {
    val nav = rememberNavController()

    fun openAiWithPrompt(prompt: String) {
        PendingAiPromptStore.set(prompt)
        nav.navigate("ai")
    }

    val gameVm: GameStateViewModel = viewModel()
        val onboardingDone by gameVm.onboardingDone.collectAsState()
        NavHost(navController = nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                if (onboardingDone) {
                    nav.navigate("home") { popUpTo("splash") { inclusive = true } }
                } else {
                    nav.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                }
            }
        }
        composable("home") { HomeScreen(nav::navigate) }

        // V2.x 天地玄黄游戏化入口
        composable("xuanhuang") { XuanhuangDashboardScreen(onBack = { nav.popBackStack() }, go = nav::navigate) }
        composable("earth_vein") { EarthVeinScreen(onBack = { nav.popBackStack() }) }
        composable("fate_growth") { FateGrowthScreen(onBack = { nav.popBackStack() }) }
        composable("shanhai_atlas") { ShanhaiAtlasScreen(onBack = { nav.popBackStack() }) }
        composable("xuan_verify") { XuanSuanVerifyScreen(onBack = { nav.popBackStack() }) }
        composable("star_chart") { StarChartScreen(onBack = { nav.popBackStack() }) }
        composable("xuan_tasks") { XuanTaskScreen(onBack = { nav.popBackStack() }) }
        composable("illustration_prompts") { IllustrationPromptScreen(onBack = { nav.popBackStack() }) }

        composable("status") { FeatureStatusScreen(onBack = { nav.popBackStack() }) }
        composable("update") { UpdateCenterScreen(onBack = { nav.popBackStack() }) }
        composable("guide") { GuideScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("seal") { SealScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("knowledge") { KnowledgeScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("xuanqi") { XuanqiScreen(onBack = { nav.popBackStack() }, onOpenAi = { nav.navigate("ai") }) }
        composable("talisman") { TalismanScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("dream") { DreamScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("wellness") { WellnessScreen(onBack = { nav.popBackStack() }, onAiPrompt = ::openAiWithPrompt) }
        composable("compass") { CompassScreen(onBack = { nav.popBackStack() }) }
        composable("flyingstar") { FlyingStarScreen(onBack = { nav.popBackStack() }) }
        composable("mountain") {
            MountainOracleScreen(
                onBack = { nav.popBackStack() },
                onOpenFlyingStar = { nav.navigate("flyingstar") }
            )
        }
        composable("bazi") { BaziScreen(onBack = { nav.popBackStack() }) }
        composable("iching") { IChingScreen(onBack = { nav.popBackStack() }) }
        composable("almanac") { AlmanacScreen(onBack = { nav.popBackStack() }) }
        composable("name") { NameScreen(onBack = { nav.popBackStack() }) }
        composable("ai") { AiChatScreen(onBack = { nav.popBackStack() }, onSettings = { nav.navigate("ai_settings") }) }
        composable("ai_settings") { AiSettingsScreen(onBack = { nav.popBackStack() }) }
        composable("xuanji_resonance_demo") { XuanjiResonanceDemoScreen(onBack = { nav.popBackStack() }) }
        composable("cases") {
            CaseListScreen(
                onBack = { nav.popBackStack() },
                onEnterCase = { caseId -> nav.navigate("case_play/$caseId") }
            )
        }
        composable("case_play/{caseId}") { backStack ->
            val caseId = backStack.arguments?.getString("caseId") ?: return@composable
            CasePlayScreen(
                caseId = caseId,
                onBack = { nav.popBackStack() },
                onNavigateTool = { route -> nav.navigate(route) }
            )
        }
        composable("ghost_hunt") { GhostHuntScreen(onBack = { nav.popBackStack() }) }
        composable("ghost_bestiary") { GhostBestiaryScreen(onBack = { nav.popBackStack() }) }
        composable("cultivation") { CultivationScreen(onBack = { nav.popBackStack() }) }
        composable("daily_tasks") {
            DailyTaskScreen(onBack = { nav.popBackStack() }, onNavigate = { route -> nav.navigate(route) })
        }
        composable("ar_ghost") { ARGhostScreen(onBack = { nav.popBackStack() }) }
        composable("onboarding") {
            val playerVm: PlayerViewModel = viewModel()
            OnboardingScreen(onDone = {
                playerVm.onOnboardingDone()
                nav.navigate("home") { popUpTo("onboarding") { inclusive = true } }
            })
        }
        composable("daily") {
            DailyQuestScreen(
                viewModel = gameVm,
                onBack = { nav.popBackStack() },
                onNavigate = { route -> nav.navigate(route) }
            )
        }
        composable("ar_scan") {
            ArScanScreen(
                onBack = { nav.popBackStack() },
                onTrigger = { nodeId -> nav.navigate("cases") }
            )
        }
        composable("onboarding") {
            OnboardingScreen(onComplete = {
                gameVm.setOnboardingDone()
                nav.navigate("home") { popUpTo("onboarding") { inclusive = true } }
            })
        }
    }
}

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        XuanSound.play(context, XuanSound.Effect.Open)
        delay(1600)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = min(cx, cy) * 0.72f
            drawCircle(Gold.copy(alpha = 0.08f), radius = r)
            drawCircle(Gold.copy(alpha = 0.05f), radius = r * 0.68f)
            drawCircle(Cinnabar.copy(alpha = 0.06f), radius = r * 0.42f)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("最后一个道士", color = Cinnabar, fontSize = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            Spacer(Modifier.height(8.dp))
            Text("THE LAST TAOIST", color = Gold.copy(alpha = 0.75f), fontSize = 13.sp, letterSpacing = 4.sp)
            Spacer(Modifier.height(32.dp))
            TaijiSymbol(symbolSize = 92.dp)
            Spacer(Modifier.height(32.dp))
            Text("道法自然 · 伏魔除妖 · 三界护法", color = TextSub.copy(alpha = 0.75f), fontSize = 13.sp)
        }
    }
}

@Composable
fun TaijiSymbol(symbolSize: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "taiji")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "taijiRot"
    )
    Canvas(modifier.size(symbolSize)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = min(cx, cy)
        rotate(rotation) {
            drawCircle(GoldBright, radius = r)
            drawCircle(Ink, radius = r * 0.48f, center = Offset(cx, cy - r * 0.5f))
            drawCircle(GoldBright, radius = r * 0.48f, center = Offset(cx, cy + r * 0.5f))
            drawCircle(Cinnabar, radius = r * 0.12f, center = Offset(cx, cy - r * 0.5f))
            drawCircle(Ink, radius = r * 0.12f, center = Offset(cx, cy + r * 0.5f))
        }
    }
}

private data class Entry(
    val route: String,
    val title: String,
    val sub: String,
    val icon: ImageVector,
    val featured: Boolean = false
)

@Composable
fun HomeScreen(go: (String) -> Unit) {
    val context = LocalContext.current
    val entries = listOf(
        Entry("xuanhuang", "天地玄黄", "动态天盘 · 山川河流 · 星河地脉 · 游戏总入口", Icons.Filled.AutoAwesome, true),
        Entry("earth_vein", "山海地脉", "山川河流 · 地脉发光 · 方位秘境", Icons.Filled.Explore, true),
        Entry("fate_growth", "命格成长", "五行属性 · 玄气加成 · 轻游戏养成", Icons.Filled.Spa, true),
        Entry("shanhai_atlas", "山海图鉴", "神兽 · 神祇 · 法器 · 插图收集", Icons.Filled.MenuBook, true),
        Entry("xuan_verify", "玄算验真", "天时地利人和 · 自行验算", Icons.Filled.Verified, true),
        Entry("star_chart", "星宿天图", "宇宙星空 · 二十八宿 · 今日值宿", Icons.Filled.Star, true),
        Entry("xuan_tasks", "玄门任务", "每日循环 · 灵气奖励 · 游戏成长", Icons.Filled.CheckCircle, true),
        Entry("illustration_prompts", "插图资源库", "首页主图 · 山海地图 · 神兽卡面提示词", Icons.Filled.Image, true),
        Entry("status", "新版功能清单", "${AppVersion.DISPLAY}", Icons.Filled.Verified),
        Entry("update", "检查更新", "APP 内打开最新版安装包", Icons.Filled.Verified),
        Entry("guide", "玄门向导", "今日问玄 · 先问再测", Icons.Filled.AutoAwesome),
        Entry("seal", "我的印记", "五行印 · 八卦印 · 今日档案", Icons.Filled.AutoAwesome),
        Entry("knowledge", "玄门资料库", "道教 · 民俗 · 五行 · 风水", Icons.Filled.MenuBook),
        Entry("xuanqi", "玄奇志", "山海经 · 神怪 · 玄幻 · 民俗", Icons.Filled.AutoAwesome),
        Entry("talisman", "今日符卡", "抽卡 · 印记 · 行动提醒", Icons.Filled.AutoAwesome),
        Entry("dream", "梦境记录", "解梦 · 情绪 · 民俗象意", Icons.Filled.NightsStay),
        Entry("wellness", "五行养生", "日课 · 呼吸 · 情绪调节", Icons.Filled.Spa),
        Entry("compass", "寻龙定脉", "风水罗盘 · 二十四山 · 八卦方位", Icons.Filled.Explore),
        Entry("flyingstar", "玄空飞星", "三元九运 · 飞星排盘", Icons.Filled.Apps),
        Entry("mountain", "二十四山向", "坐山向首 · 元龙断法", Icons.Filled.Explore),
        Entry("bazi", "命格鉴定台", "四柱 · 神煞 · 大运流年", Icons.Filled.GridView),
        Entry("iching", "易经起卦", "六十四卦 · 动爻", Icons.Filled.Casino),
        Entry("almanac", "天时神谕", "老黄历 · 宜忌 · 冲煞 · 吉神", Icons.Filled.CalendarMonth),
        Entry("name", "姓名五行", "缺补 · 起名参考", Icons.Filled.Spa),
        Entry("ai", "师尊问道", "AI玄师 · 解卦问事 · 剧情推进", Icons.Filled.AutoAwesome),
        Entry("cases", "阴阳录·卷宗", "接案 · 调查 · 推演 · 玄机共鸣结案", Icons.Filled.Book, true),
        Entry("ghost_hunt", "捉鬼行动", "感应鬼魂 · 封印镇压 · 收集鬼气", Icons.Filled.Visibility, true),
        Entry("ghost_bestiary", "鬼怪图鉴", "山海经 · 搜神记 · 民俗典故解说", Icons.Filled.MenuBook, true),
        Entry("cultivation", "道行修炼", "境界突破 · 五行属性 · 道法宝典", Icons.Filled.Spa, true),
        Entry("daily_tasks", "每日修行", "签到 · 五项修炼任务 · 连日奖励", Icons.Filled.Today, true),
        Entry("ar_ghost", "AR玄机探测", "现实叠加感应 · 鬼魂雷达 · 方位追踪", Icons.Filled.CameraAlt, true),
        Entry("daily", "每日修炼", "日常任务 · 领取灵气 · 修为汇总", Icons.Filled.CalendarToday, true),
        Entry("ar_scan", "AR玄机感应", "现实扫描 · 地脉叠加 · 节点探秘", Icons.Filled.CameraAlt, true),
        Entry("xuanji_resonance_demo", "玄机共鸣测试", "天时·地利·人和 核心引擎", Icons.Filled.AutoAwesome)
    )

    XScaffold(title = "最后一个道士") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            CentralXuanVisual(onClick = {
                XuanSound.play(context, XuanSound.Effect.Open)
                go("xuanhuang")
            })
            TodayHeader()

            SectionTitle("天地玄黄 V2.1")
            entries.filter { it.featured }.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { entry ->
                        GridCard(entry, Modifier.weight(1f)) {
                            XuanSound.play(context, XuanSound.Effect.Click)
                            go(entry.route)
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            SectionTitle("玄门工具与资料")
            entries.filterNot { it.featured }.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { entry ->
                        GridCard(entry, Modifier.weight(1f)) {
                            XuanSound.play(context, XuanSound.Effect.Click)
                            go(entry.route)
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "${AppVersion.DISPLAY} · 观天时 · 测地脉 · 演命格 · 入山海",
                color = TextSub,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CentralXuanVisual(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "homeVisual")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(42000, easing = LinearEasing), RepeatMode.Restart),
        label = "homeRot"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.52f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Reverse),
        label = "homePulse"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, Gold.copy(alpha = pulse))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().padding(18.dp)) {
                val cx = size.width / 2f
                val cy = size.height * 0.42f
                val r = min(size.width, size.height) * 0.38f
                drawRect(Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color(0xFF050712), Ink, InkSurface)))
                rotate(rotation, pivot = Offset(cx, cy)) {
                    drawCircle(Gold.copy(alpha = 0.10f + pulse * 0.12f), radius = r * 1.38f, center = Offset(cx, cy))
                    drawCircle(Gold.copy(alpha = 0.13f), radius = r, center = Offset(cx, cy))
                    drawCircle(Cinnabar.copy(alpha = 0.08f + pulse * 0.08f), radius = r * 0.54f, center = Offset(cx, cy))
                    for (i in 0 until 12) {
                        rotate(i * 30f, pivot = Offset(cx, cy)) {
                            drawCircle(GoldBright.copy(alpha = 0.38f + pulse * 0.4f), radius = 3.8f, center = Offset(cx, cy - r * 1.08f))
                        }
                    }
                }
                val mountainY = size.height * 0.76f
                repeat(6) { i ->
                    val x = size.width * i / 5f
                    drawLine(Gold.copy(alpha = 0.18f), Offset(x - 70f, mountainY), Offset(x + 24f, mountainY - 58f - i % 2 * 18f), 4f)
                    drawLine(Gold.copy(alpha = 0.12f), Offset(x + 24f, mountainY - 58f - i % 2 * 18f), Offset(x + 115f, mountainY + 4f), 4f)
                }
                repeat(3) { i ->
                    drawLine(
                        color = androidx.compose.ui.graphics.Color(0xFF4FC3F7).copy(alpha = 0.16f + pulse * 0.16f),
                        start = Offset(0f, size.height * (0.78f + i * 0.055f)),
                        end = Offset(size.width, size.height * (0.72f + i * 0.055f)),
                        strokeWidth = 5f
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("天地玄黄", color = GoldBright, fontSize = 31.sp, fontWeight = FontWeight.Bold, letterSpacing = 5.sp)
                Spacer(Modifier.height(8.dp))
                Text("山川河流 · 宇宙星空 · 地脉共鸣", color = TextSub, fontSize = 12.sp)
                Spacer(Modifier.height(18.dp))
                Surface(shape = RoundedCornerShape(30.dp), color = Gold.copy(alpha = 0.14f), border = BorderStroke(1.dp, Gold.copy(alpha = 0.42f))) {
                    Text("进入 V2.1 玄幻游戏化总入口", color = GoldBright, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp))
                }
            }
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
        Triple(
            "${solar.year}-${"%02d".format(solar.month)}-${"%02d".format(solar.day)}  星期${solar.weekInChinese}",
            "农历${lunar.monthInChinese}月${lunar.dayInChinese}  ${lunar.yearInGanZhi}${lunar.yearShengXiao}年 · ${lunar.dayInGanZhi}日",
            lunar.dayYi.take(4).joinToString(" ") to lunar.dayJi.take(4).joinToString(" ")
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
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
private fun GridCard(entry: Entry, modifier: Modifier, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "gridGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (entry.featured) 0.28f else 0.16f,
        targetValue = if (entry.featured) 0.62f else 0.38f,
        animationSpec = infiniteRepeatable(tween(if (entry.featured) 1600 else 2200, easing = LinearEasing), RepeatMode.Reverse),
        label = "gridGlowAlpha"
    )
    Surface(
        modifier = modifier
            .height(if (entry.featured) 144.dp else 132.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = InkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            InkSurface,
                            if (entry.featured) Cinnabar.copy(alpha = glowAlpha * 0.10f) else Gold.copy(alpha = glowAlpha * 0.10f),
                            InkSurface
                        )
                    )
                )
                .border(1.dp, Gold.copy(alpha = glowAlpha), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(Gold.copy(alpha = 0.28f + glowAlpha * 0.1f), Gold.copy(alpha = 0.08f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(entry.icon, contentDescription = entry.title, tint = GoldBright)
            }
            Column {
                Text(entry.title, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(entry.sub, color = TextSub, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}
