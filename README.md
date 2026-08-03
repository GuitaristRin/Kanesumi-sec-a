# Kanesumi(矩隅)

一个 Metro 风格 Android UI 库。以直角丈量边缘。

- 独立 repo,不依赖 Material 3
- 依赖 `androidx.compose.foundation` + `androidx.compose.ui`
- 命名坐标(计划):`io.github.takahashirinta:kanesumi-*`
- 设计文档:[`docs/Kanesumi_设计文档.md`](docs/Kanesumi_设计文档.md)

## 模块

| 模块 | 职责 |
|---|---|
| `:kanesumi-core` | 层 0 —— MetroInsets、基础绘制原语、性能约定 |
| `:kanesumi-anim` | 层 1 —— UWP easing 家族 + sokuouSpring 桥接 |
| `:kanesumi-structure` | 层 2 —— Window / AppBar / NavPane 结构元件 |
| `:kanesumi-controls` | 层 3 —— 按钮、列表、输入等控件 |
| `:sample` | 演示 + 性能 benchmark 载体 |

## 现状

v0.1.0-SNAPSHOT —— 骨架搭建阶段,四个模块均为空壳。
