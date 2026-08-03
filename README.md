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

v0.1.0-SNAPSHOT —— 组件层已完成第一轮。当前覆盖范围对齐 Ncrust 的全部 M3 组件依赖与手滚组件:
Ncrust 的 `Text`/`Icon` → `MetroText`/`MetroIcon`,`DropdownMenu` → `MetroDropdownMenu`,
`HorizontalDivider` → `MetroDivider`,`NcrustPivotNav` → `MetroBottomNav`,`NcrustTabRow` → `MetroTabRow`,
`NcrustIconButton` → `MetroIconButton`,`NcrustProgressIndicator` → `MetroProgressIndicator`,
`PlayAllDialog` → `MetroDialog`,`SongMenuSheet` → `MetroBottomSheet`,`DetailScaffold`/`ResponsiveContent` →
`MetroDetailScaffold`/`MetroResponsiveContent`。

播放器三层图形架构(PlayerCard 手势联动)不在库内 —— 那是 app 独有形态,不是通用控件。

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
