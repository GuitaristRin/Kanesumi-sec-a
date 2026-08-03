# Kanesumi(矩隅)

一个 Metro 风格 Android UI 库。以直角丈量边缘。

- 独立 repo,**零 material3 依赖** —— 只用 `androidx.compose.foundation` + `androidx.compose.ui`
- 目标:"默认即 Metro" —— 不套主题,默认元件就是直角、无边框、信息优先的改良 Metro
- GPU 零重组 —— 动画单一 `progress: Float` 驱动,视觉只在 `graphicsLayer` / `drawBehind` 内读取
- 命名坐标(计划):`io.github.takahashirinta:kanesumi-*`
- 设计文档:[`docs/Kanesumi_设计文档.md`](docs/Kanesumi_设计文档.md)

## 模块

| 模块 | 职责 |
|---|---|
| `:kanesumi-core` | 层 0 —— MetroInsets 安全区抽象、MetroBottomStack 底部叠层栈、主题基座 (MetroTheme/MetroColors/MetroTypography)、MetroText/MetroIcon、MetroIndication 直角闪切 |
| `:kanesumi-anim` | 层 1 —— Sokuou 分支:UWP easing 全家族、sokuouSpring 桥接、SokuouPresets/SokuouTweens、MetroFlingBehavior |
| `:kanesumi-structure` | 层 2 —— MetroShell / MetroAppBar / MetroDetailScaffold / MetroTopScrim / MetroBottomNav |
| `:kanesumi-controls` | 层 3 —— MetroSurface / MetroButton / MetroListRow / MetroSwitch / MetroTabRow / MetroIconButton / MetroDivider / MetroProgressIndicator / MetroDialog / MetroDropdownMenu / MetroBottomSheet / MetroResponsiveContent |
| `:sample` | 演示 app —— 覆盖所有组件的真实交互流 |

## 现状

v0.1.0-SNAPSHOT —— **组件层第一轮完成 + 首屏 Ncrust 反向迁移落地**。

Kanesumi 已被 Ncrust 通过 Gradle 组合构建 (`includeBuild`) 实际消费,SearchScreen
作为首个迁移屏跑通:M3 `Text` / `Icon` / `DropdownMenu` / `DropdownMenuItem` /
`NcrustIconButton` / `NcrustProgressIndicator` / `NcrustTabRow` / `TextButton` /
`LocalTextStyle` 全部换成 Metro* 对应物,包含 MetroTheme 从 NcrustColors 派生的
主题桥接。基础设施验证通过 —— 后续屏是重复劳动,不是新的架构风险。

组件覆盖范围对齐 Ncrust 的全部 M3 组件依赖与手滚组件:
`Text`/`Icon` → `MetroText`/`MetroIcon`,`DropdownMenu` → `MetroDropdownMenu`,
`HorizontalDivider` → `MetroDivider`,`NcrustPivotNav` → `MetroBottomNav`,
`NcrustTabRow` → `MetroTabRow`,`NcrustIconButton` → `MetroIconButton`,
`NcrustProgressIndicator` → `MetroProgressIndicator`,`PlayAllDialog` → `MetroDialog`,
`SongMenuSheet` → `MetroBottomSheet`,`DetailScaffold`/`ResponsiveContent` →
`MetroDetailScaffold`/`MetroResponsiveContent`。

播放器三层图形架构(PlayerCard 手势联动)不在库内 —— 那是 app 独有形态,不是通用控件。

### v0.1 里 API 打磨点(反向迁移驱动的补丁)

- `MetroText` 补齐 `maxLines / overflow / softWrap / minLines` 参数透传 —— 之前
  只能靠 BasicText 兜底,阻断了任何需要单行截断的迁移场景。
- `MetroTypography` 补 lineHeight 到全部样式(消除中文长段落行距过松),并追加
  M3 命名尺度轴 (`headlineMedium` / `titleLarge` / `titleMedium` / `bodyLarge` /
  `bodyMedium` / `bodySmall`) 与语义轴 (`pageHeading` / `title` / `body` /
  `caption` / `label`) 并存 —— M3/Ncrust 代码迁进来可以一比一替换 style 引用。
- `MetroColors.onSurfaceMuted` → `onSurfaceVariant`,对齐 M3 / 社区通用词汇,
  减少迁移期 sed 摩擦。
- `MetroTabRow` 顶部指示条与文字之间从 0dp 改成 12dp 呼吸,对齐 UWP Pivot 手感。

## Ncrust 消费方式(组合构建)

Ncrust 侧 `settings.gradle.kts` 加:

```kotlin
includeBuild("../../projects/Kanesumi") {
    dependencySubstitution {
        substitute(module("io.github.takahashirinta:kanesumi-core"))
            .using(project(":kanesumi-core"))
        substitute(module("io.github.takahashirinta:kanesumi-anim"))
            .using(project(":kanesumi-anim"))
        substitute(module("io.github.takahashirinta:kanesumi-controls"))
            .using(project(":kanesumi-controls"))
        substitute(module("io.github.takahashirinta:kanesumi-structure"))
            .using(project(":kanesumi-structure"))
    }
}
```

`app/build.gradle.kts` 加:

```kotlin
implementation("io.github.takahashirinta:kanesumi-controls:0.1.0-SNAPSHOT")
implementation("io.github.takahashirinta:kanesumi-structure:0.1.0-SNAPSHOT")
```

Kanesumi 侧无需 `maven-publish` —— 显式 `dependencySubstitution` 让 Gradle 直接
把符号坐标映射到 includeBuild 的项目。未来发到 Maven Central 时,Ncrust 只需
删掉整个 `includeBuild` 块,`implementation(...)` 的坐标一字不改就能切。

## 使用方法

```kotlin
MetroTheme {
    MetroShell(bottomBar = { MetroBottomNav(items = ..., selectedIndex = ..., onSelected = ...) }) {
        LazyColumn(
            contentPadding = bottomOverlayPadding(),  // 自动避开底部叠层
        ) {
            item { MetroAppBar(title = "Home") }
            item { MetroButton(text = "Action", onClick = { ... }) }
        }
    }
}
```

- 所有 `.clickable {}` 默认走 MetroIndication 直角闪切(由 MetroTheme 注入 LocalIndication),无需显式传 indication
- 结构元件(Nav / mini bar)通过 `rememberBottomStackReservation` 自动登记进底部叠层栈,内容侧 `bottomOverlayPadding()` 一句避开
- 动画一律用 `:kanesumi-anim` 的 Sokuou 预设,不散写 `tween(300, CubicBezierEasing(...))`

## 构建

```bash
./gradlew :sample:assembleDebug       # 演示 app
./gradlew :kanesumi-core:assemble     # 单个模块 AAR
```
