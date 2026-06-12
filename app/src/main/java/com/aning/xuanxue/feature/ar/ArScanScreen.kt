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
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import com.aning.xuanxue.ui.*
import kotlin.math.*
import kotlin.random.Random

/**
 * AR实景探秘 — 玄机感应
 * 设备支持ARCore时展示真实AR；不支持时用相机模拟界面 + 玄机叠加层
 * Phase 1：UI框架 + 权限流程 + 模拟AR叠加
 * Phase 2（后续）：接入真实ARCore Session + PlaneDetection
 */
@Composable
fun ArScanScreen(onBack: () -> Unit, onTrigger: (String) -> Unit) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    if (!hasCameraPermission) {
        CameraPermissionRequest { permLauncher.launch(Manifest.permission.CAMERA) }
        return
    }

    ArScanContent(onBack = onBack, onTrigger = onTrigger)
}

@Composable
private fun CameraPermissionRequest(onRequest: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("☯", fontSize = 64.sp, color = Color(0xFF7B2FBE))
            Text("AR玄机感应需要摄像头权限", color = TextMain, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text("将现实世界叠加玄学层，感知肉眼不可见的地脉、煞气与灵场", color = TextSub, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FBE).copy(alpha = 0.3f), contentColor = Color(0xFFCE93D8)),
                border = BorderStroke(1.dp, Color(0xFF7B2FBE))
            ) { Text("开启感应权限", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ArScanContent(onBack: () -> Unit, onTrigger: (String) -> Unit) {
    val inf = rememberInfiniteTransition(label = "ar")
    val scanLine by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), label = "sl")
    val rot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart), label = "ar_rot")
    val pulse by inf.animateFloat(0.3f, 0.9f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "ap")

    // 模拟AR感应结果（后期接真实PlaneDetection）
    var detectedNode by remember { mutableStateOf<ArNode?>(null) }
    var scanMessage by remember { mutableStateOf("正在感应周围玄机……") }
    var scanProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        scanMessage = "检测到异常地脉波动"
        kotlinx.coroutines.delay(1500)
        scanProgress = 0.6f
        scanMessage = "正在定位煞气节点……"
        kotlinx.coroutines.delay(2000)
        scanProgress = 1f
        detectedNode = ArNode("sha_qi_node", "煞气节点", "此处地脉汇聚，阴气较重，建议避开或以乾位罗盘压制", Offset(0.5f, 0.45f), NodeType.SHA_QI)
        scanMessage = "已锁定一处异常节点，点击查看"
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF050510))) {
        // 模拟相机底层（真实ARCore会替换这里）
        SimulatedCameraView()

        // AR叠加层
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height

            // 扫描线
            val lineY = scanLine * h
            drawLine(Color(0xFF00FF9C).copy(alpha = 0.5f), Offset(0f, lineY), Offset(w, lineY), 2f)
            drawRect(Color(0xFF00FF9C).copy(alpha = 0.04f),
                Offset(0f, lineY), androidx.compose.ui.geometry.Size(w, 60f))

            // 边角HUD框
            val cornerLen = 40f; val thick = 2.5f; val margin = 40f
            val corners = listOf(
                Offset(margin, margin) to listOf(Offset(margin + cornerLen, margin), Offset(margin, margin + cornerLen)),
                Offset(w - margin, margin) to listOf(Offset(w - margin - cornerLen, margin), Offset(w - margin, margin + cornerLen)),
                Offset(margin, h - margin) to listOf(Offset(margin + cornerLen, h - margin), Offset(margin, h - margin - cornerLen)),
                Offset(w - margin, h - margin) to listOf(Offset(w - margin - cornerLen, h - margin), Offset(w - margin, h - margin - cornerLen))
            )
            corners.forEach { (origin, ends) ->
                ends.forEach { end -> drawLine(Color(0xFF00FF9C), origin, end, thick) }
            }

            // 地脉流向线
            repeat(5) { i ->
                val x1 = i * w / 5f; val x2 = (i + 0.7f) * w / 5f
                drawLine(Color(0xFF7B2FBE).copy(alpha = 0.15f + sin(rot * Math.PI.toFloat() / 180f + i).toFloat().coerceAtLeast(0f) * 0.1f),
                    Offset(x1, 0f), Offset(x2, h), 1f)
            }

            // 罗盘叠加（中心）
            val cx = w / 2f; val cy = h / 2f; val r = 55f
            rotate(rot * 0.3f, Offset(cx, cy)) {
                repeat(8) { i ->
                    val a = Math.toRadians(i * 45.0)
                    drawLine(Color(0xFFFFD700).copy(alpha = 0.2f), Offset(cx, cy),
                        Offset(cx + r * cos(a).toFloat(), cy + r * sin(a).toFloat()), 1f)
                }
                drawCircle(Color(0xFFFFD700).copy(alpha = 0.1f), r, Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
            }
            drawCircle(Color(0xFFD62828).copy(alpha = pulse), 6f, Offset(cx, cy))

            // 检测到的节点
            detectedNode?.let { node ->
                val nx = node.relPos.x * w; val ny = node.relPos.y * h
                val nodeR = 22f + sin(pulse * Math.PI.toFloat()).toFloat() * 6f
                drawCircle(node.type.color.copy(alpha = 0.25f), nodeR * 2f, Offset(nx, ny))
                drawCircle(node.type.color.copy(alpha = 0.7f), nodeR, Offset(nx, ny),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                drawCircle(node.type.color, 5f, Offset(nx, ny))
            }
        }

        // 顶部HUD
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.5f)) {
                Text("← 退出", color = TextSub, modifier = Modifier.clickable(onClick = onBack)
                    .padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 13.sp)
            }
            Spacer(Modifier.weight(1f))
            Surface(shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.5f)) {
                Text("AR 玄机感应", color = Color(0xFF00FF9C), fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }

        // 底部信息面板
        Column(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 扫描进度
            if (scanProgress < 1f) {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.7f)) {
                    Column(Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("玄机扫描", color = Color(0xFF00FF9C), fontSize = 12.sp)
                            Text("${(scanProgress * 100).toInt()}%", color = Color(0xFF00FF9C), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(scanProgress, Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF00FF9C), trackColor = Color.White.copy(alpha = 0.1f))
                    }
                }
            }

            // 检测到节点时展示详情
            detectedNode?.let { node ->
                Surface(Modifier.fillMaxWidth().clickable { onTrigger(node.id) },
                    shape = RoundedCornerShape(14.dp), color = Color.Black.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, node.type.color.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(node.type.color))
                            Spacer(Modifier.width(8.dp))
                            Text(node.name, color = node.type.color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Text(node.type.label, color = TextSub, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(node.desc, color = TextMain, fontSize = 12.sp, lineHeight = 19.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("▶ 点击此处展开详细推演", color = node.type.color.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }

            // 状态消息
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.6f)) {
                Text(scanMessage, color = Color(0xFF00FF9C).copy(alpha = 0.8f), fontSize = 12.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
            }
        }
    }
}

// 简单的模拟相机视图（用Canvas绘制，后期替换为AndroidView+Camera2）
@Composable
private fun SimulatedCameraView() {
    val inf = rememberInfiniteTransition(label = "cam")
    val noise by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Restart), label = "n")
    Canvas(Modifier.fillMaxSize()) {
        // 模拟夜视/暗场景效果
        drawRect(Color(0xFF020208))
        repeat(20) { i ->
            val x = (i * 73f + noise * 10f) % size.width
            val y = (i * 137f + noise * 5f) % size.height
            drawCircle(Color(0xFF0A0A14), 80f + i * 12f, Offset(x, y))
        }
    }
}

// AR节点数据类
data class ArNode(
    val id: String,
    val name: String,
    val desc: String,
    val relPos: Offset, // 相对屏幕位置 0~1
    val type: NodeType
)

enum class NodeType(val label: String, val color: Color) {
    SHA_QI(    "煞气节点",   Color(0xFFD62828)),
    LING_CHANG("灵场",       Color(0xFF00FF9C)),
    DI_MAI(    "地脉节点",   Color(0xFF4FC3F7)),
    GUI_HUN(   "游魂踪迹",   Color(0xFF7B2FBE)),
    JIXIANG(   "吉祥方位",   Color(0xFFFFD700))
}
