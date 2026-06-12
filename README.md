# 玄机阁 · 玄学工具 Android App

一个安装包，五个模块：

| 模块 | 功能 |
|------|------|
| 风水罗盘 | 磁力计驱动，二十四山 + 后天八卦 + 红针指向 |
| 八字排盘 | 四柱、藏干、十神、纳音、旬空、五行统计 |
| 易经起卦 | 三钱六摇，六十四卦，本卦 / 变卦 / 动爻 |
| 老黄历 | 宜忌、冲煞、值神黄道、彭祖百忌、吉神方位 |
| 姓名五行 | 八字五行缺补分析 + 五行起名用字库 |
| AI 玄师 | 可插拔大模型（任意 OpenAI 兼容接口），解卦/解梦/今日运势 |

历法 / 八字 / 黄历算法基于 `cn.6tail:lunar`（MIT，无第三方依赖），保证农历与干支准确。

**AI 玄师为纯客户端直连**：用户在 APP 内填自己的 API Key / Base URL / 模型名（预设 DeepSeek、Kimi、通义、OpenRouter、OpenAI），Key 经 Android Keystore 加密存本机，请求直达用户填写的接口，无任何中间服务器。提示词内置合规边界（仅文化参考娱乐，不构成医疗/法律/投资建议，禁止效果性承诺与恐吓话术）。

---

## 一、怎么拿到能装的 APK（手机即可操作）

1. 在 GitHub 新建一个仓库（Public 即可），名字随意，比如 `xuanxue`。
2. 把本项目所有文件上传进去（保持目录结构）。**默认分支要是 `main` 或 `master`**。
3. 推上去后，进仓库的 **Actions** 标签页，会看到「Build APK」自动开始跑。
4. 跑完（绿勾）后点进那次运行，最下面 **Artifacts** 里下载 `xuanxue-debug-apk`。
5. 解压得到 `app-debug.apk`，手机安装时允许「未知来源」即可。

> 没自动触发？进 Actions → 左侧选「Build APK」→ 右上 **Run workflow** 手动跑一次。

## 二、构建说明（CI 自动完成，无需本地）

- JDK 17 + Android SDK（runner 自带）
- Gradle 8.7（workflow 内 `gradle wrapper` 现场生成，**不依赖仓库里的 wrapper.jar**，避免 wrapper 损坏）
- AGP 8.5.2 / Kotlin 2.0.21 / Compose BOM 2024.09.02
- 产物：`app/build/outputs/apk/debug/app-debug.apk`（debug 签名，免 keystore）

## 三、目录结构

```
.
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
            └── feature/{compass,bazi,iching,almanac,name}/
```

## 四、说明

玄学内容仅供参考娱乐。八字「缺即补」为简化思路，严格喜用神需结合日主旺衰与调候。
