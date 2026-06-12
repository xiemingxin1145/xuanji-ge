package com.aning.xuanxue.feature.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/** 罗盘实时状态：朝向角、真实磁场强度(μT)、磁力计校准精度 */
private data class CompassState(
    val azimuth: Float = 0f,
    val magneticUt: Float = 0f,
    val accuracy: Int = 0
)

private enum class CompassSheet { Fenjin, Nashui, Ai }

@Composable
private fun rememberCompassState(): State<CompassState> {
    val context = LocalContext.current
    val data = remember { mutableStateOf(CompassState()) }
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val rot = FloatArray(9)
        val orient = FloatArray(3)
        var cur = 0f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                when (e.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rot, e.values)
                        SensorManager.getOrientation(rot, orient)
                        var deg = Math.toDegrees(orient[0].toDouble()).toFloat()
                        deg = (deg + 360f) % 360f
                        var diff = deg - cur
                        if (diff > 180) diff -= 360
                        if (diff < -180) diff += 360
                        cur = (cur + diff * 0.12f + 360f) % 360f
                        data.value = data.value.copy(azimuth = cur)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        val ut = CompassUtils.magneticMagnitude(e.values[0], e.values[1], e.values[2])
                        data.value = data.value.copy(magneticUt = ut, accuracy = e.accuracy)
                    }
                }
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {
                if (s?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    data.value = data.value.copy(accuracy = a)
                }
            }
        }
        if (rotationSensor != null) sm.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        if (magSensor != null) sm.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(listener) }
    }
    return data
}

@Composable
fun CompassScreen(onBack: () -> Unit) {
    val state by rememberCompassState()
    val azimuth = state.azimuth
    val degreeInt = azimuth.roundToInt()
    val mountain = CompassUtils.degreeToMountain24(azimuth)
    val bagua = CompassUtils.degreeToBagua(azimuth)
    val wuxing = CompassUtils.degreeToFiveElement(azimuth)
    val fenjin = CompassUtils.degreeToFenjin(azimuth)
    val magUt = state.magneticUt
    val fieldStatus = CompassUtils.magneticFieldStatus(magUt)
    val accText = CompassUtils.accuracyText(state.accuracy)
    val statusColor = if (fieldStatus == "正常") Jade else Cinnabar

    var activeSheet by remember { mutableStateOf<CompassSheet?>(null) }

    XScaffold(title = "风水罗盘 · 玄门法器", onBack = onBack) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部信息
            Text("$degreeInt°", color = GoldBright, fontSize = 42.sp, fontWeight = FontWeight.Bold)
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

            // 磁场状态卡（真实 μT）
            XCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("磁场状态", color = GoldBright, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text(fieldStatus, color = statusColor, fontSize = 13.sp)
                }
                Spacer(Modifier.height(8.dp))
                KV("磁场强度", "${CompassUtils.formatUt(magUt)} μT")
                KV("罗盘精度", accText)
                KV("磁场状态", fieldStatus, valueColor = statusColor)
                Spacer(Modifier.height(4.dp))
                Text(CompassUtils.magneticAdvice(magUt), color = TextSub, fontSize = 12.sp)
                Spacer(Modifier.height(2.dp))
                Text("※ 传统文化娱乐参考，不代表灵异、驱邪或转运效果。", color = TextSub, fontSize = 11.sp)
            }

            Spacer(Modifier.height(20.dp))

            // 三个入口按钮
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { activeSheet = CompassSheet.Fenjin },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Gold)
                ) { Text("分金", color = Gold) }

                OutlinedButton(
                    onClick = { activeSheet = CompassSheet.Nashui },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Gold)
                ) { Text("纳水", color = Gold) }

                Button(
                    onClick = { activeSheet = CompassSheet.Ai },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                ) { Text("AI 解读") }
            }
        }
    }

    when (activeSheet) {
        CompassSheet.Fenjin -> FenjinSheet(
            degree = degreeInt, mountain = mountain, fenjin = fenjin, wuxing = wuxing
        ) { activeSheet = null }
        CompassSheet.Nashui -> NashuiSheet { activeSheet = null }
        CompassSheet.Ai -> AiPromptSheet(
            prompt = CompassUtils.buildCompassPrompt(
                degree = degreeInt,
                mountain = mountain,
                bagua = bagua,
                fiveElement = wuxing,
                fenjin = fenjin,
                magneticUt = magUt,
                accuracyText = accText
            )
        ) { activeSheet = null }
        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FenjinSheet(
    degree: Int,
    mountain: String,
    fenjin: String,
    wuxing: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = InkSurface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 16.dp)
        ) {
            SectionTitle("分金 · 当前坐向")
            Spacer(Modifier.height(12.dp))
            KV("角度", "$degree°")
            KV("坐山", "$mountain 山")
            KV("分金", fenjin)
            KV("五行", wuxing)
            Spacer(Modifier.height(10.dp))
            Text(
                "分金是二十四山中更细的格位，用于微调立向。此处为简化示意，仅作传统文化参考。",
                color = TextSub, fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NashuiSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = InkSurface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 16.dp)
        ) {
            SectionTitle("纳水 · 说明")
            Spacer(Modifier.height(12.dp))
            Text(
                "当前罗盘上的纳水弧线只是示意，用于标注大致的来水 / 去水方位。",
                color = TextMain, fontSize = 14.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "后续版本将支持手动记录：来水方位、去水方位、道路走向、水口位置，并结合坐向给出参考。",
                color = TextSub, fontSize = 13.sp
            )
            Spacer(Modifier.height(10.dp))
            Text("※ 传统文化娱乐参考，请勿据此做现实决策。", color = TextSub, fontSize = 11.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiPromptSheet(
    prompt: CompassUtils.CompassAiPrompt,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = InkSurface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 16.dp)
        ) {
            SectionTitle("AI 解读 · 上下文已生成")
            Spacer(Modifier.height(8.dp))
            Text("已生成罗盘解读上下文，后续接入 AI 玄师。", color = Jade, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            XCard(Modifier.fillMaxWidth()) {
                Text(prompt.toPromptText(), color = TextMain, fontSize = 12.sp)
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
                color = Color(0xFF4FC3F7).toArgb()
                strokeWidth = 3f
                style = android.graphics.Paint.Style.STROKE
            }
            nc.drawArc(cx - r * 0.55f, cy - r * 0.55f, cx + r * 0.55f, cy + r * 0.55f, 60f, 70f, false, nashuiPaint)
            nc.drawArc(cx - r * 0.55f, cy - r * 0.55f, cx + r * 0.55f, cy + r * 0.55f, 240f, 70f, false, nashuiPaint)

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
