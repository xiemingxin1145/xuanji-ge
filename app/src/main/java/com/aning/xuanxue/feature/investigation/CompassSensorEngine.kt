package com.aning.xuanxue.feature.investigation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt

/**
 * 风水罗盘 · 磁力计探测引擎
 *
 * 原理：地球地磁场基线约 25–65 μT。
 *   - 当手机靠近金属/电器/异常磁源时，读数会偏离基线。
 *   - 游戏化：用真实磁场扰动驱动"罗盘乱转"，把物理异常包装成"阴煞扰场"。
 *
 * 四级灵异强度（对应 GPT 设计的罗盘四态）：
 *   STABLE   指针稳定         偏差 < 8μT
 *   FLUTTER  指针抖动·阴气浮动  8–20μT
 *   DEVIATE  八卦盘偏转·卦象现  20–40μT
 *   REVERSED 罗盘反转·磁场紊乱  > 40μT  → 产出证据
 *
 * 注：真机上玩家把手机靠近门框铁件/电器即可触发高强度，
 *     恐怖游戏里这恰好制造"某个角落不对劲"的真实体感。
 */

enum class CompassAnomaly(
    val label: String,
    val tip: String,
    val bagua: String      // 偏转时显示的卦象
) {
    STABLE("罗盘稳定",      "此地气场平和，暂无异样。", ""),
    FLUTTER("阴气浮动",     "指针轻颤……附近似有东西。", "巽"),
    DEVIATE("卦象偏转",     "八卦盘自转，坎位大凶！",   "坎"),
    REVERSED("磁场紊乱",    "罗盘彻底反转——阴煞就在眼前！", "艮")
}

data class CompassReading(
    val microTesla: Float,          // 当前磁场强度
    val baseline: Float,            // 校准基线
    val deviation: Float,           // 偏差绝对值
    val heading: Float,             // 朝向角度(0-360)，乱场时叠加抖动
    val anomaly: CompassAnomaly,
    val producesEvidence: EvidenceType? = null
)

class CompassSensorEngine(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val magnetometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _reading = MutableStateFlow(
        CompassReading(50f, 50f, 0f, 0f, CompassAnomaly.STABLE)
    )
    val reading: StateFlow<CompassReading> = _reading

    val hasMagnetometer: Boolean get() = magnetometer != null

    // 校准基线：开局先采样 1.5 秒求平均，作为"此地正常磁场"
    private var baseline = 50f
    private var calibrating = true
    private val calibSamples = mutableListOf<Float>()
    private var calibStart = 0L

    // 用于算朝向的两个传感器原始值
    private val gravity = FloatArray(3)
    private val geomag = FloatArray(3)

    // 平滑滤波，避免读数太跳
    private var smoothMag = 50f

    fun start() {
        calibrating = true
        calibSamples.clear()
        calibStart = System.currentTimeMillis()
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER ->
                System.arraycopy(event.values, 0, gravity, 0, 3)

            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomag, 0, 3)
                val (mx, my, mz) = event.values
                val magnitude = sqrt(mx * mx + my * my + mz * mz)

                // 校准阶段：收集样本
                if (calibrating) {
                    calibSamples.add(magnitude)
                    if (System.currentTimeMillis() - calibStart > 1500 &&
                        calibSamples.size > 10
                    ) {
                        baseline = calibSamples.average().toFloat().coerceIn(20f, 70f)
                        calibrating = false
                    }
                    return
                }

                // 平滑
                smoothMag = smoothMag * 0.8f + magnitude * 0.2f
                val deviation = kotlin.math.abs(smoothMag - baseline)

                val anomaly = when {
                    deviation > 40f -> CompassAnomaly.REVERSED
                    deviation > 20f -> CompassAnomaly.DEVIATE
                    deviation > 8f  -> CompassAnomaly.FLUTTER
                    else            -> CompassAnomaly.STABLE
                }

                // 朝向计算
                val heading = computeHeading()
                // 异常时给朝向叠加随机抖动（罗盘"乱转"的视觉来源）
                val jitter = when (anomaly) {
                    CompassAnomaly.STABLE   -> 0f
                    CompassAnomaly.FLUTTER  -> (Math.random() * 20 - 10).toFloat()
                    CompassAnomaly.DEVIATE  -> (Math.random() * 90 - 45).toFloat()
                    CompassAnomaly.REVERSED -> (Math.random() * 360).toFloat()
                }

                // REVERSED 时产出证据
                val evidence = if (anomaly == CompassAnomaly.REVERSED) {
                    // 偏差极大→磁场紊乱；中高且方向反转→罗盘反转
                    if (deviation > 60f) EvidenceType.COMPASS_REVERSED
                    else EvidenceType.MAGNETIC_CHAOS
                } else null

                _reading.value = CompassReading(
                    microTesla = smoothMag,
                    baseline = baseline,
                    deviation = deviation,
                    heading = (heading + jitter + 360f) % 360f,
                    anomaly = anomaly,
                    producesEvidence = evidence
                )
            }
        }
    }

    private fun computeHeading(): Float {
        val rotation = FloatArray(9)
        val orientation = FloatArray(3)
        if (SensorManager.getRotationMatrix(rotation, null, gravity, geomag)) {
            SensorManager.getOrientation(rotation, orientation)
            return (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
        }
        return 0f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

/**
 * Compose 侧便捷封装：在进入捉鬼/案件界面时自动启停。
 * 用法：
 *   val reading by rememberCompassEngine()
 */
@Composable
fun rememberCompassEngine(active: Boolean = true): State<CompassReading> {
    val context = LocalContext.current
    val engine = remember { CompassSensorEngine(context) }

    DisposableEffect(active) {
        if (active) engine.start()
        onDispose { engine.stop() }
    }
    return engine.reading.collectAsState()
}
