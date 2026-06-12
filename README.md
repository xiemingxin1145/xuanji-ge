# 玄机阁 · 仙神话界

**玄幻 · 神话 · 天时地利战略 · 多功能游戏元素** 的沉浸式神话APP

> 零已全面接管改造（2026-06-12）。本项目正从传统玄学工具App，升级为上古神话世界中的「玄机阁主传人」战略养成游戏。

## 核心愿景

玩家觉醒成为玄机阁新任传人，通过推演上古神通（罗盘、八字、易经、黄历、姓名五行、玄师）影响三界命运。

**核心机制**：**天时·地利·人和「玄机共鸣」系统**
- 天时（黄历吉凶）+ 地利（罗盘方位/地形匹配）+ 人和（八字/姓名）叠加触发共鸣爆发
- 用于修炼突破、战斗结算、剧情决策、秘境探索

**双模式**：
- **凡尘模式**：专业玄学工具（保留原有所有算法与功能）
- **幻界沉浸模式**：全屏玄幻UI + 粒子特效 + 神话音效 + 游戏化反馈 + 剧情推进

## 当前状态（零接管后）

- 基础代码已非常扎实（Kotlin + Compose + feature模块 + CI自动打包APK）
- 已包含大量功能入口（含「玄奇志」神怪玄幻内容）
- 零正在主导：
  - Phase 0 文档与品牌重构（进行中）
  - Phase 1 核心玄机共鸣引擎开发（即将开始）
  - 后续逐个模块游戏化

详细规划见仓库内 **XUANHUAN_TRANSFORMATION_PLAN.md**

---

## 原有传统功能说明（凡尘模式保留）

一个安装包，多个模块：

| 模块 | 功能 |
|------|------|
| 风水罗盘 | 磁力计驱动，二十四山 + 后天八卦 + 红针指向 |
| 八字排盘 | 四柱、藏干、十神、纳音、旬空、五行统计 |
| 易经起卦 | 三钱六摇，六十四卦，本卦 / 变卦 / 动爻 |
| 老黄历 | 宜忌、冲煞、值神黄道、彭祖百忌、吉神方位 |
| 姓名五行 | 八字五行缺补分析 + 五行起名用字库 |
| AI 玄师 | 可插拔大模型（任意 OpenAI 兼容接口），解卦/解梦/今日运势 |

历法 / 八字 / 黄历算法基于 `cn.6tail:lunar`（MIT，无第三方依赖），保证农历与干支准确。

**AI 玄师为纯客户端直连**：用户在 APP 内填自己的 API Key / Base URL / 模型名（预设 DeepSeek、Kimi、通义、OpenRouter、OpenAI），Key 经 Android Keystore 加密存本机，请求直达用户填写的接口，无任何中间服务器。提示词内置合规边界（仅文化参考娱乐，不构成医疗/法律/投资建议）。

---

## 怎么拿到能装的 APK（手机即可操作）

1. Fork 或直接在 GitHub 新建仓库（Public 即可）
2. 把本项目所有文件上传进去（保持目录结构）
3. 推上去后，进仓库的 **Actions** 标签页，会看到「Build APK」自动开始跑
4. 跑完（绿勾）后点进那次运行，最下面 **Artifacts** 里下载 `app-debug.apk`
5. 解压得到 `app-debug.apk`，手机安装时允许「未知来源」即可

> 没自动触发？进 Actions → 左侧选「Build APK」→ 右上 **Run workflow** 手动跑一次。

## 构建说明（CI 自动完成，无需本地）

- JDK 17 + Android SDK（runner 自带）
- Gradle 8.7（workflow 内 `gradle wrapper` 现场生成）
- AGP 8.5.2 / Kotlin 2.0.21 / Compose BOM 2024.09.02
- 产物：`app/build/outputs/apk/debug/app-debug.apk`（debug 签名）

## 目录结构（当前）

```
.
├── XUANHUAN_TRANSFORMATION_PLAN.md   ← 零新增的改造总规划
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── .github/workflows/build.yml
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/values/{strings,themes}.xml
        └── java/com/aning/xuanxue/
            ├── MainActivity.kt / AppNav.kt
            ├── ui/{Theme,Common}.kt
            └── feature/{compass,bazi,iching,almanac,name,ai,...}
```

## 说明

玄学内容仅供参考娱乐。改造后将增加完整神话世界观与游戏化战略层。

**零当前状态**：已创建改造规划文档，正在准备Phase 0品牌更新与Phase 1核心引擎。

主人，零已全面接管。随时等候您的下一步指示。