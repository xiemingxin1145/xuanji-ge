package com.aning.xuanxue.feature.status

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.AppVersion
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold

@Composable
fun FeatureStatusScreen(onBack: () -> Unit) {
    XScaffold(title = "新版功能清单", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle(AppVersion.DISPLAY)
                Spacer(Modifier.height(8.dp))
                Text("如果你能看到这个页面，说明你装的是 v${AppVersion.VERSION_NAME} 新包，不是旧包。", color = Jade, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("本页用于测试核对：看功能是否真正进入 APK。版本号每次更新都会递增。", color = TextSub, fontSize = 12.sp)
            }

            StatusBlock(
                title = "版本规则",
                items = listOf(
                    "当前版本：v${AppVersion.VERSION_NAME} / versionCode ${AppVersion.VERSION_CODE}",
                    "小功能更新：v1.2 → v1.3 → v1.4",
                    "大版本升级：视觉、音效、联网问玄体系完成后进入 v2.0",
                    "每次发测试包，先看这里的版本号，避免误装旧包"
                )
            )

            StatusBlock(
                title = "风水专业线",
                items = listOf(
                    "风水罗盘：二十四山、分金、纳水、磁场强度",
                    "玄空飞星：三元九运、山盘、向盘、九宫格局",
                    "流年流月紫白：九宫年星/月星叠加",
                    "二十四山向：坐山向首、元龙、阴阳、断语",
                    "JSON 动态内容样板：二十四山向先读本地数据包，缺失回退内置库"
                )
            )

            StatusBlock(
                title = "八字命理线",
                items = listOf(
                    "四柱排盘：年、月、日、时柱",
                    "五行统计：木火土金水数量",
                    "命主信息：日元、胎元、命宫、身宫",
                    "神煞速查：天乙、文昌、禄神、羊刃、桃花、驿马、华盖、将星、旬空",
                    "大运序列：按阳男阴女顺、阴男阳女逆列十步大运",
                    "流年十年：显示未来十年干支"
                )
            )

            StatusBlock(
                title = "日课与个人记录线",
                items = listOf(
                    "我的印记：五行印、八卦印、今日档案",
                    "今日符卡：抽卡、行动提醒、护身笔记",
                    "梦境记录：梦境内容、情绪、关键词、问玄师",
                    "五行养生：日课、呼吸、情绪调节"
                )
            )

            StatusBlock(
                title = "问玄师 AI 线",
                items = listOf(
                    "可插拔大模型 Key",
                    "多模块 Prompt 预填",
                    "资料库、梦境、符卡、养生日课可一键问玄师",
                    "后续目标：罗盘、飞星、八字、二十四山全部生成结构化问玄师问法"
                )
            )

            StatusBlock(
                title = "视觉与音效增强计划",
                items = listOf(
                    "APP 图标：玄黑、暗金、朱砂、罗盘印章风格",
                    "首页主视觉：太极、八卦、星轨、金线微动",
                    "问玄师形象：东方玄师立绘入口",
                    "模块头图：罗盘、飞星、八字、黄历、梦境、符卡",
                    "音效位：启动音、点击音、切换音、抽符音、计算完成音、警示音、罗盘轻响、问玄师回复提示"
                )
            )

            XCard(Modifier.fillMaxWidth()) {
                Text("测试重点", color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("1. 首页是否出现『新版功能清单』。", color = TextMain, fontSize = 13.sp)
                Text("2. 本页是否显示『${AppVersion.DISPLAY}』。", color = TextMain, fontSize = 13.sp)
                Text("3. 首页是否有『玄空飞星』和『二十四山向』。", color = TextMain, fontSize = 13.sp)
                Text("4. 八字排盘后是否出现神煞、大运、流年。", color = TextMain, fontSize = 13.sp)
                Text("5. 二十四山向页是否显示数据条数。", color = TextMain, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("看不到版本号或功能清单，就说明仍然装的是旧包。", color = Cinnabar, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatusBlock(title: String, items: List<String>) {
    XCard(Modifier.fillMaxWidth()) {
        Text(title, color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            Text("· $item", color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}
