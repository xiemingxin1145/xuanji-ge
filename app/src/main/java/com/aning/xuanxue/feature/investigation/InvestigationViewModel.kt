package com.aning.xuanxue.feature.investigation

import androidx.lifecycle.ViewModel
import com.aning.xuanxue.core.sound.ProceduralSound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 调查局 · 一局完整捉鬼流程的状态机
 *
 * 游戏循环（GPT 拍板的核心）：
 *   开局随机鬼种 → 玩家用探测器采集证据 → 证据缩小鬼种范围
 *   → 判断鬼种 → 选对应克制法器处置 → 成败结算
 *
 * 判断错误 / 法器不对 → backlash 反扑，本局失败。
 */

enum class InvestigationPhase {
    BRIEFING,     // 案情简报
    INVESTIGATE,  // 取证中
    DEDUCE,       // 下判断（选鬼种）
    SEAL,         // 选法器处置
    RESULT        // 结算
}

data class InvestigationState(
    val phase: InvestigationPhase = InvestigationPhase.BRIEFING,
    val sceneName: String = "城郊荒宅",
    val collectedEvidence: Set<EvidenceType> = emptySet(),
    val suspects: List<GhostCaseProfile> = GhostCaseTable.pool(),
    val accusedGhostId: String? = null,    // 玩家判断的鬼种
    val usedTool: XuanTool? = null,
    val success: Boolean? = null,
    val resultMessage: String = "",
    val essenceGained: Int = 0,
    val xpGained: Int = 0
)

class InvestigationViewModel : ViewModel() {

    // 本局真正的鬼（隐藏，结算才揭晓）
    private var trueGhost: GhostCaseProfile = GhostCaseTable.pool().random()

    private val _state = MutableStateFlow(
        InvestigationState(sceneName = "城郊荒宅")
    )
    val state: StateFlow<InvestigationState> = _state.asStateFlow()

    /** 开新局，重置 */
    fun newCase(scene: String = "城郊荒宅") {
        trueGhost = GhostCaseTable.pool().random()
        _state.value = InvestigationState(sceneName = scene)
    }

    fun startInvestigation() {
        _state.value = _state.value.copy(phase = InvestigationPhase.INVESTIGATE)
        ProceduralSound.play(ProceduralSound.Sfx.QING_BELL)
    }

    /**
     * 用探测器采集证据。
     * 只有当【该探测器能探到的证据】∩【本局真鬼的证据】非空时，才采集成功。
     * @return 采集到的证据，null 表示此法器在此处探不到东西
     */
    fun detectWith(tool: XuanTool): EvidenceType? {
        if (tool.kind != ToolKind.DETECTOR) return null
        val s = _state.value
        if (s.phase != InvestigationPhase.INVESTIGATE) return null

        // 该法器能探到的、且属于真鬼的、且还没采集过的证据
        val findable = tool.detects
            .intersect(trueGhost.evidences)
            .minus(s.collectedEvidence)

        if (findable.isEmpty()) {
            // 探不到：可能是法器不对，或这条证据真鬼没有
            playToolSound(tool, hit = false)
            return null
        }

        val found = findable.first()
        val newCollected = s.collectedEvidence + found
        val newSuspects = GhostCaseTable.narrowDown(newCollected)

        playToolSound(tool, hit = true)

        _state.value = s.copy(
            collectedEvidence = newCollected,
            suspects = newSuspects,
            // 集齐 3 条 → 可进入判断阶段
            phase = if (newCollected.size >= 3)
                InvestigationPhase.DEDUCE else InvestigationPhase.INVESTIGATE
        )
        return found
    }

    /** 玩家可手动提前进入判断（哪怕证据不足，风险自负） */
    fun forceDeduce() {
        _state.value = _state.value.copy(phase = InvestigationPhase.DEDUCE)
    }

    /** 玩家下判断，指认鬼种 */
    fun accuse(ghostId: String) {
        _state.value = _state.value.copy(
            accusedGhostId = ghostId,
            phase = InvestigationPhase.SEAL
        )
    }

    /** 选法器处置 */
    fun sealWith(tool: XuanTool) {
        val s = _state.value
        if (tool.kind != ToolKind.SEALER) return

        val accusedRight = s.accusedGhostId == trueGhost.ghostId
        val toolRight = tool == trueGhost.weaknessTool
        val win = accusedRight && toolRight

        if (win) {
            ProceduralSound.play(ProceduralSound.Sfx.TALISMAN)
            _state.value = s.copy(
                phase = InvestigationPhase.RESULT,
                usedTool = tool,
                success = true,
                resultMessage = "镇压成功！【${trueGhost.displayName}】已被${tool.label}封印。" +
                    "你的判断与法器皆准，不愧是玄机阁的道士。",
                essenceGained = 60 + trueGhost.evidences.size * 20,
                xpGained = 100
            )
        } else {
            ProceduralSound.play(ProceduralSound.Sfx.HEARTBEAT)
            val reason = when {
                !accusedRight -> "你认错了鬼——它根本不是【${suspectName(s.accusedGhostId)}】。"
                else -> "鬼种判断对了，但${tool.label}克它不得。"
            }
            _state.value = s.copy(
                phase = InvestigationPhase.RESULT,
                usedTool = tool,
                success = false,
                resultMessage = "$reason ${trueGhost.backlashDesc} " +
                    "（真正的元凶是【${trueGhost.displayName}】，克星为${trueGhost.weaknessTool.label}）",
                essenceGained = 0,
                xpGained = 15   // 失败也给少量经验，不劝退
            )
        }
    }

    private fun suspectName(id: String?): String =
        GhostCaseTable.pool().firstOrNull { it.ghostId == id }?.displayName ?: "？"

    private fun playToolSound(tool: XuanTool, hit: Boolean) {
        when (tool) {
            XuanTool.WIND_BELL    -> ProceduralSound.play(ProceduralSound.Sfx.WIND_CHIME)
            XuanTool.LUOPAN       -> if (hit) ProceduralSound.play(ProceduralSound.Sfx.COLD_WIND)
            XuanTool.THERMO_TALISMAN -> if (hit) ProceduralSound.play(ProceduralSound.Sfx.TALISMAN, 0.4f)
            else -> ProceduralSound.play(ProceduralSound.Sfx.DIVINE, 0.5f)
        }
    }

    // 给 UI 调试用：暴露当前真鬼（仅 BuildConfig.DEBUG 时该在 UI 隐藏）
    fun debugTrueGhost() = trueGhost.displayName
}
