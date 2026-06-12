package com.aning.xuanxue.feature.ai

data class CompassAiContext(
    val degree: Int,
    val mountain: String,
    val bagua: String,
    val fiveElement: String,
    val fenjin: String,
    val magneticUt: Float,
    val accuracyText: String
)

object PendingAiPromptStore {
    private var pendingPrompt: String? = null
    fun set(prompt: String) { pendingPrompt = prompt }
    fun consume(): String? {
        val p = pendingPrompt
        pendingPrompt = null
        return p
    }
}
