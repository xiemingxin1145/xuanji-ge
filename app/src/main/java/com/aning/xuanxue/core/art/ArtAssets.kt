package com.aning.xuanxue.core.art

import androidx.annotation.DrawableRes
import com.aning.xuanxue.R

/**
 * 玄机阁 · 美术资源统一索引层（P0 基础设施）
 *
 * 资源来源：Grok 41图包 v1（已批处理：场景缩放、鬼影/法器抠透明）
 *
 * 类别：
 *   backgrounds      场景背景（完整场景图，1080×1920）
 *   ghostScene       鬼怪场景卡（图鉴大图，完整场景）
 *   ghostSilhouette  鬼怪透明鬼影（捉鬼揭晓/AR叠加，透明720×1080）
 *   tools            法器图标（透明512×512）
 *   ui / splash      纹理与封面
 *
 * 缺图返回 null，由显示组件 fallback，绝不炸 UI。
 */
object ArtAssets {

    // ── 场景背景 ─────────────────────────────────
    private val backgrounds: Map<String, Int?> = mapOf(
        "old_house"     to R.drawable.bg_case_old_house,
        "well"          to R.drawable.bg_case_well,
        "temple"        to R.drawable.bg_case_temple,
        "ancestral_hall" to R.drawable.bg_case_ancestral_hall,
        "graveyard"     to R.drawable.bg_case_graveyard,
        "old_hospital"  to R.drawable.bg_case_old_hospital,
        "mountain_path" to R.drawable.bg_case_mountain_path,
        "fog_alley"     to R.drawable.bg_case_fog_alley,
        "城郊荒宅"       to R.drawable.bg_case_old_house,
        "home"          to R.drawable.bg_home_xuanji
    )

    // ── 鬼怪场景卡（图鉴大图）─────────────────────
    private val ghostScene: Map<String, Int?> = mapOf(
        "you_hun"  to R.drawable.ghost_scene_you_hun,
        "shui_gui" to R.drawable.ghost_scene_shui_gui,
        "yuan_hun" to R.drawable.ghost_scene_yuan_hun,
        "li_gui"   to R.drawable.ghost_scene_li_gui,
        "shan_mei" to R.drawable.ghost_scene_shan_mei,
        "ye_cha"   to R.drawable.ghost_scene_ye_cha,
        "jiu_mei"  to R.drawable.ghost_scene_jiu_mei,
        "gu_shen"  to R.drawable.ghost_scene_gu_shen
    )

    // ── 鬼怪透明鬼影（捉鬼揭晓/AR）────────────────
    private val ghostSilhouette: Map<String, Int?> = mapOf(
        "you_hun"  to R.drawable.ghost_silhouette_you_hun,
        "shui_gui" to R.drawable.ghost_silhouette_shui_gui,
        "yuan_hun" to R.drawable.ghost_silhouette_yuan_hun,
        "li_gui"   to R.drawable.ghost_silhouette_li_gui,
        "shan_mei" to R.drawable.ghost_silhouette_shan_mei,
        "ye_cha"   to R.drawable.ghost_silhouette_ye_cha,
        "jiu_mei"  to R.drawable.ghost_silhouette_jiu_mei,
        "gu_shen"  to R.drawable.ghost_silhouette_gu_shen
    )

    // ── 法器图标（透明）──────────────────────────
    private val tools: Map<String, Int?> = mapOf(
        "luopan"          to R.drawable.tool_luopan,
        "wind_bell"       to R.drawable.tool_wind_bell,
        "bronze_mirror"   to R.drawable.tool_bronze_mirror,
        "thermo_talisman" to R.drawable.tool_talisman,
        "geo_drum"        to R.drawable.tool_geo_drum,
        "omen_lots"       to R.drawable.tool_omen_lots,
        "cinnabar_seal"   to R.drawable.tool_cinnabar_seal,
        "peach_sword"     to R.drawable.tool_peach_sword,
        "river_seal"      to R.drawable.tool_river_seal,
        "demon_mirror"    to R.drawable.tool_demon_mirror,
        "thunder_wood"    to R.drawable.tool_thunder_wood,
        "soul_bell"       to R.drawable.tool_soul_bell,
        "soothe_talisman" to R.drawable.tool_soothe_talisman
    )

    private val splash: Int? = R.drawable.splash_main

    // ─────────────────────────────────────────────
    // 查询接口
    // ─────────────────────────────────────────────
    @DrawableRes fun caseBackgroundRes(caseId: String): Int? = backgrounds[caseId]
    @DrawableRes fun ghostRes(ghostId: String): Int? = ghostScene[ghostId]          // 默认返回场景卡
    @DrawableRes fun ghostSceneRes(ghostId: String): Int? = ghostScene[ghostId]
    @DrawableRes fun ghostSilhouetteRes(ghostId: String): Int? = ghostSilhouette[ghostId]
    @DrawableRes fun toolRes(toolId: String): Int? = tools[toolId]
    @DrawableRes fun splashRes(): Int? = splash

    fun hasAnyArt(): Boolean = true
}
