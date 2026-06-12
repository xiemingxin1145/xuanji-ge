package com.aning.xuanxue.feature.ar

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.aning.xuanxue.core.xuanji.XuanjiResonance
import com.aning.xuanxue.ui.*
import kotlin.math.*

/**
 * 最后一个道士 · AR鬼魂探测
 *
 * 架构说明：
 * 1. 检查相机权限 + ARCore设备支持
 * 2. 通过 AndroidView 嵌入 ARCore GLSurfaceView（预留接口）
 * 3. Compose层叠加"玄机感应"HUD
 * 4. 根据方位角+时间+玄机共鸣实时生成鬼魂感应信号
 *
 * 完整AR渲染：接入 io.github.sceneview:arsceneview 后可激活3D鬼魂模型
 */

@Composable
fun ARGhostScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCameraPermission = it
    }

    val arSupported = remember {
        try {
            val pm = context.packageManager
            pm.hasSystemFeature("android.hardware.camera.ar") ||
            pm.hasSystemFeature("android.hardware.camera")
        } catch (e: Exception) { false }
    }

    when {
        !hasCameraPermission -> CameraPermissionRequest { launcher.launch(Manifest.permission.CAMERA) }
        !arSupported         -> ARNotSupportedScreen(onBack)
        else                 -> ARGhostActiveScreen(onBack)
    }
}

@Composable
private fun CameraPermissionRequest(onRequest: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0xFF050510)), contentAlignment = Alignment.Center) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📷", fontSize = 64.sp)
            Text("需要相机权限", color = TextMain, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                "AR鬼魂探测需要使用相机，以便将玄机感应叠加在现实画面中。\n\n你的相机数据不会被上传或保存。",
                color = TextSub, fontSize = 14.sp, lineHeight = 22.sp, textAlign = TextAlign.Center
            )
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FBE).copy(alpha = 0.2f), contentColor = Color(0xFF7B2FBE)),
                border = BorderStroke(1.dp, Color(0xFF7B2FBE)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("授予权限", fontSize = 16.sp) }
        }
    }
}

@Composable
private fun ARNotSupportedScreen(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0xFF050510)), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("设备不支持AR", color = Cinnabar, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("但你仍可以使用「鬼怪雷达」感应附近的玄机波动", color = TextSub, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.07f)),
                shape = RoundedCornerShape(12.dp)) { Text("返回", color = TextSub) }
        }
    }
}

@Composable
private fun ARGhostActiveScreen(onBack: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "arInf")
    val scan by inf.animateFloat(0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "scan")
    val pulse by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse), label = "pulse")
    val time by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart), label = "time")

    // 模拟感应信号（真实版接compass/传感器数据）
    val signals = remember {
        listOf(
            Triple(0.25f, 0.3f,  "游魂"),
            Triple(0.7f,  0.55f, "水鬼"),
            Triple(0.45f, 0.75f, "山魅")
        )
    }
    var selectedSignal by remember { mutableStateOf<Triple<Float,Float,String>?>(null) }
    val resonance = remember { XuanjiResonance.calculate(65, 70, 55) }

    Box(Modifier.fillMaxSize().background(Color(0xFF000008))) {
        // 摄像头预览层（真实ARCore激活后替换为ArSceneView）
        CameraPreviewPlaceholder()

        // AR HUD 覆盖层
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f; val cy = size.height / 2f

            // 扫描光环
            rotate(scan, Offset(cx, cy)) {
                drawArc(Brush.sweepGradient(listOf(Color.Transparent, Color(0xFF00FF9C).copy(alpha = 0.35f), Color.Transparent)),
                    -30f, 60f, false, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
            }
            drawCircle(Color(0xFF00FF9C).copy(alpha = 0.1f), size.minDimension * 0.35f, Offset(cx, cy))
            drawCircle(Color(0xFF00FF9C).copy(alpha = 0.06f), size.minDimension * 0.5f, Offset(cx, cy))

            // 感应信号点
            signals.forEach { (rx, ry, _) ->
                val sx = rx * size.width; val sy = ry * size.height
                val a = 0.4f + sin(time * 6.28f + rx * 10f).toFloat() * 0.3f
                drawCircle(Color(0xFFFF0040).copy(alpha = a), 12f, Offset(sx, sy))
                drawCircle(Color(0xFFFF0040).copy(alpha = a * 0.3f), 25f, Offset(sx, sy))
            }
        }

        // 感应信号标签
        signals.forEach { sig ->
            Box(Modifier.fillMaxSize()) {
                val lx = sig.first * 360 - 60
                val ly = sig.second * 700 - 40
                Surface(
                    Modifier.offset(lx.dp, ly.dp).clickable { selectedSignal = sig },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFF0040).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFFFF0040).copy(alpha = pulse))
                ) {
                    Text("⚠ ${sig.third}", color = Color(0xFFFF0040), fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                }
            }
        }

        // 顶部HUD
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.Black.copy(alpha = 0.5f)) {
                    Text("← 返回", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp,
                        modifier = Modifier.clickable(onClick = onBack).padding(horizontal = 12.dp, vertical = 6.dp))
                }
                Surface(shape = RoundedCornerShape(10.dp), color = Color.Black.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color(0xFF00FF9C).copy(alpha = 0.4f))) {
                    Text("AR玄机探测", color = Color(0xFF00FF9C), fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                Surface(shape = RoundedCornerShape(10.dp), color = Color.Black.copy(alpha = 0.5f)) {
                    Text(resonance.level.displayName, color = Color(0xFF7B2FBE), fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                }
            }
        }

        // 底部感应面板
        Column(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            selectedSignal?.let { sig ->
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, Color(0xFFFF0040).copy(alpha = 0.6f))) {
                    Column(Modifier.padding(14.dp)) {
                        Text("感应到【${sig.third}】", color = Color(0xFFFF0040), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("距离：约 ${(15..35).random()} 米 | 方位：${listOf("东北","西南","正东","东南").random()}", color = TextSub, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { selectedSignal = null }, Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0040).copy(alpha = 0.15f), contentColor = Color(0xFFFF0040)),
                            border = BorderStroke(1.dp, Color(0xFFFF0040)),
                            shape = RoundedCornerShape(10.dp)) {
                            Text("追踪锁定", fontSize = 14.sp)
                        }
                    }
                }
            }
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.Black.copy(alpha = 0.6f)) {
                Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("发现信号 ${signals.size} 处", color = Color(0xFF00FF9C), fontSize = 12.sp)
                    Text("|", color = TextSub, fontSize = 12.sp)
                    Text("玄机共鸣 ${resonance.total}", color = Color(0xFF7B2FBE), fontSize = 12.sp)
                    Text("|", color = TextSub, fontSize = 12.sp)
                    Text("扫描中…", color = TextSub, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewPlaceholder() {
    // 真实ARCore版本：替换为 ArSceneView（需要 sceneview 依赖）
    // implementation("io.github.sceneview:arsceneview:2.2.1")
    // AndroidView(factory = { ctx -> ArSceneView(ctx).apply { setupSession() } })

    // 现阶段：暗色渐变模拟夜视摄像头效果
    val inf = rememberInfiniteTransition(label = "cam")
    val noise by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Reverse), label = "n")
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Brush.radialGradient(
            listOf(Color(0xFF0A1510), Color(0xFF000308), Color.Black),
            radius = size.maxDimension * 0.7f
        ))
        // 噪点模拟
        repeat(200) { i ->
            val px = ((i * 137.5f + noise * 50f) % size.width)
            val py = ((i * 83.7f + noise * 30f) % size.height)
            drawCircle(Color.White.copy(alpha = 0.01f + noise * 0.02f), 1f, Offset(px, py))
        }
    }
    // 提示文本
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📡", fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text("摄像头感应中", color = Color(0xFF00FF9C).copy(alpha = 0.5f), fontSize = 13.sp)
            Text("接入 SceneView 后启用全AR渲染", color = TextSub, fontSize = 10.sp)
        }
    }
}
