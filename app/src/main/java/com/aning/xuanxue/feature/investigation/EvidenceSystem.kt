package com.aning.xuanxue.feature.investigation

import androidx.compose.ui.graphics.Color

/**
 * 玄机阁 · 证据推理系统
 *
 * 设计目标（参考 Phasmophobia 三证据模型，做中式化改造）：
 *   每个鬼种绑定恰好 3 条灵异证据。
 *   玩家不能直接捉鬼，必须先用法器（探测器）逐条采集证据，
 *   集齐证据→缩小鬼种范围→判断鬼种→选择对应克制法器处置。
 *   判断错误或法器不对 → 处置失败，鬼反扑。
 *
 * 可扩展性约定（应 GPT/阿宁要求，从一开始支持 25+ 鬼种无痛扩容）：
 *   1. 证据用 enum，新增证据只加枚举项，不动结构。
 *   2. 鬼种→证据 用独立映射表 GhostEvidenceTable，与 GhostData 解耦。
 *   3. 法器→可探测证据 用 ToolCapability 表，新增法器只加一行。
 *   4. 全部数据驱动，UI 不写死任何鬼种/证据。
 */

// ─────────────────────────────────────────────
// 1. 灵异证据（探测器能采集到的"线索"）
// ─────────────────────────────────────────────
enum class EvidenceType(
    val label: String,
    val icon: String,
    val hint: String,           // 采集到时给玩家看的描述
    val accentColor: Color
) {
    MAGNETIC_CHAOS(
        "磁场紊乱", "🧭",
        "罗盘指针疯狂打转，阴气扰乱了天地磁场。",
        Color(0xFF42A5F5)
    ),
    COMPASS_REVERSED(
        "罗盘反转", "🔄",
        "指南反指北——此地阴阳颠倒，必有水煞。",
        Color(0xFF26C6DA)
    ),
    COLD_BREATH(
        "阴风刺骨", "🌬",
        "温度骤降，呵气成霜，鬼魅藏身于此。",
        Color(0xFF80DEEA)
    ),
    WHISPER(
        "阴风低语", "👂",
        "风铃自响，隐约有人声在耳边低语。",
        Color(0xFFCE93D8)
    ),
    VOICE_MIMIC(
        "人声模仿", "🗣",
        "它在模仿活人的声音，意图引你上前。",
        Color(0xFFF06292)
    ),
    LOW_CRY(
        "低频哭声", "😢",
        "听风铃捕捉到极低频的啜泣，婴灵之征。",
        Color(0xFF9575CD)
    ),
    TALISMAN_BURN(
        "符纸自燃", "🔥",
        "朱砂符未近其身已自行燃烧，怨气极盛。",
        Color(0xFFFF7043)
    ),
    SEAL_BREAK(
        "符文断笔", "✒️",
        "落笔画符时笔锋自断——它在抗拒镇压。",
        Color(0xFFFFB74D)
    ),
    MIRROR_ANOMALY(
        "镜中异相", "🪞",
        "铜镜映出与现实不符的影像，画皮之类。",
        Color(0xFFFFD54F)
    ),
    WATER_SHADOW(
        "镜现水影", "💧",
        "干燥之地，镜中却映出水波——水鬼无疑。",
        Color(0xFF4FC3F7)
    ),
    FOOTSTEP_QUAKE(
        "脚步震动", "👣",
        "地面传来沉重而规律的震动，山魈行近。",
        Color(0xFF8D6E63)
    ),
    SHRILL_SCREAM(
        "怪声尖啸", "📢",
        "非人能及的尖啸刺破夜色，听之心悸。",
        Color(0xFFE57373)
    );
}

// ─────────────────────────────────────────────
// 2. 法器（探测器 + 处置器）
// ─────────────────────────────────────────────
enum class ToolKind { DETECTOR, SEALER }   // 探测 / 处置

enum class XuanTool(
    val label: String,
    val icon: String,
    val kind: ToolKind,
    val sensorHint: String,     // 调用哪个真实传感器（给开发用，也可展示）
    val detects: Set<EvidenceType> = emptySet(),  // DETECTOR 能采集的证据
    val intro: String
) {
    // ── 探测器 ──────────────────────
    LUOPAN(
        "风水罗盘", "🧭", ToolKind.DETECTOR,
        sensorHint = "磁力计 Magnetometer",
        detects = setOf(
            EvidenceType.MAGNETIC_CHAOS,
            EvidenceType.COMPASS_REVERSED
        ),
        intro = "二十四山向罗经。指针随真实磁场偏转，阴煞扰场时疯狂打转。"
    ),
    WIND_BELL(
        "听风铃", "🎐", ToolKind.DETECTOR,
        sensorHint = "麦克风 Microphone（分贝/低频检测）",
        detects = setOf(
            EvidenceType.WHISPER,
            EvidenceType.VOICE_MIMIC,
            EvidenceType.LOW_CRY,
            EvidenceType.SHRILL_SCREAM
        ),
        intro = "悬于檐角的铜铃。无风自响，能捕捉常人听不到的阴声。"
    ),
    BRONZE_MIRROR(
        "铜镜照妖", "🪞", ToolKind.DETECTOR,
        sensorHint = "摄像头 Camera（后续 AR）",
        detects = setOf(
            EvidenceType.MIRROR_ANOMALY,
            EvidenceType.WATER_SHADOW
        ),
        intro = "秦镜照胆。镜中之相与现实相悖，便是妖物现形之时。"
    ),
    THERMO_TALISMAN(
        "测温符", "🌡", ToolKind.DETECTOR,
        sensorHint = "（模拟）环境温度",
        detects = setOf(
            EvidenceType.COLD_BREATH,
            EvidenceType.TALISMAN_BURN
        ),
        intro = "贴于壁上的感应符。阴气重则结霜，怨气盛则自燃。"
    ),
    GEO_DRUM(
        "震地鼓", "🥁", ToolKind.DETECTOR,
        sensorHint = "加速度计 Accelerometer",
        detects = setOf(
            EvidenceType.FOOTSTEP_QUAKE
        ),
        intro = "伏地而听。重物行近的地脉震动，瞒不过这面小鼓。"
    ),
    CINNABAR_SEAL(
        "朱砂符笔", "🖌", ToolKind.DETECTOR,
        sensorHint = "触摸屏手势 Touch",
        detects = setOf(
            EvidenceType.SEAL_BREAK
        ),
        intro = "蘸朱砂试画符文。若笔锋自断，则鬼怨在抗拒镇压。"
    ),

    // ── 处置器（克制法器，捉鬼用）──────────────
    PEACH_SWORD(
        "桃木剑", "🗡", ToolKind.SEALER,
        sensorHint = "陀螺仪 Gyroscope（挥砍）",
        intro = "辟邪正器。挥剑斩煞，对怨气化形的厉鬼最有效。"
    ),
    RIVER_SEAL(
        "镇河符", "📜", ToolKind.SEALER,
        sensorHint = "触摸绘制",
        intro = "镇水安魂。专克溺亡水煞，封其于水脉不得作祟。"
    ),
    DEMON_MIRROR(
        "照妖镜", "🔆", ToolKind.SEALER,
        sensorHint = "对准目标",
        intro = "照破伪形。专破画皮、模仿之妖，令其无所遁形。"
    ),
    THUNDER_WOOD(
        "雷击木", "⚡", ToolKind.SEALER,
        sensorHint = "蓄力点击",
        intro = "天雷所殛之木，阳气最烈。镇山林精怪如山魈者。"
    ),
    SOUL_BELL(
        "安魂铃", "🔔", ToolKind.SEALER,
        sensorHint = "节奏摇动",
        intro = "安抚怨魂。专渡婴灵、冤魂，令其释执往生。"
    ),
    SOOTHE_TALISMAN(
        "朱砂安魂符", "🧧", ToolKind.SEALER,
        sensorHint = "触摸绘制",
        intro = "引渡无主游魂，温和超度，不伤其魂。"
    );

    companion object {
        val detectors get() = entries.filter { it.kind == ToolKind.DETECTOR }
        val sealers   get() = entries.filter { it.kind == ToolKind.SEALER }
    }
}

// ─────────────────────────────────────────────
// 3. 鬼种 → 证据 + 克制法器 映射表
//    （与 feature.ghost.GhostRegistry 的 id 对齐，解耦扩展）
// ─────────────────────────────────────────────
data class GhostCaseProfile(
    val ghostId: String,                 // 对应 GhostRegistry.byId
    val displayName: String,
    val evidences: Set<EvidenceType>,    // 恰好 3 条
    val weaknessTool: XuanTool,          // 唯一正确的处置法器
    val backlashDesc: String             // 判断错误时鬼反扑的描述
) {
    init {
        require(evidences.size == 3) {
            "鬼种 $ghostId 必须恰好绑定 3 条证据，当前 ${evidences.size} 条"
        }
        require(weaknessTool.kind == ToolKind.SEALER) {
            "$ghostId 的克制法器必须是处置型(SEALER)"
        }
    }
}

object GhostCaseTable {

    // MVP 第一版：5 种鬼（荒宅场景）
    // 后续扩容只需往这个 list 加条目，UI/逻辑零改动
    val profiles: List<GhostCaseProfile> = listOf(

        GhostCaseProfile(
            ghostId = "you_hun",
            displayName = "游魂",
            evidences = setOf(
                EvidenceType.COLD_BREATH,
                EvidenceType.WHISPER,
                EvidenceType.MAGNETIC_CHAOS
            ),
            weaknessTool = XuanTool.SOOTHE_TALISMAN,
            backlashDesc = "游魂受惊四散，怨气倒灌，你的鬼气被夺走一缕。"
        ),

        GhostCaseProfile(
            ghostId = "shui_gui",
            displayName = "水鬼",
            evidences = setOf(
                EvidenceType.COMPASS_REVERSED,
                EvidenceType.COLD_BREATH,
                EvidenceType.WATER_SHADOW
            ),
            weaknessTool = XuanTool.RIVER_SEAL,
            backlashDesc = "水鬼拖你入幻境，险些找了替身——道行受损。"
        ),

        GhostCaseProfile(
            ghostId = "yuan_hun",
            displayName = "冤魂",
            evidences = setOf(
                EvidenceType.TALISMAN_BURN,
                EvidenceType.WHISPER,
                EvidenceType.SEAL_BREAK
            ),
            weaknessTool = XuanTool.SOUL_BELL,
            backlashDesc = "冤气化火扑面，符纸尽燃，你被灼伤心神。"
        ),

        GhostCaseProfile(
            ghostId = "li_gui",
            displayName = "厉鬼",
            evidences = setOf(
                EvidenceType.MAGNETIC_CHAOS,
                EvidenceType.SHRILL_SCREAM,
                EvidenceType.TALISMAN_BURN
            ),
            weaknessTool = XuanTool.PEACH_SWORD,
            backlashDesc = "厉鬼狰狞反扑，险些破了你的护身罡气！"
        ),

        GhostCaseProfile(
            ghostId = "shan_mei",
            displayName = "山魅",
            evidences = setOf(
                EvidenceType.FOOTSTEP_QUAKE,
                EvidenceType.SHRILL_SCREAM,
                EvidenceType.MIRROR_ANOMALY
            ),
            weaknessTool = XuanTool.THUNDER_WOOD,
            backlashDesc = "山魅遁入林木，反手震得你气血翻涌。"
        )
    )

    fun byGhostId(id: String) = profiles.first { it.ghostId == id }

    /** 当前关卡可能出现的鬼种池（MVP=全部5种） */
    fun pool(): List<GhostCaseProfile> = profiles

    /**
     * 推理核心：给定已采集证据，返回仍然"可能"的鬼种。
     * 规则：候选鬼种的证据集合必须【包含】所有已采集证据。
     */
    fun narrowDown(
        collected: Set<EvidenceType>,
        pool: List<GhostCaseProfile> = profiles
    ): List<GhostCaseProfile> {
        if (collected.isEmpty()) return pool
        return pool.filter { it.evidences.containsAll(collected) }
    }
}
