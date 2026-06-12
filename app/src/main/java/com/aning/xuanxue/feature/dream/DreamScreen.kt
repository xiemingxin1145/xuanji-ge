package com.aning.xuanxue.feature.dream

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class DreamRecord(
    val time: String,
    val content: String,
    val mood: String,
    val keywords: String
)

@Composable
fun DreamScreen(
    onBack: () -> Unit,
    onAiPrompt: (String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }
    val records = remember { mutableStateListOf<DreamRecord>() }

    XScaffold(title = "梦境记录", onBack = onBack) { padding ->
        ScrollColumn(padding) {
            XCard(Modifier.fillMaxWidth()) {
                SectionTitle("记一段梦")
                Spacer(Modifier.height(8.dp))
                Text(
                    "先记录梦境，再看民俗象意与情绪线索。第一版仅保存在本次页面中，后续可接本地历史记录。",
                    color = TextSub,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            XCard(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    placeholder = { Text("我梦见……", color = TextSub) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = fieldColors()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = mood,
                    onValueChange = { mood = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("醒来后的情绪：害怕 / 放松 / 惊讶 / 难过……", color = TextSub, fontSize = 12.sp) },
                    colors = fieldColors()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("关键词：水、蛇、旧人、房屋、飞行……", color = TextSub, fontSize = 12.sp) },
                    colors = fieldColors()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                records.add(0, DreamRecord(nowText(), content.trim(), mood.trim(), keywords.trim()))
                                content = ""
                                mood = ""
                                keywords = ""
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                    ) { Text("记录") }
                    Button(
                        onClick = {
                            val prompt = buildDreamPrompt(content, mood, keywords)
                            onAiPrompt(prompt)
                        },
                        enabled = content.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                    ) { Text("问 AI 解梦") }
                }
            }

            if (records.isNotEmpty()) {
                SectionTitle("本次记录")
                records.forEach { record ->
                    XCard(Modifier.fillMaxWidth()) {
                        Text(record.time, color = GoldBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(record.content, color = TextMain, fontSize = 13.sp, lineHeight = 20.sp)
                        if (record.mood.isNotBlank()) Text("情绪：${record.mood}", color = Jade, fontSize = 12.sp)
                        if (record.keywords.isNotBlank()) Text("关键词：${record.keywords}", color = TextSub, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { onAiPrompt(buildDreamPrompt(record.content, record.mood, record.keywords)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Cinnabar, contentColor = TextMain)
                        ) { Text("用这条问 AI") }
                    }
                }
            }

            Text(
                "梦境解释仅作民俗文化与自我观察参考，不作为现实预兆或心理诊断。",
                color = TextSub,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Gold,
    unfocusedBorderColor = TextSub.copy(alpha = 0.35f),
    focusedTextColor = TextMain,
    unfocusedTextColor = TextMain,
    cursorColor = Gold
)

private fun buildDreamPrompt(content: String, mood: String, keywords: String): String = buildString {
    appendLine("【梦境记录】")
    appendLine("梦境内容：$content")
    if (mood.isNotBlank()) appendLine("醒后情绪：$mood")
    if (keywords.isNotBlank()) appendLine("关键词：$keywords")
    appendLine()
    appendLine("请从民俗象意、情绪线索、现实提醒三个角度温和分析，并生成一句今日印记。")
    appendLine("请不要把梦境说成确定预兆，不制造焦虑，如涉及严重困扰请建议寻求专业帮助。")
}

private fun nowText(): String = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())
