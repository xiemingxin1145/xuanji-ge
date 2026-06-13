package com.aning.xuanxue.feature.ghost

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.core.art.ArtAssets
import com.aning.xuanxue.ui.GoldBright
import com.aning.xuanxue.ui.TextSub

/**
 * 捉鬼成功后的美术揭晓卡。
 *
 * 这个组件只读 ArtAssets，不影响捉鬼状态机。
 */
@Composable
fun GhostRevealCard(
    ghost: GhostType,
    modifier: Modifier = Modifier
) {
    val res = ArtAssets.ghostSceneRes(ghost.id)
    Surface(
        modifier = modifier.fillMaxWidth().height(320.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.86f),
        border = BorderStroke(1.5.dp, ghost.rarity.color.copy(alpha = 0.85f)),
        shadowElevation = 12.dp
    ) {
        Box {
            if (res != null) {
                Image(
                    painter = painterResource(res),
                    contentDescription = ghost.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.62f to Color.Transparent,
                            1f to Color(0xEE0D0A14)
                        )
                    )
                )
            } else {
                Box(
                    Modifier.fillMaxSize().background(ghost.rarity.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("已镇压", color = ghost.rarity.color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(
                Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Text("镇压成功", color = GoldBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(ghost.name, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("${ghost.rarity.label} · ${ghost.element.label}属性 · 鬼气 +${ghost.dropEssence}", color = TextSub, fontSize = 12.sp)
            }
        }
    }
}
