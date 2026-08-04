# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

Kanesumi (矩隅) is a Metro-style Android UI library. **Zero `material3` dependency by design** —
built only on `androidx.compose.foundation` + `androidx.compose.ui`. The goal is that the *default*
widgets are already Metro (right angles, borderless, flat), not "Material with a theme override".

Version: `v0.1.0-SNAPSHOT`. Group: `io.github.takahashirinta`.

Design source-of-truth: `README.md` — the 「为什么叫 Kanesumi / 设计理念 / 关键决策 (ADR)」
sections. When making non-trivial decisions (new component API shape, animation strategy, layering),
consult it — it records the *why* behind the constraints below.

## Build & run

```bash
./gradlew :sample:assembleDebug       # demo app APK
./gradlew :kanesumi-core:assemble     # single-module AAR
./gradlew assemble                    # everything
```

Toolchain: AGP 8.5.0, Kotlin 1.9.24, Compose BOM 2024.12.01, Compose compiler 1.5.14,
`compileSdk=36`, `minSdk=24`, `jvmTarget=11`. Gradle daemon runs on JDK 21
(`gradle/gradle-daemon-jvm.properties`).

There is no test suite yet — no `test` / `androidTest` sources exist. Don't invent a lint or
test command; the CI shape is not yet fixed.

## Module layout & dependency direction

```
:kanesumi-core   ── leaf ─┐
:kanesumi-anim   ── leaf ─┤ (siblings, no deps between them)
:kanesumi-controls        │ api → core, anim
:kanesumi-structure       │ api → core, anim, controls  ← inverts the "layer" number
:sample                   │ depends on all four
```

Note the inversion: the README module table numbers `structure` as layer 2 and `controls` as
layer 3, but `:kanesumi-structure` *depends on* `:kanesumi-controls` because scaffolds/chrome want
buttons and surfaces for empty/error states. Direction is acyclic; do not add a controls→structure edge.

`api(project(...))` is used deliberately — downstream consumers (including `:sample`) should be able
to reach `MetroText`, `MetroSurface`, etc. through whichever module they already imported.

## Non-negotiable conventions

These are enforced by API shape and reviewed strictly. Break them and the perf/consistency story
falls apart.

**1. No `material3` imports anywhere in library modules.** Only `foundation`, `ui`, `runtime`,
`ui-graphics`. The `:sample` module is allowed to pull `androidx.compose.material:material-icons-core`
for `ImageVector` assets — that package has no theme/components, only icon vectors. Do not add
`material` or `material3` even in sample.

**2. GPU zero-recomposition for animation.** Drive animations with a single `progress: Float`
(usually `Animatable`), and read it *only* inside `graphicsLayer { … }` / `drawBehind { … }` /
`drawWithCache`. **Never** use `animateFloatAsState` at the composition phase for visual properties
— it forces recomposition on every frame. `MetroIndication` in `kanesumi-core` is the reference
implementation (Modifier.Node + DrawModifierNode reading `alpha.value` inside `draw()`).

**3. Use Sokuou presets, don't hand-roll animation specs.** All animation specs live in
`kanesumi-anim` under `SokuouPresets` / `SokuouTweens` / `sokuouSpring(response, dampingRatio)`.
Do not scatter `tween(300, CubicBezierEasing(0.2f, 0f, 0f, 1f))` across call sites — add a named
preset if a new one is genuinely needed.

**4. Bottom overlay coordination via `MetroBottomStack`.** Anything that sits at the bottom of
the screen (bottom nav, mini player, etc.) must call `rememberBottomStackReservation(key, heightDp)`
so its height enters the shared stack. Scrollable content pads itself with `bottomOverlayPadding()`
(or `Modifier.metroBottomOverlayPadding()`) to avoid being covered. `MetroShell` installs the
`MetroBottomStackScope` — code outside a `MetroShell` that calls these helpers will crash by design.

**5. Wrap the app in `MetroTheme`.** It installs `LocalIndication = MetroIndication(tint = colors.pressTint)`,
so every `.clickable {}` in the tree gets the right-angle press flash for free. Do not pass
`indication = ...` explicitly unless overriding intentionally.

**6. Insets go through `MetroInsets`, not raw `WindowInsets`.** `rememberMetroInsets()` returns
a single immutable snapshot bundling status bar / nav bar / display cutout / IME with both
`Dp` and `Px` per edge. New components asking "how far from the top edge am I safe?" should read
`MetroInsets`, not re-derive from `WindowInsets.*` — the single-source-of-truth is the whole
point of layer 0.

## Architectural shape (what to read to get productive)

Start here in order:

1. `kanesumi-core/.../insets/MetroInsets.kt` + `MetroBottomStack.kt` — the "护城河" (moat) layer.
   Understanding these two files explains why `MetroShell` looks the way it does and why bottom
   overlays coordinate through a scope instead of prop-drilling heights.
2. `kanesumi-core/.../theme/MetroTheme.kt` + `MetroIndication.kt` — how the "default is Metro"
   promise is actually installed (via `LocalIndication`) and the reference for the zero-recomposition
   draw pattern.
3. `kanesumi-anim/.../sokuou/Sokuou.kt` — animation vocabulary. `SokuouPresets.SheetAppear`,
   `.QuickSwitch`, `sokuouSpring(...)` — this is the whole vocabulary; use these names.
4. `kanesumi-structure/.../MetroShell.kt` — the app-level container. Note that it *does not*
   provide a `topBar` slot: Metro convention is "top bar = the first item of the scrolling
   list", not chrome. `MetroAppBar` is meant to be an item inside a `LazyColumn`, not a Scaffold slot.
5. `kanesumi-controls/` — Ncrust-parity widgets (each Metro* has a comment noting which
   Material 3 / Ncrust component it replaces).

## Ncrust parity (project context)

Kanesumi is intended to replace all Material 3 / hand-rolled UI in a sibling project called
**Ncrust**. The README's parity table (`Text` → `MetroText`, `DropdownMenu` → `MetroDropdownMenu`,
`SongMenuSheet` → `MetroBottomSheet`, etc.) is the definition of "component layer complete" —
when adding new components, check whether Ncrust already has a Material equivalent that needs
mapping. The player-card three-layer graphics architecture explicitly stays *out* of this library
(app-specific, not a general control).
