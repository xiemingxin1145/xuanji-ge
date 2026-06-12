package com.aning.xuanxue.feature.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** 对话消息 */
data class ChatMsg(val role: String, val content: String)

/** 玄师人设 + 合规边界（写死，不可被用户配置覆盖） */
const val XUANSHI_SYSTEM_PROMPT = """你是"玄机阁"应用中的 AI 玄师，精通易经六十四卦、八字命理、风水方位、老黄历宜忌与中国民俗文化。

回答风格：
- 先给传统民俗角度的解读（以"传统民俗认为""从象征意义看"起笔），再给现实层面的具体建议。
- 像说书人一样讲解，有画面感、有故事性，但不啰嗦，单次回答控制在三百字以内。
- 用户给出卦象、八字、坐向、黄历信息时，围绕这些信息解读，不要凭空编造未提供的内容。

合规边界（必须遵守）：
- 一切解读仅为传统文化参考与娱乐，不构成医疗、法律、投资建议。
- 严禁保证发财、转运、驱邪、治病等任何效果性承诺。
- 严禁恐吓用户（如"你必有灾""不化解就会倒霉"），严禁诱导购买符咒法事。
- 涉及健康问题建议就医，涉及重大决策建议理性评估。"""

object AiClient {

    /**
     * 调用 OpenAI 兼容的 /chat/completions
     * @return Result：成功为回复文本，失败为可读错误信息
     */
    suspend fun chat(config: AiConfig, history: List<ChatMsg>): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(config.baseUrl.trimEnd('/') + "/chat/completions")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 20_000
                    readTimeout = 120_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${config.apiKey}")
                }

                val messages = JSONArray()
                messages.put(JSONObject().put("role", "system").put("content", XUANSHI_SYSTEM_PROMPT))
                history.forEach { m ->
                    messages.put(JSONObject().put("role", m.role).put("content", m.content))
                }
                val body = JSONObject()
                    .put("model", config.model)
                    .put("messages", messages)
                    .put("temperature", 0.8)

                conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }

                val code = conn.responseCode
                val text = (if (code in 200..299) conn.inputStream else conn.errorStream)
                    ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""

                if (code !in 200..299) {
                    val msg = try {
                        JSONObject(text).optJSONObject("error")?.optString("message") ?: text.take(200)
                    } catch (e: Exception) { text.take(200) }
                    return@withContext Result.failure(Exception("HTTP $code：$msg"))
                }

                val content = JSONObject(text)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                Result.success(content.trim())
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "网络请求失败"))
            }
        }

    /** 测试连接：发一条极短消息 */
    suspend fun test(config: AiConfig): Result<String> =
        chat(config, listOf(ChatMsg("user", "回复“连接成功”四个字即可。")))
}
