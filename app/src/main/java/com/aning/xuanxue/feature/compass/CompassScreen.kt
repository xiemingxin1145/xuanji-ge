package com.aning.xuanxue.feature.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aning.xuanxue.ui.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

// 二十四山 (从正北/子起，顺时针每 15°)
private val MOUNTAINS = listOf(
    "子", "癸", "丑", "艮", "寅", "甲", "卯", "乙", "辰", "巽", "巳", "丙",
    "午", "丁", "未", "坤", "申", "庚", "酉", "辛", "戌", "乾", "亥", "壬"
)
// 后天八卦 (方位角 -> 卦名)，正北=坎
private val BAGUA = listOf(
    0 to "坎", 45 to "艮", 90 to "震", 135 to "巽",
    180 to "离", 225 to "坤", 270 to "兑", 315 to "乾"
)

private fun dirName(deg: Float): String {
    val names = listOf("正北", "东北", "正东", "东南", "正南", "西南", "正西", "西北")
    return names[(((deg + 22.5f) % 360f) / 45f).toInt() % 8]
}

@Composable
private fun rememberAzimuth(): State<Float> {
    val context = LocalContext.current
    val azimuth = remember { mutableFloatStateOf(0f) }
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val rot = FloatArray(9)
        val orient = FloatArray(3)
        var cur = 0f
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                if (e.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return
                SensorManager.getRotationMatrixFromVector(rot, e.values)
                SensorManager.getOrientation(rot, orient)
                var deg = Math.toDegrees(orient[0].toDouble()).toFloat()
                deg = (deg + 360f) % 360f
                // 低通平滑(处理 0/360 跨越)
                var diff = deg - cur
                if (diff > 180) diff -= 360
                if (diff < -180) diff += 360
                cur = (cur + diff * 0.15f + 360f) % 360f
                azimuth.floatValue = cur
            }

            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        if (sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sm.unregisterListener(listener) }
    }
    return azimuth
}

@Composable
fun CompassScreen(onBack: () -> Unit) {
    val azimuth by rememberAzimuth()
    val mountain = MOUNTAINS[((azimuth / 15f).roundToInt()) % 24]

    XScaffold(title = "风水罗盘", onBack = onBack) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text("${azimuth.roundToInt()}°", color = GoldBright, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text("${dirName(azimuth)} · 坐 $mountain 山", color = TextMain, fontSize = 16.sp)
            Spacer(Modifier.height(24.dp))

            Luopan(azimuth, Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp))

            Spacer(Modifier.height(20.dp))
            XCard(Modifier.fillMaxWidth()) {
                Text(
                    "把手机平放，红针指向方位即为朝向。盘面随地磁旋转，顶端三角对应你正对着的方位与山向。",
                    color = TextSub, fontSize = 13.sp, lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun Luopan(azimuth: Float, modifier: Modifier) {
    val goldArgb = Gold.toArgb()
    val brightArgb = GoldBright.toArgb()
    val subArgb = TextSub.toArgb()
    val cinnabar = Cinnabar
    Canvas(modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = min(cx, cy)

        // 固定顶端指示三角(朝向标)
        val nc = drawContext.canvas.nativeCanvas

        rotate(degrees = -azimuth, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
            val canvas = drawContext.canvas.nativeCanvas

            val ringPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.STROKE
                color = goldArgb
                alpha = 120
            }
            // 三圈
            canvas.drawCircle(cx, cy, r * 0.98f, ringPaint.apply { strokeWidth = 3f })
            canvas.drawCircle(cx, cy, r * 0.74f, ringPaint.apply { strokeWidth = 2f; alpha = 90 })
            canvas.drawCircle(cx, cy, r * 0.50f, ringPaint.apply { strokeWidth = 2f; alpha = 90 })
            canvas.drawCircle(cx, cy, r * 0.26f, ringPaint.apply { strokeWidth = 2f; alpha = 70 })

            // 刻度 (每 15°)
            val tick = android.graphics.Paint().apply {
                isAntiAlias = true; color = goldArgb; alpha = 100; strokeWidth = 2f
            }
            for (i in 0 until 24) {
                val a = Math.toRadians((i * 15).toDouble())
                val sx = cx + (r * 0.86f) * sin(a).toFloat()
                val sy = cy - (r * 0.86f) * cos(a).toFloat()
                val ex = cx + (r * 0.98f) * sin(a).toFloat()
                val ey = cy - (r * 0.98f) * cos(a).toFloat()
                canvas.drawLine(sx, sy, ex, ey, tick)
            }

            // 二十四山 (外圈)
            val mtPaint = android.graphics.Paint().apply {
                isAntiAlias = true; color = brightArgb; textSize = r * 0.072f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            for (i in MOUNTAINS.indices) {
                val ang = (i * 15).toFloat()
                canvas.save()
                canvas.rotate(ang, cx, cy)
                // 正北方(子)用朱砂强调
                mtPaint.color = if (i == 0) cinnabar.toArgb() else brightArgb
                canvas.drawText(MOUNTAINS[i], cx, cy - r * 0.81f + mtPaint.textSize / 3, mtPaint)
                canvas.restore()
            }

            // 八卦 (中圈)
            val gPaint = android.graphics.Paint().apply {
                isAntiAlias = true; color = goldArgb; textSize = r * 0.085f
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            for ((deg, name) in BAGUA) {
                canvas.save()
                canvas.rotate(deg.toFloat(), cx, cy)
                canvas.drawText(name, cx, cy - r * 0.60f + gPaint.textSize / 3, gPaint)
                canvas.restore()
            }

            // 中心红针(指南北)
            val needle = android.graphics.Paint().apply { isAntiAlias = true }
            val northP = android.graphics.Path().apply {
                moveTo(cx, cy - r * 0.40f); lineTo(cx - r * 0.05f, cy); lineTo(cx + r * 0.05f, cy); close()
            }
            val southP = android.graphics.Path().apply {
                moveTo(cx, cy + r * 0.40f); lineTo(cx - r * 0.05f, cy); lineTo(cx + r * 0.05f, cy); close()
            }
            needle.color = cinnabar.toArgb()
            canvas.drawPath(northP, needle)
            needle.color = goldArgb
            canvas.drawPath(southP, needle)
            // 太极心
            needle.color = Ink.toArgb()
            canvas.drawCircle(cx, cy, r * 0.06f, needle)
            needle.style = android.graphics.Paint.Style.STROKE
            needle.color = brightArgb; needle.strokeWidth = 3f
            canvas.drawCircle(cx, cy, r * 0.06f, needle)
        }

        // 顶端固定朝向三角(不随盘旋转)
        val triPaint = android.graphics.Paint().apply { isAntiAlias = true; color = brightArgb }
        val tri = android.graphics.Path().apply {
            moveTo(cx, cy - r * 0.99f)
            lineTo(cx - r * 0.04f, cy - r * 0.90f)
            lineTo(cx + r * 0.04f, cy - r * 0.90f)
            close()
        }
        nc.drawPath(tri, triPaint)
    }
}
