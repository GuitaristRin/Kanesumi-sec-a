# Sec-A 默认视觉调研与原版对照

本轮只调研、构建、截图和读图，未修改组件、API、主题或 Ncrust。日期按本次会话环境为 2026-11-12。

结论：Sec-A 目前保存了部分 Kanesumi 几何与动画实现，却没有把 Ncrust 的内容层级和版式表达变成可靠的默认体验。问题同时来自库默认值和 sample 装配；等待 Rust runtime 无法解决眼前这些问题。

## 证据坐标与限制

| 对象 | 本轮固定坐标 | 用途 |
|---|---|---|
| Sec-A 当前检出 | `b8e51a5`，干净工作区；比自身 `origin/master` 多一条本地提交 | 新构建与截图的源码 |
| Sec-A 远端 HEAD | `9ab6ffd`，本轮 `git ls-remote origin HEAD` 查得 | 区分本地修复与已推送版本 |
| Ncrust 当前检出 | `883abf1` | 对照实际消费方式、主题、列表与响应式实现 |
| Kanesumi 主仓远端 HEAD | `0ac3f162e278769dd8f431adb737f04b5c4eb87c`，本轮 `git ls-remote` 查得 | 用 `git show 0ac3f16:<path>` 读取当前主仓代码 |
| 主仓本机子模块检出 | `5c288f5`，与远端不同 | 未切换或覆盖它；不能冒充远端最新状态 |
| Ether 当前检出 | `18ccfa02` | 正典、runtime 计划、供应门与交接材料 |

这是两个独立仓库、两套语言实现，不能用共同祖先或“落后 N 个提交”衡量差距。
Ether 本机有署期 11-13 的交接与盘点；本轮将其视为读到的文档陈述，不据日期推断已经验收。

完整证据在工作台的 [本轮证据目录](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/)，截图未加入 Git，避免复制大型二进制。
[evidence.json](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/evidence.json) 记录完整 SHA、环境、截图 SHA-256、测量值；`sample-build.txt` 保存构建输出，`capture_sample.py` 保存主要页面的复现步骤。

构建：`.\gradlew.bat :sample:assembleDebug --console=plain` → **BUILD SUCCESSFUL in 1m 49s**，112 tasks，13 executed / 99 up-to-date。有 Gradle 弃用警告，不能称“无告警”。

运行：只使用 `emulator-5580`，Pixel_10 AVD，API 37 镜像，软件 GPU，`-read-only -no-snapshot`。没有操作连接着的用户手机。使用本轮成功构建的 APK，安装 `Success`、Activity 启动 `Status: ok`。

截图三档：原生 1080×2424 / 420dpi（约 411×923dp）、小屏 960×1600 / 480dpi（320×533dp）、宽屏 1920×1200 / 240dpi（1280×800dp）。尺寸与密度覆盖已 reset。

本轮没有运行当前 Ncrust、Rust runtime 或真机性能测试。模拟器启动时间不作为性能结论。不同内容、视口和历史版本之间没有计算“像素相似度”。

## 亲自读图所得

| 图 | 亲眼所见 | 证据等级 |
|---|---|---|
| [Ncrust 历史库页](../../../Ncrust/docs/small-screen/shots/e5-home.jpg) | 文件名叫 home，实际是“库”；真实封面承担视觉重心，曲名与灰色元数据紧凑分层，强调色用于选择和动作；底部播放器与导航形成连续中性底 | 历史截图，提交 `84fde97`，2026-10-15 |
| [Ncrust 历史播放器](../../../Ncrust/docs/small-screen/shots/e5-player.jpg) | 大封面形成完整画面；同时存在矮屏遮挡缺陷，不能照搬为验收图 | 同上，修复前问题图 |
| [Ncrust 历史歌词](../../../Ncrust/docs/small-screen/shots/e5-lyrics.jpg) | 绿色当前行承担强调；矮屏可视内容不足 | 同上，修复前问题图 |
| [Sec-A 首页](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-home.png) | 大蓝块与诊断文字占据主要层级；同一行按钮被挤到两行，按钮、进度底板高度不齐；封面只是小色块 | 本轮新构建、运行、截图 |
| [Sec-A 对话框](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-dialog.png) | 对话框确实出现，菜单列结构基本成立，但表面明显偏蓝黑 | 同上 |
| [Sec-A 点击 sheet 后](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-sheet.png) | 点击 Open sheet 后仍是首页，没有可见面板；不是一张已成功展示的 sheet 图 | 同上，失败证据 |
| [Sec-A 详情](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-detail.png) | 大面积纯紫占位不能证明真实封面效果；小字蓝色副标题发暗，列表右侧重复星号抢占注意力 | 同上 |
| [Sec-A 歌词](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-lyrics.png) | 标题进入状态栏；当前行偏暗，未来行很暗；页面底部没有可见的播放/重置按钮，被底栏覆盖 | 同上，动态视觉质量未测 |
| [Sec-A 小屏](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-small-320x533dp.png) | 诊断内容自动折行后占满主要视口，首屏看不到业务列表 | 同上 |
| [Sec-A 宽屏](../../../.arcdeck/artifacts/kanesumi-sec-a-visual-20261112/sec-a-wide-1280x800dp.png) | 内容仍在中央窄列，底部导航却横跨整个窗口，二者尺度失配 | 同上 |

还亲自看过主仓 `docs/research/light_palette/after_dark.png` 与 `after_light.png`：它们是 10-01 的调色板历史样张，橙色与控件形状均不能当成最新主仓的默认视觉。主仓临时值登记明确禁止把这种历史样张扩散为设计规范。

## 原因与落点

### 1. 默认主题没有兑现完整设计语言

`kanesumi-core/.../theme/MetroColors.kt:8` 仅有暗色默认值：`surface #0A0F16`、`surfaceVariant #14181F`、`primary #2E67B5`、正文 `#F0F0F0`。`MetroTheme.kt:14` 直接取这组默认值，没有方案选择或自动跟随系统主题的 API。

Ncrust 并未靠这些颜色获得现有外观：`NcrustColors.kt:63` 的 `toMetroColors` 显式替换整套颜色，暗色用中性 `#1A1A1A`、白字、`#B3B3B3` 次级文字；浅色另有暖白方案。这证明默认体验依赖消费方补配色。

主仓 `0ac3f16` 已有 `MetroTheme::{dark,light,for_scheme}`、独立状态色、强调色派生及控件笔刷，默认方案为 Light。它的具体配色仍是实现决策；不能因主仓默认浅色，就直接改变 Ncrust 的产品默认。

### 2. 默认按钮把普通动作也升级成强调动作

`MetroButton.kt:40` 默认容器为 `primary`，sample 大量 `fillMaxWidth()` 放大了这个选择。原生首页有 **321,391 / 2,617,920 像素 = 12.28%** 恰好等于默认蓝色；这里只统计精确颜色，不含抗锯齿，不是审美分数。

主仓 `kanesumi-controls/src/button.rs` 的 `new()` 默认 `Standard`（`control_fill`），强调按钮由 `accent()` 显式选择。值得对齐的是动作主次与令牌职责，桌面按钮 padding 不宜直接照搬到 Android 触控区。

### 3. sample 没有展示库声称提供的版式

`MainActivity.kt:229` 把 `DebugPanel` 放在首屏；`:299` 用 48dp 色块代替封面；`:304` 把 `MetroListRow` 默认的 0dp 左边距覆写成 16dp。默认按钮旁还塞入进度底板和设置图标，造成横向拥挤。

Ncrust 当前 `SongCard.kt:59` 实际用 72dp 封面、0dp 左边距、14dp 图文间隙、16/12sp 主副文字和单行省略。Sec-A `MetroListRow.kt:43` 默认是 15/13sp、12dp 间隙，未限制行数。两者不是视觉等价的替换关系。

但不能把差距全部归为字号：Ncrust 当前 `pageHeading` 本来就来自 Sec-A 的 34/42 Regular；部分排版已经相同。真正缺少的是页面级参考、内容模型、动作密度与统一的行布局规则。

### 4. 响应式提取落后于实际消费方

`MetroResponsiveContent.kt:23` 对所有宽度一律限制 360dp。1280dp 宽视口中内容列占 **28.125%**，其余横向空间没有用于内容。

Ncrust 自己的 `ui/ResponsiveContent.kt` 已分 `<600dp` 手机与宽屏策略：宽屏默认铺开、留 24dp 边距，文本页才主动限宽。sample 没有采用这套策略，也没有自动切换 sidebar。

手机上的 360dp 限宽本身是 Ncrust 已采用的选择，不应把所有居中边距一概判为违反贴边。应对齐“内容栏内贴边”和不同宽度下内容栏如何分配。

### 5. 存在装配错误，必须先于视觉润色处理

- **Sheet 层序**：`MainActivity.kt:133` 先组合 sheet，再在 `:137` 组合不透明 Shell；`MetroShell.kt:43` 铺满背景。点击后面板不可见与这个层序一致。问题落在 sample 装配，尚不能据此声称 `MetroBottomSheet` 在所有消费方都坏了。
- **歌词安全区与底部留白**：`MainActivity.kt:618` 的歌词分支自己铺满屏，标题没有状态栏留白，底部操作也未避开 Shell 的导航叠层。截图已出现标题侵入系统栏与操作不可见。
- **系统栏明暗**：原生首页时间/信号几乎不可见，dialog 打开时变白可见；`dumpsys window` 记录 Activity 为 `LIGHT_STATUS_BARS LIGHT_NAVIGATION_BARS`，与深色页面不匹配。`enableEdgeToEdge()` 未按库主题显式配置。此结论先限定 API 37 模拟器，不外推到所有 Android 版本。

### 6. 可读性不能靠换成 Ncrust 绿色一次解决

用 sRGB 线性化后的相对亮度计算：

| 前景 / 背景 | 对比度 |
|---|---:|
| Sec-A 默认蓝 / 黑 | 3.724:1 |
| 白 / Sec-A 默认蓝 | 5.640:1 |
| Ncrust 绿 / 黑 | 8.120:1 |
| 白 / Ncrust 绿 | 2.586:1 |

`MetroTabRow.kt:62` 的选中文字是 14sp；默认蓝作为黑底小字比原版绿弱得多。可是若将全库按钮直接换成绿色而继续固定白字，白字可读性又会变差。强调文字色、强调填充、其上前景需要分别建模。

`MetroLyricsPanel.kt:98` 过去行固定白色 alpha、未来行固定灰色 alpha，也没有随深浅主题成组定义。当前只实测暗色，不把浅色潜在问题写成已复现。

## 主仓进展哪些相关，哪些不能直接搬

依据 [设计正典](../../../Ether/docs/KANESUMI_DESIGN.md)、[runtime 工程计划](../../../Ether/docs/RUNTIME_PLAN.md)、主仓 `docs/KANESUMI_RUNTIME.md`，以及 Ether 的供应门和框架供给盘点：

| 范围 | 已读到的进展 | 对 Sec-A 的意义 |
|---|---|---|
| 设计与控件契约 | 深浅主题、强调色派生、控件笔刷、字重阶梯、列表行为、尺寸令牌、光学居中等已有实现与规格 | 可逐项移植语义，Compose 使用本平台的布局/绘制机制落实 |
| R1 共享资源 | 字体按需加载、mmap/索引等已有分项实现与历史真机测量 | 不能把“runtime 未整体落地”误写成所有分项都没做；这些 Linux 进程资源机制也不是 Sec-A 默认页的审美修复 |
| R2/R3 | 第一方动态库与第三方 ABI 的完整交付仍受计划和验收约束 | 不能视为 Android 可直接接入的现成 runtime |
| 框架供应与装配 | 最新盘点仍记录多种控件已提供、应用未迁移；代码存在与消费完成分开 | 主仓也不应凭控件数、提交数被当成默认体验已验收的标杆 |
| 文字层级 | 主仓 Header/Subheader/Title 为 Light，正文 Normal，Base ExtraBold；Sec-A 标题多数 Normal，titleMedium 为 Medium | 确有规范漂移；但 Ncrust 原版也在用当前 Sec-A 标题，不能把改成 Light 等同于“还原原版” |
| 运动 | 现正典允许主运动解析弹簧，Sec-A/Ncrust 本地约定仍禁止弹簧；Ncrust `spec/design/tokens.json` 也明确其时长与桌面不同 | 先记录冲突，本轮不改裁定、不自动同步参数；静态图不能证明手感、可中断性或零重组 |

主仓值得同步的是经过确认的设计契约和默认行为。字体共享、动态链接、Wayland 表面与 Rust 元素树不能直接变成 Kotlin 补丁；本轮没有发现它们是蓝块密度、窄列或 overlay 遮挡的前置依赖。

## 建议实施顺序与下一轮任务卡（尚未开工）

目标：让 Sec-A 零配置组件在真实内容组成的页面中形成可读、克制的 Kanesumi 表达，同时保住 Ncrust 现有消费行为。先建立可复查的默认体验，再扩组件或谈 runtime 对接。

真值：正典负责身份与约束；Ncrust 当前源码负责消费契约，历史图只负责已知视觉表达；主仓 `0ac3f16` 的已确认规范作为同步候选，临时值不得升级为设计。

1. **修复 sample 的可见性问题**：sheet 层序、歌词顶部安全区/底部操作留白、系统栏明暗。先恢复能看、能点的展示页，不改变库公共默认值。
2. **整理默认组件语义**：标准/强调按钮分工、深浅方案、中性色表面、强调填充与前景。默认值变化会影响下游，先在组件规格中列出行为与兼容办法；不把所有按钮变绿当作还原。
3. **补页面参考**：把诊断集中到专门入口，用来源明确的封面和中英混合长文本展示列表、详情、菜单、表单；统一对齐线、动作密度和省略策略。库默认效果与 Ncrust 显式品牌主题都需要有独立页面，防止 sample 靠私有修饰掩盖默认缺陷。
4. **补响应式策略**：保留手机已有合理限宽；宽屏内容铺开/文本限宽，导航与内容尺度协调；小屏诊断换行不得吞掉整个默认展示页。
5. **单列动效研究**：录屏与逐帧对照 Ncrust 的真实行为，核对中断和状态反馈，再讨论与主仓词汇表的对应。不能以本轮静态图宣称动效已对齐。

建议验收：

- 同一组固定内容，默认组件与 Ncrust 显式主题分别截图；覆盖列表、详情、对话框、sheet、表单、歌词。
- 覆盖 320×533dp、普通手机、1280×800dp，以及深浅主题。截图后亲自看图并记录视口、字体缩放、源码 SHA。
- sheet 能实际显示；歌词返回、播放和重置操作可见可用；系统栏可辨；短按钮文案不被不合理挤压；长列表文本溢出可预测。
- 库 API 变更检查 Ncrust 的 `toMetroColors` 和现有调用，并运行两仓对应 assembleDebug；编译通过与视觉验收分开汇报。
- 给出配色对比度、内容栏宽度、控件尺寸等测量，不用“更高级”“相似度 90%”代替证据。

止损：不新增依赖，不整体迁入桌面 runtime，不复制 Linux 字体/ABI 机制，不重做 Ncrust 业务页面，不改播放器三层架构，不直接推翻已有动效裁定。

本轮产物仅此调研报告与工作台证据文件。没有组件修复、没有新测试套件、没有发布或推送，也没有把本轮截图视为整改后的验收。
