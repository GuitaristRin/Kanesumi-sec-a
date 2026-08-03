# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew :sample:assembleDebug       # 演示 app → sample/build/outputs/apk/debug/
./gradlew :kanesumi-core:assemble     # 单个模块 AAR
./gradlew build                       # 全量构建
./gradlew test                        # 运行单元测试
```

- Target/Compile SDK: 36 (Android 15), Min SDK: 24
- Java 11, Kotlin 1.9.24, AGP 8.5.0, Compose BOM 2024.12.01, Compose Compiler 1.5.14
- **网络提示**:本机访问 `services.gradle.org` 不稳定,首次构建若 gradle-9.3.1 分发下载超时,用腾讯镜像 `https://mirrors.cloud.tencent.com/gradle/gradle-9.3.1-bin.zip` 手动放入 `%USERPROFILE%\.gradle\wrapper\dists\gradle-9.3.1-bin\<hash>\` 目录。

## Development Log & Commit Convention

**No separate log file.** All development history lives in `git log`. Do not create or maintain a parallel `*_log.md` file.

Commit messages follow **Conventional Commits** with a lowercase type prefix (与 Ncrust 同一约定):

| Prefix | When to use |
|---|---|
| `feat:` | User-visible new capability (组件、API 新增) |
| `fix:` | Bug fix; no new behaviour beyond restoring correctness |
| `chore:` | Housekeeping (delete unused files, rename directories, gitignore updates) |
| `docs:` | Docs-only changes (README, AGENTS.md, in-code comments) |
| `build:` | Build system / dependencies / version bumps |
| `refactor:` | Code shape change without behavioural change |
| `perf:` | Performance-only optimisation |
| `style:` | Formatting, whitespace, comment tweaks |

Subject line: prefix + one-sentence Chinese summary. Body (blank line, then paragraphs) explains *why* — 关键设计决策要写原因,让一年后读 git log 不需要开 PR。

## Versioning

Single source of truth: `gradle.properties` → `VERSION_NAME` (当前 `0.1.0-SNAPSHOT`)+ `GROUP` (`io.github.takahashirinta`)。

发布坐标(计划):`io.github.takahashirinta:kanesumi-*`。模块内不硬编码版本号。

## What This Project Is

Kanesumi(矩隅)—— 一个 Metro 风格 Android UI 库,以直角丈量边缘。三句话定位:

1. **零 material3 依赖** —— 只用 `androidx.compose.foundation` + `androidx.compose.ui`,不套主题;
2. **默认即 Metro** —— 默认元件就是直角、无边框、信息优先的改良 Metro;
3. **GPU 零重组** —— 动画单一 `progress: Float` 驱动,视觉只在 `graphicsLayer` / `drawBehind` 内读取。

设计文档见 `docs/Kanesumi_设计文档.md`(命名决策、技术路线、分层、ADR)。

## Relationship with Sokuou

- **Sokuou 是独立的桌面端动画引擎**(即応エンジン,Rust),仓库 `GuitaristRin/Sokuou`,不在本仓库内。
- 本库是 **Sokuou 家族在 Kotlin/Compose 生态上的分支**,负责 Metro 组件层,不吞并 Sokuou。
- `:kanesumi-anim` 是移植分支,与 `PezMax-One/src/sokuou` 保持同名 API 与参数惯例,方便跨项目共享动画词汇。上游改动时本分支同步移植,不做私有重命名。

## Architecture

### 模块分层

| 模块 | 依赖 | 职责 |
|---|---|---|
| `:kanesumi-core` | 仅 foundation/ui/runtime | 层0 — MetroInsets 安全区抽象、MetroBottomStack 底部叠层栈、MetroTheme/MetroColors/MetroTypography、MetroText/MetroIcon、MetroIndication 直角闪切 |
| `:kanesumi-anim` | 仅 foundation/ui/runtime | 层1 — UWP easing 全家族、sokuouSpring 桥接、SokuouPresets/SokuouTweens、MetroFling |
| `:kanesumi-controls` | core + anim | 层3 控件(MetroButton/MetroSwitch/MetroDialog/MetroBottomSheet/…) |
| `:kanesumi-structure` | core + anim + controls | 层2 结构元件(MetroShell/MetroAppBar/MetroBottomNav/MetroDetailScaffold/…) |
| `:sample` | 全部 | 演示 app —— 覆盖所有组件的真实交互流 |

**依赖方向必须无环**:anim/core 是叶子,只依赖 Compose 基础;controls 依赖 core+anim;structure 依赖 controls。新代码不得反转方向。

### 零 M3 铁律

- 任何模块**不得**依赖 `androidx.compose.material3`(也不得依赖 `material` / `material-icons-core` —— 图标由调用方以 `ImageVector` 提供)。
- 交互反馈一律走 `LocalIndication`(MetroTheme 注入 `MetroIndication`),`.clickable{}` 默认即直角闪切。
- 任何新增组件先确认 M3 等价物并明确"为什么不用它"。

### 动画模式

- 视觉属性只在 `graphicsLayer { }` / `drawBehind` / draw phase 内读取 `Animatable`;**绝不在组合阶段用 `animateFloatAsState` / `animateColorAsState`**。
- 一律用 `:kanesumi-anim` 的 Sokuou 预设,不散写 `tween(300, CubicBezierEasing(...))`。
- Metro 无弹簧/回弹、无 ripple 扩散、无 elevation 联动 —— 受控减速(MetroCubic 等)。
- 颜色插值用"叠层 alpha"(如 `MetroBottomNav` 双 Icon 叠 Alpha)而非 `animateColorAsState`。

### 贴边与安全区(层0 是护城河)

- `MetroShell` 把 bottomBar 作为**悬浮 overlay** 画在 content 之上,content 铺满全屏;M3 Scaffold 是"从 content 切出空间",Kanesumi 不这么做。
- 结构元件(Nav / mini bar)通过 `rememberBottomStackReservation(key, heightDp)` 自动登记进底部叠层栈;内容侧一句 `bottomOverlayPadding()` 自适应留白。
- 系统导航栏 / 状态栏 / 挖孔 padding:`metroNavigationBarsPadding()` / `metroStatusBarsPadding()` / `metroSystemBarsPadding()` / `metroDisplayCutoutPadding()`,由调用方决定贴哪条边。
- 异形屏兼容(挖孔/刘海/手势条)的目标是变成**库的问题,不是开发者的问题** —— 新 inset 相关 API 必须收敛进 core/insets,不散落各组件。

## Key Constraints

- **Metro Design**:无圆角、无阴影、无边框(除非信息需要),直角切一切。Cover/图片铺满不做圆角裁切。
- **默认即 Metro**:每个新组件必须有合理默认值 —— 调用方零配置得到的即是 Metro 外观;任何需要"调教"才能成型的 API 都是设计缺陷。
- **a11y 不可妥协**:控件必须带 `semantics`(role/contentDescription),`MetroIndication` 只负责视觉反馈。
- **注释**:关键设计决策写 *why*,参数给出来源(如 SOKUOU_ENGINE.md 的典型值表),中英混合。
- **播放器三层图形架构**属于 app 独有形态,不进库 —— 那是 Ncrust 的领域,不是通用控件。
