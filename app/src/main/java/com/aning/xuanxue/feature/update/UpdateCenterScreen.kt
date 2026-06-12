package com.aning.xuanxue.feature.update

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.AppVersion
import com.aning.xuanxue.core.sound.XuanSound
import com.aning.xuanxue.ui.Cinnabar
import com.aning.xuanxue.ui.Gold
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.Ink
import com.aning.xuanxue.ui.Jade
import com.aning.xuanxue.ui.ScrollColumn
import com.aning.xuanxue.ui.SectionTitle
import com.aning.xuanxue.ui.TextMain
import com.aning.xuanxue.ui.TextSub
import com.aning.xuanxue.ui.XCard
import com.aning.xuanxue.ui.XScaffold

@Composable
fun UpdateCenterScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var soundEnabled by remember { mutableStateOf(XuanSound.isEnabled(context)) }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    XScaffold(title = "检查更新", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle(AppVersion.DISPLAY)
                Spacer(Modifier.height(8.dp))
                Text("当前版本：v${AppVersion.VERSION_NAME} / ${AppVersion.VERSION_CODE}", color = GoldBright, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("这里是玄机阁测试版更新中心。以后所有测试包都从这里进入，不用每次在聊天里翻链接。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("音效开关")
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text(if (soundEnabled) "音效已开启" else "音效已关闭", color = if (soundEnabled) Jade else Cinnabar, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("控制启动、点击、问玄师回复、警示、抽符、排盘完成等基础音效。后续会替换为道铃、木鱼、罗盘轻响等真实素材。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            XuanSound.setEnabled(context, it)
                            if (it) XuanSound.play(context, XuanSound.Effect.Success)
                        }
                    )
                }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("v1.7 音效测试点")
                Spacer(Modifier.height(8.dp))
                Text("· 启动 APP：开场轻提示音", color = TextMain, fontSize = 13.sp)
                Text("· 首页入口：点击音", color = TextMain, fontSize = 13.sp)
                Text("· 今日符卡：抽符音；问玄师跳转音", color = TextMain, fontSize = 13.sp)
                Text("· 八字排盘：成功完成音；日期错误警示音", color = TextMain, fontSize = 13.sp)
                Text("· 问玄师：发送音；回复音；请求失败警示音", color = TextMain, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("当前为系统轻提示音，不依赖素材文件。真实道铃、木鱼、罗盘声会在后续素材包接入。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("一键前往最新版安装包")
                Spacer(Modifier.height(8.dp))
                Text("当前阶段使用 GitHub Actions 生成测试 APK。点击下面按钮后，选择最新绿色构建，进入页面底部下载 Artifacts。", color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { openUrl(AppVersion.BUILD_WORKFLOW_URL) },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.OpenInBrowser, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("打开最新版 APK 构建页", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { openUrl(AppVersion.ACTIONS_URL) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("打开全部构建记录", color = Gold) }
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("为什么还不是完全自动更新？")
                Spacer(Modifier.height(8.dp))
                Text("Android 侧真正做到“点一下自动下载并安装 APK”，需要稳定的公开 APK 下载地址、安装未知应用授权、下载管理器或应用内安装流程。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text("当前先做测试版最稳方案：APP 内打开最新版构建页；等我们有公开 Release 或自己的服务器，再升级成真正一键下载更新。", color = TextSub, fontSize = 12.sp, lineHeight = 18.sp)
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("更新判断")
                Spacer(Modifier.height(8.dp))
                Text("1. 首页是否显示 ${AppVersion.DISPLAY}", color = TextMain, fontSize = 13.sp)
                Text("2. 是否有『检查更新』入口", color = TextMain, fontSize = 13.sp)
                Text("3. 是否有『问玄师』入口", color = TextMain, fontSize = 13.sp)
                Text("4. 检查更新页是否有『v1.7 音效测试点』", color = TextMain, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text("看不到这些，就是旧包。", color = Cinnabar, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
