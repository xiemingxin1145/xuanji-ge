package com.aning.xuanxue.feature.ai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.R
import com.aning.xuanxue.ui.*
import com.nlf.calendar.Solar
import kotlinx.coroutines.launch
import java.util.Calendar

private fun todayContext(): String {
    val c = Calendar.getInstance()
    val solar = Solar.fromYmd(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    val l = solar.lunar
    return "今天是公历${solar.year}年${solar.month}月${solar.day}日，" +
            "农历${l.monthInChinese}月${l.dayInChinese}，${l.yearInGanZhi}年${l.monthInGanZhi}月${l.dayInGanZhi}日，" +
            "宜：${l.dayYi.take(6).joinToString("、")}；忌：${l.dayJi.take(6).joinToString("、")}；" +
            "冲${l.dayChongDesc}煞${l.daySha}，值神${l.dayTianShen}（${l.dayTianShenLuck}）。"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiChatScreen(onBack: () -> Unit, onSettings: () -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var config by remember { mutableStateOf(AiStore.load(ctx)) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val msgs = remember { mutableStateListOf<ChatMsg>() }
    val listState = rememberLazyListState()

    // 回到此页时刷新配置（从设置页返回）
    LaunchedEffect(Unit) { config = AiStore.load(ctx) }

    fun send(text: String) {
        if (text.isBlank() || loading) return
        if (!config.isReady) return
        msgs.add(ChatMsg("user", text))
        input = ""
        loading = true
        scope.launch {
            listState.animateScrollToItem((msgs.size - 1).coerceAtLeast(0))
            val r = AiClient.chat(config, msgs.toList())
            loading = false
            msgs.add(
                if (r.isSuccess) ChatMsg("assistant", r.getOrThrow())
                else ChatMsg("assistant", "（请求失败：${r.exceptionOrNull()?.message}）")
            )
            listState.animateScrollToItem((msgs.size - 1).coerceAtLeast(0))
        }
    }

    Scaffold(
        containerColor = Ink,
        topBar = {
            TopAppBar(
                title = { Text("AI 玄师", color = GoldBright) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Gold)
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Filled.Settings, "设置", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Ink)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            AiMasterHeader(
                modelName = config.model.ifBlank { "未接入" },
                isReady = config.isReady,
                onSettings = onSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            if (!config.isReady) {
                XCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text("尚未接入大模型。", color = TextMain, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "填入你自己的 API Key（DeepSeek / Kimi / 通义 / OpenAI 等任意 OpenAI 兼容接口），费用走你的账户，应用不经手。",
                        color = TextSub, fontSize = 12.sp, lineHeight = 17.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                    ) { Text("去接入") }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(msgs) { m -> Bubble(m) }
                if (loading) item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(16.dp), color = Gold, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("玄师推演中…", color = TextSub, fontSize = 13.sp)
                    }
                }
            }

            // 快捷提问
            if (config.isReady) {
                FlowRow(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { send(todayContext() + "请为我解读今日运势要点。") },
                        label = { Text("今日运势", fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = InkSurface2, labelColor = TextMain)
                    )
                    AssistChip(
                        onClick = { input = "我昨晚梦见：" },
                        label = { Text("解梦", fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = InkSurface2, labelColor = TextMain)
                    )
                    AssistChip(
                        onClick = { input = "我摇得本卦【】，动爻【】，变卦【】，所问之事：" },
                        label = { Text("解卦", fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = InkSurface2, labelColor = TextMain)
                    )
                }
            }

            // 输入栏
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = input, onValueChange = { input = it },
                    placeholder = { Text("问玄师…", color = TextSub, fontSize = 14.sp) },
                    enabled = config.isReady && !loading,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold, unfocusedBorderColor = TextSub.copy(alpha = 0.4f),
                        focusedTextColor = TextMain, unfocusedTextColor = TextMain, cursorColor = Gold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                FilledIconButton(
                    onClick = { send(input) },
                    enabled = config.isReady && !loading && input.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Gold, contentColor = Ink)
                ) { Icon(Icons.AutoMirrored.Filled.Send, "发送") }
            }
        }
    }
}

@Composable
private fun AiMasterHeader(
    modelName: String,
    isReady: Boolean,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    XCard(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(InkSurface2),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.img_ai_master),
                    contentDescription = "AI玄师形象",
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("玄微子 · AI玄师", color = GoldBright, fontSize = 17.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (isReady) "已接入模型：$modelName" else "未接入模型 · 请先配置 API Key",
                    color = TextSub,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                Spacer(Modifier.height(4.dp))
                Text("传统文化参考 · 现实建议优先", color = TextSub.copy(alpha = 0.75f), fontSize = 11.sp)
            }
            TextButton(onClick = onSettings) {
                Text(if (isReady) "切换" else "接入", color = Gold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun Bubble(m: ChatMsg) {
    val isUser = m.role == "user"
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Box(
            Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isUser) Gold.copy(alpha = 0.85f) else InkSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                m.content,
                color = if (isUser) Ink else TextMain,
                fontSize = 14.sp, lineHeight = 21.sp
            )
        }
    }
}
