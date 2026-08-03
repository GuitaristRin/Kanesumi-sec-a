# Kanesumi(矩隅)设计文档

> 一个 Metro 风格 Android UI 库 —— 让开发者像开发 UWP 应用一样,使用默认元件即可得到完整的"改良 Metro"风格。
>
> 版本:v0.1(设计草案)
> 状态:讨论定稿,待原型验证

---

## 0. 一句话定位

**Kanesumi 是一个独立的、高性能的 Metro 风格 Android UI 库**,它的目标不是"套一层 Metro 主题",而是让**默认值本身就是 Metro** —— 开发者不配置任何东西,得到的界面就是直角、无边框、信息优先的改良 Metro。

---

## 1. 命名决策

### 1.1 定稿

- **英文名:Kanesumi**
- **日文书写:矩隅**
- **中文名:矩隅**

### 1.2 词源

| 字 | 读音 | 含义 | 与库的对应 |
|---|---|---|---|
| 矩 | かね(kane) | 矩尺,画出直角的工具 | "以直角丈量边缘" —— 几何身份 |
| 隅 | すみ(sumi) | 角落、边缘(同音"墨",日本墨的东方意境) | 直角、无边框、贴边 |

合意:**"以直角丈量边缘"**,浓缩了本库的整个几何身份(直角 / 无边框 / 贴边)。隅同音"墨"提供东方审美双关,Logo 可用"隅"或"墨"字。

### 1.3 为何不用 Sumikane(隅矩)

`すみかね` 恰好是日语现成短语 **"住みかね"**(难以居住 / 犹豫不决),存在负面歧义;且"隅(墨)"打头会先导向"颜色"而非"几何"。Kanesumi 以"矩"立住几何,再谈墨色,更自然。

### 1.4 命名历程(取舍记录)

- 英文常用词(Tile / Chisel / Ortho / Crisp / Tessera / Miter):被否 —— 太通用或缺乏东方气质。
- 纯日语单字(隅 / 折 / 矩 / 凪 / 凛):都是合格候选,但单一意象不够"库"的完整感。
- 最终选择双字组合 **矩隅**:真实训读日语组合词(非拼凑罗马音),兼具几何精准与东方美感。

---

## 2. 项目背景与动机

### 2.1 痛点

作者已制作多个 Metro 风格软件,但每次都要:

1. **反复调教 Jetpack Compose 与魔改 Material 3** —— M3 自带圆角、阴影、默认动画、默认动效规格,与 Metro 直角无边框理念相悖,每次都要覆盖重置,极不独立;
2. **性能堪忧** —— Compose 的 recomposition 作用域、M3 高层组件的惰性语义,带来不必要的性能损耗;
3. **贴边与异形屏适配重复劳动** —— 每次都要处理状态栏、手势条、挖孔/刘海、真全屏(如 Xperia)的 inset 补偿,没有一个统一抽象。

### 2.2 目标

- **零调教**:默认元件即 Metro 风格,无需对抗 M3;
- **高性能**:GPU 零重组,动画只发生在绘制阶段;
- **贴边自适应**:无缝贴边成为库的默认能力,而不是开发者的负担;
- **异形屏兼容**:挖孔 / 刘海 / 真全屏一次抽象、处处生效;
- **可日用**:面向真实日常软件的组件完整度。

---

## 3. 与 Sokuou 的关系

- **Sokuou 是独立的桌面端动画引擎**(即应,来自 PezMax-One Rust 项目的移植),面向桌面应用;
- **Kotlin / Compose 只是 Sokuou 的一个分支**;
- 本库定位:**Sokuou 家族在 Kotlin/Compose 生态上的分支**,负责 Metro 组件层,不吞并 Sokuou。
- 动画曲线(UWP easing 全家族)、`sokuouSpring(response, damping)` 桥接等,**上游来自 Sokuou**,本库消费而非重造。

> 命名判据因此变化:本库不需要"自带速度感"(那是 Sokuou 的事),而应聚焦直角、无边框、贴边、扁平的几何身份,并与 Sokuou 在气质上同源。

---

## 4. 设计哲学(改良 Metro)

三大设计优先级:

1. **直角(No Curves)** —— 无圆角、无曲线,一切由直角切出;
2. **无边框(Borderless)** —— 去装饰,信息优先,大量元件需要完全无缝贴边;
3. **扁平与克制(Flat & Controlled)** —— 非物理动画(UWP easing,无弹簧/回弹),受控减速。

### 4.1 "改良"体现在哪

- 不是 Windows 8 磁贴式堆叠,而是**信息优先**的直角版式;
- 无边框 + 贴边,追求屏幕利用率;
- 动画使用 Sokuou 的受控减速,而非系统默认的物理弹性。

---

## 5. 性能原则

**核心模式:GPU 零重组(zero-recomposition)**

- 单一 `progress: Float` 驱动所有动画;
- 视觉属性一律在 `graphicsLayer { }` 中计算(仅绘制阶段),**绝不在组合阶段用 `animateFloatAsState`**;
- 状态全部下沉到 ViewModel(`MutableStateFlow`),组合层只读稳定快照;
- 类稳定性:`@Stable` / `@Immutable` 显式标注,避免 recomposition 作用域扩张;
- 库的 API 设计**强制**此约定:不提供 state 驱动动画的高层组件,动画值只从 `Animatable` 进 `graphicsLayer`。

> 诚实判断:大部分 Compose 卡顿源于用法(重组作用域太宽、类不稳定、lambda 捕获大对象),而非 Compose 本身。本库把正确用法固化成 API 形态,使性能保证随库自带。

---

## 6. 关于"更底层"的技术路线

讨论结论:不需要走到 Skia 自绘(路线 B),那等于重造布局/文本/手势/a11y 引擎。

| 路线 | 内容 | 结论 |
|---|---|---|
| **A. Compose 之上、Material 之下(采用)** | 依赖 `foundation` + `ui` 原语,完全不碰 `material3`。直角/扁平/无边框直接用 `drawWithCache` + 自定义 `Modifier` 绘制 | 保留 Compose 的语义、a11y、文本排版、手势系统,摆脱 M3 的默认圆角/阴影/动效 |
| B. Skia 自绘(SurfaceView/Canvas) | 真正脱离 Compose 布局 | 成本过高,且 Compose Multiplatform 底层本就是 Skia,绕路 |
| C. 纯数据驱动 UI | UI 描述为纯数据,由渲染器解释 | 交互式页面(播放器卡片手势联动)不适合,过度设计 |

路线 A 的自有成本:需自行实现 **ink 反馈、焦点态、语义树**(无障碍不可妥协)。

---

## 7. 核心架构分层

### 层0:安全区抽象(MetroInsets)—— 护城河

**为什么这是最关键的一层**:UWP 时代 Windows 提供唯一确定的几何(固定任务栏/固定安全区),所以贴边"很自然"。而 Android 的几何是**分裂的**,每次开发都要面对:

- 状态栏:挖孔(左上/居中/右上)、刘海、真全屏(如 Xperia 21:9)各不相同;
- 底部:手势条(需留白)vs 三键(实底),且 M3 `NavigationBar` 实际 80dp vs 设计常数 56dp 的偏差;
- Metro 要"内容无缝顶到屏幕边缘",直接与 `WindowInsets` 的默认语义冲突。

**方案**:统一封装 `systemBars + displayCutout + navigationBar` 的合并与拆分,让贴边元件只问一句 **"我的哪条边要被谁让开"**,而不是每个组件各自计算 inset。

**验收标准**:异形屏兼容变成"库的问题",而不是"开发者的问题"。

### 层1:Sokuou + 动画引擎 + Metro 基础原语

- UWP easing 全家族(Quadratic ~ Elastic × In/Out/InOut)+ `sokuouSpring(response, dampingRatio)`;
- 动画计算模块(进度→值映射、`mapRange` / `mapRangeClamped`);
- Metro 基础原语:直角、无边框绘制基元,自定义 ink 替代 M3 ripple;
- 性能约定在此固化。

### 层2:结构元件

- 窗口/框架(对应 UWP Window 的顶层容器);
- AppBar / NavPane(汉堡菜单入口)/ Content 框架(对应 UWP NavigationView);
- 贴边元件与 MetroInsets 的接线在此完成。

### 层3:控件库(长期滚动补齐)

按钮、列表、文本输入、对话框、Flyout、开关、滑块……每一件都要 Metro 造型 + a11y 语义 + 手势行为。

---

## 8. 命名空间与模块规划(草案)

```
kanesumi/
├── :kanesumi-core        # 层0 MetroInsets + 基础原语 + 性能约定
├── :kanesumi-anim        # 消费 Sokuou 动画曲线(分支),动画计算模块
├── :kanesumi-structure   # 层2 结构元件(AppBar/NavPane/Content)
├── :kanesumi-controls    # 层3 控件库
└── sample/               # 性能 benchmark + demo app(证明"高性能"的载体)
```

> 现阶段验证优先级:先让 `kanesumi-core` 独立编译、零 M3 依赖、单独出 AAR。

---

## 9. 里程碑

1. **M0 原型**:从 Ncrust 提炼 `MetroInsets` 抽象,验证"零 M3 依赖"要重写多少;
2. **M1 层0**:安全区抽象正式成型,异形屏 + 手势条贴边一次解;
3. **M2 层1**:动画引擎分支 + Metro 基础原语(直角、无边框、自定义 ink);
4. **M3 层2**:Window 框架 + NavigationView 对应物(汉堡菜单);
5. **M4 层3**:控件库按日常软件需求滚动补齐;
6. **M5 发布**:benchmark 达标后,评估是否公开为独立库。

---

## 10. 设计决策记录(ADR)

| 决策 | 结论 | 原因 |
|---|---|---|
| 库名 | Kanesumi(矩隅) | 真实日语组合词,几何精准 + 东方美感,无负面歧义 |
| 与 Sokuou 关系 | Sokuou 独立(桌面),本库为其 Kotlin 分支 | 各自独立演进,本库消费曲线不吞并引擎 |
| 技术路线 | Compose 之上、Material 之下 | 保留平台能力,摆脱 M3 魔改 |
| 动画策略 | 单 progress + graphicsLayer,零重组 | GPU 性能核心 |
| 贴边策略 | 统一 MetroInsets 抽象 | Android 几何分裂,需一次解而非逐组件算 |
| 无障碍 | 层0 起自带语义树 | 不可妥协 |

---

## 11. 遗留问题 / 待讨论

- [ ] Logo 与视觉标识(隅/墨字图形);
- [ ] 层0 是否要兼容 Compose Multiplatform(桌面端分支)的排期;
- [ ] "默认即 Metro"与 Android 平台手势(返回/滑动)的融合方式;
- [ ] benchmark 指标定义(重组次数 / 帧耗时 / 冷启动);
- [ ] 与 Ncrust 的抽取边界:哪些组件回填到 Ncrust,哪些留在库内。

---

*本文档由作者与 AI 协作讨论形成,记录了从动机、命名、技术路线到架构分层的完整思考。*
