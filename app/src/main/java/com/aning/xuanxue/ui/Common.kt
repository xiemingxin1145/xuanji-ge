package com.aning.xuanxue.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Ink,
        topBar = {
            TopAppBar(
                title = { Text(title, color = GoldBright, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = Gold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Ink,
                    titleContentColor = GoldBright
                )
            )
        }
    ) { padding -> content(padding) }
}

@Composable
fun XCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = InkSurface,
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.25f))
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            Modifier
                .size(4.dp, 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Gold)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = GoldBright, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** 标签 + 值 一行 */
@Composable
fun KV(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = TextMain) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(label, color = TextSub, fontSize = 14.sp, modifier = Modifier.width(76.dp))
        Spacer(Modifier.width(8.dp))
        Text(value, color = valueColor, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ScrollColumn(
    padding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

val GoldVerticalBrush = Brush.verticalGradient(listOf(GoldBright, Gold))
