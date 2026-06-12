package com.aning.xuanxue.core.xuanji

/**
 * 玄机阁 · 仙神话界
 * 核心神通系统：天时·地利·人和「玄机共鸣」引擎
 *
 * 零自主实现（全权授权后立即推进）
 * 设计原则：
 * - 纯逻辑优先，可被任何feature模块调用
 * - 提供数值 + 神话风味描述
 * - 后期可轻松接入真实黄历/罗盘/八字数据
 * - 支持Compose预览和游戏化反馈
 */

object XuanjiResonance {

    /** 共鸣强度等级 */
    enum class Level(val displayName: String, val colorHint: String, val multiplier: Float) {
        NONE("无共鸣", "#888888", 1.0f),
        WEAK("微弱共鸣", "#A8D5BA", 1.2f),
        MODERATE("中度共鸣", "#F4D35E", 1.5f),
        STRONG("强烈共鸣", "#F77F00", 2.0f),
        EPIC("玄机爆发", "#D62828", 3.5f)   // 传说级，稀有
    }

    /** 共鸣计算结果 */
    data class Result(
        val tianShi: Int,      // 天时分数 0-100（来自黄历吉凶）
        val diLi: Int,         // 地利分数 0-100（来自罗盘方位/地形匹配）
        val renHe: Int,        // 人和分数 0-100（来自八字羁绊 + 姓名五行相生）
        val total: Int,        // 综合分数
        val level: Level,
        val multiplier: Float,
        val mythicDesc: String, // 神话风味描述（可直接用于UI）
        val gameEffect: String  // 游戏效果提示（buff类型）
    )

    /**
     * 计算玄机共鸣
     * @param tianShiRaw 黄历原始吉凶值（可后期映射）
     * @param diLiRaw 罗盘匹配度（0-100）
     * @param renHeRaw 八字/姓名相生度（0-100）
     * @param context 上下文（可选，用于随机种子或玩家状态）
     */
    fun calculate(
        tianShiRaw: Int = 50,
        diLiRaw: Int = 50,
        renHeRaw: Int = 50,
        context: Any? = null
    ): Result {
        // 基础归一化（防止异常值）
        val tian = tianShiRaw.coerceIn(0, 100)
        val di = diLiRaw.coerceIn(0, 100)
        val ren = renHeRaw.coerceIn(0, 100)

        // 加权综合（天时略重，符合「谋事在人，成事在天」）
        val rawTotal = (tian * 0.40 + di * 0.30 + ren * 0.30).toInt()
        val total = rawTotal.coerceIn(0, 100)

        // 等级判定（带一点随机波动，增加玄幻感）
        val level = when {
            total >= 92 -> Level.EPIC
            total >= 78 -> Level.STRONG
            total >= 60 -> Level.MODERATE
            total >= 35 -> Level.WEAK
            else -> Level.NONE
        }

        val multiplier = level.multiplier

        // 神话风味描述（零手写，可后期接入AI生成或配置表）
        val mythicDesc = when (level) {
            Level.EPIC -> "天雷地火齐鸣，三界玄机共振！上古神祇仿佛在注视此间。"
            Level.STRONG -> "风起云涌，龙吟凤啸，地利天时已成，此刻出手必有神助。"
            Level.MODERATE -> "气运流转，阴阳和合，虽未至巅峰，却已隐隐有仙缘加持。"
            Level.WEAK -> "玄机微动，如晨曦初现，尚需更多机缘方能引动大势。"
            Level.NONE -> "天时不济，地利难寻，人和未至，暂且按兵不动为上策。"
        }

        // 游戏效果提示（后期可扩展为真实buff）
        val gameEffect = when (level) {
            Level.EPIC -> "全属性+150% · 暴击率+40% · 特殊事件触发率大幅提升"
            Level.STRONG -> "全属性+80% · 成功率+25% · 可能触发隐藏剧情"
            Level.MODERATE -> "全属性+35% · 成功率+12%"
            Level.WEAK -> "全属性+10% · 小幅好运加成"
            Level.NONE -> "无加成 · 建议等待更好时机或调整策略"
        }

        return Result(
            tianShi = tian,
            diLi = di,
            renHe = ren,
            total = total,
            level = level,
            multiplier = multiplier,
            mythicDesc = mythicDesc,
            gameEffect = gameEffect
        )
    }

    /**
     * 便捷方法：从黄历宜忌数量快速估算天时
     * （零先实现简单版，后期可替换为更精确的映射）
     */
    fun estimateTianShiFromAlmanac(yiCount: Int, jiCount: Int): Int {
        val base = 50
        val yiBonus = (yiCount * 4).coerceAtMost(35)
        val jiPenalty = (jiCount * 3).coerceAtMost(30)
        return (base + yiBonus - jiPenalty).coerceIn(10, 95)
    }

    /**
     * 便捷方法：从罗盘当前指向与目标方位的夹角计算地利
     * angleDiff: 0~180度
     */
    fun calculateDiLiFromCompass(angleDiff: Float, isAuspiciousDirection: Boolean): Int {
        val base = if (isAuspiciousDirection) 65 else 35
        val anglePenalty = (angleDiff / 180f * 40).toInt()
        return (base - anglePenalty).coerceIn(5, 95)
    }
}

// 使用示例（可删除或移到测试）：
// val result = XuanjiResonance.calculate(tianShiRaw = 85, diLiRaw = 72, renHeRaw = 68)
// println(result.mythicDesc)