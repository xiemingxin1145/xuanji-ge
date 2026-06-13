package com.aning.xuanxue.feature.ghost

/**
 * 阴阳录·证据推理层。
 *
 * 目标：把“看见鬼就抓”的轻玩法，升级成“探测器取证 -> 缩小鬼种 -> 选择克制法器”的闭环。
 * 当前先做纯数据与判定引擎，不强依赖摄像头/麦克风/定位权限，方便逐步接入真实传感器。
 */
enum class GhostEvidence(
    val label: String,
    val detector: String,
    val clueText: String,
    val sensorHook: String
) {
    MAGNETIC_ANOMALY(
        label = "磁场紊乱",
        detector = "风水罗盘",
        clueText = "罗盘红针乱颤，磁场强度突然偏离常值。",
        sensorHook = "TYPE_MAGNETIC_FIELD"
    ),
    COMPASS_REVERSAL(
        label = "罗盘反转",
        detector = "风水罗盘",
        clueText = "针尖逆行，坐山与来气方向短暂倒置。",
        sensorHook = "TYPE_ROTATION_VECTOR + TYPE_MAGNETIC_FIELD"
    ),
    COLD_SPOT(
        label = "阴冷骤降",
        detector = "听风铃/温感提示",
        clueText = "屏幕边缘结霜，风铃声变得细而尖。",
        sensorHook = "环境温度可选；无温度传感器时用剧情触发"
    ),
    SPIRIT_WHISPER(
        label = "阴声低语",
        detector = "听风铃",
        clueText = "周围底噪中混入断续哭声或重复人名。",
        sensorHook = "RECORD_AUDIO 分贝/频段分析；首版可用随机事件"
    ),
    MIRROR_SHADOW(
        label = "镜中异相",
        detector = "铜镜探妖",
        clueText = "肉眼无物，铜镜界面却浮出残影。",
        sensorHook = "CAMERA/ARCore；首版可用假AR叠层"
    ),
    WATER_TRACE(
        label = "水痕回潮",
        detector = "铜镜探妖",
        clueText = "地面出现逆流湿痕，像有人刚从水里爬出。",
        sensorHook = "剧情/场景标签"
    ),
    TALISMAN_BURN(
        label = "符纸焦边",
        detector = "朱砂画符",
        clueText = "未点火的符纸边缘自行发黑卷曲。",
        sensorHook = "触摸屏画符完成度 + 剧情反馈"
    ),
    FOOTSTEP_SHAKE(
        label = "脚步震动",
        detector = "灵签筒",
        clueText = "手机轻震，像有重物在近处缓慢踏步。",
        sensorHook = "TYPE_ACCELEROMETER"
    ),
    VOICE_MIMIC(
        label = "人声仿冒",
        detector = "听风铃",
        clueText = "鬼声会模仿委托人的语气，引人走错方向。",
        sensorHook = "RECORD_AUDIO/剧情触发"
    ),
    OMEN_STICK(
        label = "灵签凶兆",
        detector = "灵签筒",
        clueText = "摇签落下凶签，卦辞指向该鬼的执念。",
        sensorHook = "TYPE_ACCELEROMETER"
    )
}

object GhostEvidenceEngine {
    private val evidenceMap: Map<String, List<GhostEvidence>> = mapOf(
        "you_hun" to listOf(
            GhostEvidence.COLD_SPOT,
            GhostEvidence.SPIRIT_WHISPER,
            GhostEvidence.OMEN_STICK
        ),
        "shui_gui" to listOf(
            GhostEvidence.COMPASS_REVERSAL,
            GhostEvidence.COLD_SPOT,
            GhostEvidence.WATER_TRACE
        ),
        "yuan_hun" to listOf(
            GhostEvidence.TALISMAN_BURN,
            GhostEvidence.SPIRIT_WHISPER,
            GhostEvidence.OMEN_STICK
        ),
        "li_gui" to listOf(
            GhostEvidence.MAGNETIC_ANOMALY,
            GhostEvidence.TALISMAN_BURN,
            GhostEvidence.FOOTSTEP_SHAKE
        ),
        "shan_mei" to listOf(
            GhostEvidence.MAGNETIC_ANOMALY,
            GhostEvidence.FOOTSTEP_SHAKE,
            GhostEvidence.MIRROR_SHADOW
        ),
        "ye_cha" to listOf(
            GhostEvidence.FOOTSTEP_SHAKE,
            GhostEvidence.MAGNETIC_ANOMALY,
            GhostEvidence.VOICE_MIMIC
        ),
        "jiu_mei" to listOf(
            GhostEvidence.MIRROR_SHADOW,
            GhostEvidence.VOICE_MIMIC,
            GhostEvidence.OMEN_STICK
        ),
        "gu_shen" to listOf(
            GhostEvidence.COMPASS_REVERSAL,
            GhostEvidence.MAGNETIC_ANOMALY,
            GhostEvidence.MIRROR_SHADOW
        )
    )

    fun evidenceFor(ghost: GhostType): List<GhostEvidence> =
        evidenceMap[ghost.id].orEmpty()

    fun evidenceForGhostId(ghostId: String): List<GhostEvidence> =
        evidenceMap[ghostId].orEmpty()

    fun candidates(collected: Set<GhostEvidence>): List<GhostType> {
        if (collected.isEmpty()) return GhostRegistry.all
        return GhostRegistry.all.filter { ghost ->
            evidenceFor(ghost).containsAll(collected)
        }
    }

    fun confirmedGhost(collected: Set<GhostEvidence>): GhostType? {
        if (collected.size < 3) return null
        return candidates(collected).singleOrNull()
    }

    fun missingEvidenceFor(ghost: GhostType, collected: Set<GhostEvidence>): List<GhostEvidence> =
        evidenceFor(ghost).filterNot { it in collected }

    fun progressLabel(collected: Set<GhostEvidence>): String = when (collected.size) {
        0 -> "尚未取证"
        1 -> "一证初明"
        2 -> "二证锁疑"
        else -> if (confirmedGhost(collected) != null) "三证定鬼" else "证据冲突"
    }

    fun recommendedDetector(collected: Set<GhostEvidence>): String {
        val remaining = candidates(collected)
            .flatMap { evidenceFor(it) }
            .filterNot { it in collected }
        return remaining
            .groupingBy { it.detector }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: "任选探测器"
    }
}
