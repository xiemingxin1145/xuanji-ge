package com.aning.xuanxue.feature.investigation

import kotlin.random.Random

/**
 * Investigation runtime state.
 *
 * This file is intentionally UI-free. Compose screens, AR overlays, sensor callbacks,
 * or story cases can all call this engine without duplicating deduction rules.
 */
data class InvestigationSession(
    val target: GhostCaseProfile,
    val collected: Set<EvidenceType> = emptySet(),
    val attempts: Int = 0,
    val sealed: Boolean = false,
    val failed: Boolean = false
) {
    val candidates: List<GhostCaseProfile>
        get() = GhostCaseTable.narrowDown(collected)

    val missing: Set<EvidenceType>
        get() = target.evidences - collected

    val progressText: String
        get() = when (collected.size) {
            0 -> "未取证"
            1 -> "一证初明"
            2 -> "二证锁疑"
            else -> if (candidates.size == 1) "三证定型" else "证据冲突"
        }

    val canIdentify: Boolean
        get() = collected.size >= 3 && candidates.size == 1
}

sealed class InvestigationStepResult {
    data class Collected(
        val session: InvestigationSession,
        val evidence: EvidenceType,
        val tool: XuanTool
    ) : InvestigationStepResult()

    data class NoSignal(
        val session: InvestigationSession,
        val tool: XuanTool
    ) : InvestigationStepResult()
}

sealed class SealAttemptResult {
    data class Success(val session: InvestigationSession) : SealAttemptResult()
    data class WrongTool(val session: InvestigationSession, val correctTool: XuanTool) : SealAttemptResult()
    data class NotEnoughEvidence(val session: InvestigationSession) : SealAttemptResult()
}

object InvestigationSessionEngine {
    fun startRandom(pool: List<GhostCaseProfile> = GhostCaseTable.pool()): InvestigationSession {
        return InvestigationSession(target = pool.random())
    }

    fun startForGhostId(ghostId: String): InvestigationSession {
        return InvestigationSession(target = GhostCaseTable.byGhostId(ghostId))
    }

    fun scan(session: InvestigationSession, tool: XuanTool, random: Random = Random): InvestigationStepResult {
        if (tool.kind != ToolKind.DETECTOR) {
            return InvestigationStepResult.NoSignal(session.copy(attempts = session.attempts + 1), tool)
        }

        val detectable = session.missing.intersect(tool.detects)
        if (detectable.isEmpty()) {
            return InvestigationStepResult.NoSignal(session.copy(attempts = session.attempts + 1), tool)
        }

        val evidence = detectable.elementAt(random.nextInt(detectable.size))
        val next = session.copy(
            collected = session.collected + evidence,
            attempts = session.attempts + 1
        )
        return InvestigationStepResult.Collected(next, evidence, tool)
    }

    fun trySeal(session: InvestigationSession, tool: XuanTool): SealAttemptResult {
        if (!session.canIdentify) {
            return SealAttemptResult.NotEnoughEvidence(session.copy(attempts = session.attempts + 1))
        }

        return if (tool == session.target.weaknessTool) {
            SealAttemptResult.Success(session.copy(sealed = true, attempts = session.attempts + 1))
        } else {
            SealAttemptResult.WrongTool(
                session.copy(failed = true, attempts = session.attempts + 1),
                session.target.weaknessTool
            )
        }
    }
}
