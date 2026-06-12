package com.aning.xuanxue.feature.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** AI 服务配置（可插拔：任意 OpenAI 兼容接口） */
data class AiConfig(
    val baseUrl: String = "",
    val apiKey: String = "",
    val model: String = ""
) {
    val isReady: Boolean get() = baseUrl.isNotBlank() && apiKey.isNotBlank() && model.isNotBlank()
}

/** 服务商预设（均为 OpenAI 兼容格式） */
data class Preset(val name: String, val baseUrl: String, val model: String)

val PRESETS = listOf(
    Preset("DeepSeek", "https://api.deepseek.com/v1", "deepseek-chat"),
    Preset("Kimi (月之暗面)", "https://api.moonshot.cn/v1", "moonshot-v1-8k"),
    Preset("通义千问", "https://dashscope.aliyuncs.com/compatible-mode/v1", "qwen-plus"),
    Preset("OpenRouter", "https://openrouter.ai/api/v1", "deepseek/deepseek-chat"),
    Preset("OpenAI", "https://api.openai.com/v1", "gpt-4o-mini"),
    Preset("自定义", "", ""),
)

object AiStore {
    private const val FILE = "ai_config"

    private var secureAvailable: Boolean = true
    private var memoryConfig: AiConfig? = null   // Keystore 失败时的会话内存存储

    /** 安全存储是否可用（Keystore 正常） */
    val isSecureAvailable: Boolean get() = secureAvailable

    private fun prefs(ctx: Context): SharedPreferences? = try {
        val key = MasterKey.Builder(ctx)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            ctx, FILE, key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        secureAvailable = false
        null   // 不再静默降级到明文 SharedPreferences，保护用户 Key 安全
    }

    fun load(ctx: Context): AiConfig {
        if (!secureAvailable) {
            return memoryConfig ?: AiConfig()
        }
        val p = prefs(ctx) ?: return AiConfig()
        return AiConfig(
            baseUrl = p.getString("base_url", "") ?: "",
            apiKey = p.getString("api_key", "") ?: "",
            model = p.getString("model", "") ?: ""
        )
    }

    fun save(ctx: Context, c: AiConfig) {
        if (!secureAvailable) {
            memoryConfig = c   // 仅本次会话内存保存，不落盘到不安全存储
            return
        }
        val p = prefs(ctx) ?: return
        p.edit()
            .putString("base_url", c.baseUrl.trim().trimEnd('/'))
            .putString("api_key", c.apiKey.trim())
            .putString("model", c.model.trim())
            .apply()
    }

    fun clear(ctx: Context) {
        memoryConfig = null
        if (!secureAvailable) return
        prefs(ctx)?.edit()?.clear()?.apply()
    }
}
