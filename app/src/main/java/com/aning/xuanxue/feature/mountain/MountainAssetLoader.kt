package com.aning.xuanxue.feature.mountain

import android.content.Context
import org.json.JSONObject

object MountainAssetLoader {
    fun load(context: Context): List<MountainOracle> = runCatching {
        val text = context.assets.open("data/mountain24.json").bufferedReader().use { it.readText() }
        val root = JSONObject(text)
        val items = root.getJSONArray("items")
        List(items.length()) { index ->
            val item = items.getJSONObject(index)
            val cautionsJson = item.optJSONArray("cautions")
            val cautions = if (cautionsJson == null) emptyList() else List(cautionsJson.length()) { i -> cautionsJson.optString(i) }
            MountainOracle(
                mountain = item.optString("mountain"),
                facing = item.optString("facing"),
                range = item.optString("range"),
                palace = item.optString("palace"),
                yuanLong = item.optString("yuanLong"),
                yinYang = item.optString("yinYang"),
                elementHint = item.optString("elementHint"),
                usage = item.optString("usage"),
                cautions = cautions,
                flyingStarHint = item.optString("flyingStarHint")
            )
        }.filter { it.mountain.isNotBlank() }
    }.getOrDefault(emptyList())
}
