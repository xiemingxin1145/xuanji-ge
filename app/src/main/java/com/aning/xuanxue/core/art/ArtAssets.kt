package com.aning.xuanxue.core.art

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.aning.xuanxue.R

/**
 * 玄机阁 · 美术资源统一索引层（P0 基础设施）
 *
 * 设计原则（采纳 GPT 的 P0 方案）：
 *   1. 所有 R.drawable.xxx 集中在此映射，页面不散写。
 *   2. 资源缺失返回 null —— 由显示组件 fallback，绝不让缺图炸 UI。
 *   3. 命名严格对齐三方约定：bg_case_* / ghost_* / tool_* / splash_*
 *
 * 接入新图的唯一改动点：在对应 map 里加一行 "id" to R.drawable.文件名。
 * GPT 出图、阿宁存图后，Claude 只需在这里登记，全项目自动生效。
 *
 * 当前所有条目为 null（尚无美术资源）—— 全项目走 fallback，
 * 编译运行完全正常。图一到位，把 null 换成 R.drawable.xxx 即可。
 */
object ArtAssets {

    // ── 场景背景 bg_case_* ───────────────────────
    // key = caseId 或场景标识
    private val backgrounds: Map<String, Int?> = mapOf(
        "old_house" to R.drawable.bg_case_old_house,
        "temple"    to null,   // R.drawable.bg_case_temple
        "hospital"  to null,
        "graveyard" to null,
        "subway"    to null,
        "城郊荒宅"   to R.drawable.bg_case_old_house    // 中文场景名映射到荒宅
    )

    // ── 鬼怪立绘 ghost_*（透明PNG/WebP）──────────
    // key = GhostRegistry 的 ghost id
    private val ghosts: Map<String, Int?> = mapOf(
        "you_hun"  to R.drawable.ghost_you_hun,
        "shui_gui" to null,
        "yuan_hun" to null,
        "li_gui"   to null,
        "shan_mei" to null,
        "ye_cha"   to null,
        "jiu_mei"  to null,
        "gu_shen"  to null
    )

    // ── 法器图标 tool_*（透明，512²）─────────────
    // key = XuanTool.name 的小写 / 工具标识
    private val tools: Map<String, Int?> = mapOf(
        "luopan"      to null, // R.drawable.tool_luopan
        "wind_bell"   to null,
        "bronze_mirror" to null,
        "thermo_talisman" to null,
        "geo_drum"    to null,
        "omen_lots"   to null,
        "cinnabar_seal" to null,
        "peach_sword" to null,
        "river_seal"  to null,
        "demon_mirror" to null,
        "thunder_wood" to null,
        "soul_bell"   to null,
        "soothe_talisman" to null
    )

    // ── 启动封面 splash_* ────────────────────────
    private val splash: Int? = null   // R.drawable.splash_main

    // ─────────────────────────────────────────────
    // 查询接口（GPT 指定的三个函数签名）
    // ─────────────────────────────────────────────
    @DrawableRes
    fun caseBackgroundRes(caseId: String): Int? = backgrounds[caseId]

    @DrawableRes
    fun ghostRes(ghostId: String): Int? = ghosts[ghostId]

    @DrawableRes
    fun toolRes(toolId: String): Int? = tools[toolId]

    @DrawableRes
    fun splashRes(): Int? = splash

    /** 是否已有任何美术资源（用于全局判断要不要走美术模式） */
    fun hasAnyArt(): Boolean =
        backgrounds.values.any { it != null } ||
        ghosts.values.any { it != null } ||
        tools.values.any { it != null }
}
