package com.aning.xuanxue.feature.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiSettingsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val saved = remember { AiStore.load(ctx) }

    var baseUrl by remember { mutableStateOf(saved.baseUrl) }
    var apiKey by remember { mutableStateOf(saved.apiKey) }
    var model by remember { mutableStateOf(saved.model) }
    var showKey by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var testing by remember { mutableStateOf(false) }

    XScaffold(title = "AI 玄师 · 接入设置", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("快速选择服务商")
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PRESETS.forEach { p ->
                        AssistChip(
                            onClick = {
                                if (p.baseUrl.isNotBlank()) { baseUrl = p.baseUrl; model = p.model }
                                status = null
                            },
                            label = { Text(p.name, fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = InkSurface2, labelColor = TextMain
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                enabled = true, borderColor = Gold.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "任意 OpenAI 兼容接口均可接入。费用走你自己的 Key，应用不经手。",
                    color = TextSub, fontSize = 11.sp, lineHeight = 16.sp
                )
            }

            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("接入参数")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = baseUrl, onValueChange = { baseUrl = it; status = null },
                    label = { Text("Base URL（含 /v1）", fontSize = 12.sp) },
                    singleLine = true, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = apiKey, onValueChange = { apiKey = it; status = null },
                    label = { Text("API Key", fontSize = 12.sp) },
                    singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showKey = !showKey }) {
                            Text(if (showKey) "隐藏" else "显示", color = Gold, fontSize = 12.sp)
                        }
                    },
                    colors = fieldColors(), modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = model, onValueChange = { model = it; status = null },
                    label = { Text("模型名称", fontSize = 12.sp) },
                    singleLine = true, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val c = AiConfig(baseUrl, apiKey, model)
                            AiStore.save(ctx, c)
                            status = true to "已保存"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                    ) { Text("保存", fontWeight = FontWeight.Bold) }
                    OutlinedButton(
                        onClick = {
                            val c = AiConfig(baseUrl, apiKey, model)
                            if (!c.isReady) { status = false to "请先填完整三项"; return@OutlinedButton }
                            testing = true; status = null
                            scope.launch {
                                val r = AiClient.test(c)
                                testing = false
                                status = if (r.isSuccess) {
                                    AiStore.save(ctx, c)
                                    true to "连接成功，已保存 ✓"
                                } else false to (r.exceptionOrNull()?.message ?: "连接失败")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !testing,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
                    ) {
                        if (testing) CircularProgressIndicator(Modifier.size(16.dp), color = Gold, strokeWidth = 2.dp)
                        else Text("测试连接", color = Gold)
                    }
                }
                status?.let { (ok, msg) ->
                    Spacer(Modifier.height(10.dp))
                    Text(msg, color = if (ok) Jade else Cinnabar, fontSize = 13.sp)
                }
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = {
                    AiStore.clear(ctx); baseUrl = ""; apiKey = ""; model = ""
                    status = true to "已清除本机保存的 Key"
                }) { Text("一键删除已存 Key", color = Cinnabar, fontSize = 13.sp) }
            }

            XCard(Modifier.fillMaxWidth()) {
                Text(
                    "Key 仅加密保存在本机（Android Keystore），不上传任何服务器；请求直连你填写的接口地址。",
                    color = TextSub, fontSize = 11.sp, lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Gold, unfocusedBorderColor = TextSub.copy(alpha = 0.5f),
    focusedLabelColor = Gold, unfocusedLabelColor = TextSub,
    focusedTextColor = TextMain, unfocusedTextColor = TextMain, cursorColor = Gold
)
