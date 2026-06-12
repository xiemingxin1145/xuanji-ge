package com.aning.xuanxue.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ===== 水墨配金 国风调色 =====
val Ink = Color(0xFF15151A)        // 背景墨
val InkSurface = Color(0xFF20212A) // 卡片
val InkSurface2 = Color(0xFF2A2B36)
val Gold = Color(0xFFC9A86A)       // 描金
val GoldBright = Color(0xFFE6CB8C)
val Cinnabar = Color(0xFFB5413A)   // 朱砂(动爻/忌)
val Jade = Color(0xFF6FA68A)       // 青(宜/吉)
val TextMain = Color(0xFFECE7DD)   // 米白
val TextSub = Color(0xFF9A958C)    // 灰

private val XuanxueColors = darkColorScheme(
    primary = Gold,
    onPrimary = Ink,
    secondary = GoldBright,
    background = Ink,
    onBackground = TextMain,
    surface = InkSurface,
    onSurface = TextMain,
    surfaceVariant = InkSurface2,
    onSurfaceVariant = TextSub,
    error = Cinnabar,
)

@Composable
fun XuanxueTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = XuanxueColors,
        typography = Typography(),
        content = content
    )
}
