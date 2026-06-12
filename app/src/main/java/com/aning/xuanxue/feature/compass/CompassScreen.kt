package com.aning.xuanxue.feature.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

@Composable
private fun rememberAzimuthAndAccuracy(): State<Pair<Float, Int>> {
    val context = LocalContext.current
    val data = remember { mutableStateOf(0f to 0) }
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
                var diff = deg - cur
                if (diff > 180) diff -= 360
                if (diff < -180) diff += 360
                cur = (cur + diff * 0.12f + 360f) % 360f
                val accuracy = e.accuracy
                data.value = cur to accuracy
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        if (sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sm.unregisterListener(listener) }
    }
    return data
}

@Composable
fun CompassScreen(onBack: () -> Unit) {
    val sensorData by rememberAzimuthAndAccuracy()
    val azimuth = sensorData.first
    val accuracy = sensorData.second
    val mountain = CompassUtils.degreeToMountain24(azimuth)
    val bagua = CompassUtils.degreeToBagua(azimuth)
    val wuxing = CompassUtils.degreeToFiveElement(azimuth)
    val fenjin = CompassUtils.degreeToFenjin(azimuth)
    val magLevel = CompassUtils.magneticStrengthLevel(accuracy)
    val magText = CompassUtils.magneticLevelText(magLevel)

    XScaffold(title = "风水罗盘 · 玄门法器", onBack = onBack) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部信息
            Text("${azimuth.roundToInt()}°", color = GoldBright, fontSize = 42.sp, fontWeight = FontWeight.Bold)
            Text("坐 $mountain 山 · $bagua 卦 · $wuxing 行 · $fenjin", color = TextMain, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))

            // 专业罗盘 Canvas
            EnhancedLuopan(
                azimuth = azimuth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // 磁场状态卡
            XCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("磁场状态", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text(magText, color = if (magLevel >= 2) Jade else Cinnabar, fontSize = 13.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text("建议：手机平放，远离金属与强磁干扰后重新校准", color = TextSub, fontSize = 12.sp)
            }

            Spacer(Modifier.height(20.dp))

            // 三个入口按钮（预留）
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { /* TODO: 跳转分金页 */ },
                    modifier = Modifier.weight(1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
                ) { Text("分金", color = Gold) }

                OutlinedButton(
                    onClick = { /* TODO: 跳转纳水页 */ },
                    modifier = Modifier.weight(1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
                ) { Text("纳水", color = Gold) }

                Button(
                    onClick = { /* TODO: AI 解读当前坐向 */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                ) { Text("AI 解读") }
            }
        }
    }
}

@Composable
private fun EnhancedLuopan(azimuth: Float, modifier: Modifier) {
    val goldArgb = Gold.toArgb()
    val brightArgb = GoldBright.toArgb()
    val cinnabarArgb = Cinnabar.toArgb()
    val inkArgb = Ink.toArgb()

    Canvas(modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = min(cx, cy)

        val nc = drawContext.canvas.nativeCanvas

        rotate(degrees = -azimuth, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
            // === 外圈 360° 细刻度 ===
            val fineTick = android.graphics.Paint().apply {
                isAntiAlias = true
                color = goldArgb
                alpha = 70
                strokeWidth = 1f
            }
            for (i in 0 until 360) {
                val a = Math.toRadians(i.toDouble())
                val len = if (i % 5 == 0) 0.97f else 0.985f
                val sx = cx + r * len * sin(a).toFloat()
                val sy = cy - r * len * cos(a).toFloat()
                val ex = cx + r * 0.995f * sin(a).toFloat()
                val ey = cy - r * 0.995f * cos(a).toFloat()
                nc.drawLine(sx, sy, ex, ey, fineTick)
            }

            // === 二十四山外圈 ===
            val mtPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = brightArgb
                textSize = r * 0.065f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            MOUNTAINS_24.forEachIndexed { i, name ->
                val ang = i * 15f
                nc.save()
                nc.rotate(ang, cx, cy)
                mtPaint.color = if (i == 0) cinnabarArgb else brightArgb
                nc.drawText(name, cx, cy - r * 0.86f + mtPaint.textSize / 3, mtPaint)
                nc.restore()
            }

            // === 八卦中圈 ===
            val gPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = goldArgb
                textSize = r * 0.08f
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            BAGUA_8.forEach { (deg, name) ->
                nc.save()
                nc.rotate(deg.toFloat(), cx, cy)
                nc.drawText(name, cx, cy - r * 0.62f + gPaint.textSize / 3, gPaint)
                nc.restore()
            }

            // === 分金细线（简化） ===
            val fenjinPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = Gold.copy(alpha = 0.5f).toArgb()
                strokeWidth = 1.2f
            }
            for (base in 0 until 24) {
                for (off in listOf(0f, 5f, 10f)) {
                    val a = Math.toRadians(((base * 15) + off).toDouble())
                    val sx = cx + r * 0.72f * sin(a).toFloat()
                    val sy = cy - r * 0.72f * cos(a).toFloat()
                    val ex = cx + r * 0.82f * sin(a).toFloat()
                    val ey = cy - r * 0.82f * cos(a).toFloat()
                    nc.drawLine(sx, sy, ex, ey, fenjinPaint)
                }
            }

            // === 纳水弧线（简化示意） ===
            val nashuiPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = Color(0xFF4FC3F7).toArgb() // 青色代表水
                strokeWidth = 3f
                style = android.graphics.Paint.Style.STROKE
            }
            // 简单画两条弧表示来水去水
            nc.drawArc(cx - r*0.55f, cy - r*0.55f, cx + r*0.55f, cy + r*0.55f, 60f, 70f, false, nashuiPaint)
            nc.drawArc(cx - r*0.55f, cy - r*0.55f, cx + r*0.55f, cy + r*0.55f, 240f, 70f, false, nashuiPaint)

            // === 中心朱砂朝向针 ===
            val needlePaint = android.graphics.Paint().apply { isAntiAlias = true }
            val northPath = android.graphics.Path().apply {
                moveTo(cx, cy - r * 0.38f)
                lineTo(cx - r * 0.045f, cy)
                lineTo(cx + r * 0.045f, cy)
                close()
            }
            needlePaint.color = cinnabarArgb
            nc.drawPath(northPath, needlePaint)

            val southPath = android.graphics.Path().apply {
                moveTo(cx, cy + r * 0.38f)
                lineTo(cx - r * 0.045f, cy)
                lineTo(cx + r * 0.045f, cy)
                close()
            }
            needlePaint.color = goldArgb
            nc.drawPath(southPath, needlePaint)

            // 太极心
            needlePaint.color = inkArgb
            nc.drawCircle(cx, cy, r * 0.055f, needlePaint)
            needlePaint.style = android.graphics.Paint.Style.STROKE
            needlePaint.color = brightArgb
            needlePaint.strokeWidth = 2.5f
            nc.drawCircle(cx, cy, r * 0.055f, needlePaint)
        }

        // 固定顶端朝向三角
        val triPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = brightArgb
        }
        val tri = android.graphics.Path().apply {
            moveTo(cx, cy - r * 0.985f)
            lineTo(cx - r * 0.035f, cy - r * 0.90f)
            lineTo(cx + r * 0.035f, cy - r * 0.90f)
            close()
        }
        nc.drawPath(tri, triPaint)
    }
}
