# Compose Advanced Demos Design Spec

**Date:** 2026-04-24
**Module:** compose-demo
**Scope:** 8 new Activity demos covering advanced Compose topics

## Overview

The compose-demo module currently has 31 Activities across 7 levels. This spec adds 8 new Activities to deepen coverage of state management, animation, performance, and advanced patterns. All new Activities follow the existing pattern: standalone Activity with detailed comments and interactive demos.

## New Activities

### Level 3 (State & Interaction)

#### 1. StateRestoreActivity — rememberSaveable & State Restoration

**Purpose:** Demonstrate how Compose survives process death and configuration changes.

**Demos:**
- `remember` vs `rememberSaveable` side-by-side comparison — type text, rotate device, observe which survives
- Custom `Saver` for a data class (e.g., `data class UserInput(name: String, age: Int)`) — implement `Saver<UserInput, Bundle>`
- `SaveableStateHolder` — save/restore scroll position when switching between tabs
- `rememberSaveable(saver = ...)` with `mapSaver` / `listSaver` shortcuts

**Key concepts:** process death, Bundle serialization, custom Saver, SaveableStateHolder

#### 2. SnapshotFlowActivity — SnapshotFlow & State-to-Flow Bridge

**Purpose:** Bridge between Compose State and Kotlin Flow.

**Demos:**
- `snapshotFlow { }` — convert `remember` state to a cold Flow, collect with `LaunchedEffect`
- Monitor `LazyListState.firstVisibleItemIndex` — show "scroll to top" button when scrolled past item 10
- `Flow.collectAsState()` — consume a Room Flow in Compose UI
- Debounce text input — `snapshotFlow` + `debounce(300ms)` for search-as-you-type

**Key concepts:** snapshotFlow, collectAsState, LaunchedEffect + Flow, debounce

---

### Level 6 (Animation & Graphics)

#### 3. InfiniteTransitionActivity — Infinite & Physics-based Animations

**Purpose:** Cover infinite looping animations and spring/decay physics.

**Demos:**
- `rememberInfiniteTransition` — rotation loading spinner, pulsing scale effect, color cycling
- `spring()` — dampingRatio (under-damped bouncy, critically-damped, over-damped stiff) with interactive slider controls
- `animateDecay` — fling gesture with velocity, observe deceleration curve
- Gesture-driven spring animation — drag a card, release triggers `spring()` snap-back

**Key concepts:** InfiniteTransition, spring parameters, animateDecay, gesture + animation combination

#### 4. GraphicsLayerActivity — RenderEffects & Shaders

**Purpose:** Explore the GraphicsLayer API for advanced visual effects.

**Demos:**
- `GraphicsLayer` basics — `renderEffect` for runtime blur (stackBlur on an image card)
- `RenderEffect.createBlurEffect` + `createColorFilterEffect` — combine blur + tint
- `ShaderBrush` with AGSL — custom gradient pattern (wave, noise)
- Real-world: frosted glass card effect — blurred background + semi-transparent overlay

**Key concepts:** GraphicsLayer, RenderEffect, ShaderBrush, AGSL shaders

---

### Level 7 (Advanced Topics)

#### 5. StabilityActivity — Compose Compiler Stability

**Purpose:** Understand how `@Stable`/`@Immutable` affect recomposition.

**Demos:**
- Unstable class demo — a regular `class` parameter causes unnecessary recomposition (visible via `SideEffect` counter)
- `@Stable` annotation — same class, now skippable, recomposition count drops
- `@Immutable` annotation — data class with `List` parameter, show how wrapping in `kotlinx.collections.immutable` fixes stability
- Compose Compiler Metrics — instructions on how to generate and read stability report (`-PcomposeCompilerReports=true`)
- `@Poko` annotation (Kotlin 2.0+) — generate stable equals/hashCode without data class overhead

**Key concepts:** stability, skippability, Compose Compiler metrics, @Stable, @Immutable

#### 6. AnnotatedStringActivity — Rich Text & Inline Styling

**Purpose:** Build styled, interactive text with AnnotatedString.

**Demos:**
- `AnnotatedString.Builder` — apply `SpanStyle` (bold, italic, color, fontSize) to substrings
- `ParagraphStyle` — mixed alignment and line height within one text block
- Clickable annotation — tag a substring with `StringAnnotation`, handle click via `onClick` callback + `UriHandler`
- Real-world: terms-of-service text with clickable "Privacy Policy" link and highlighted keywords

**Key concepts:** AnnotatedString, SpanStyle, ParagraphStyle, StringAnnotation, UriHandler

#### 7. ScaffoldAdvancedActivity — BottomSheet, Drawer, Snackbar

**Purpose:** Demonstrate advanced Scaffold patterns.

**Demos:**
- `BottomSheetScaffold` — sheet content with peek height, drag to expand/collapse
- `ModalNavigationDrawer` — drawer items, gestures, scrim color
- `SnackbarHost` + `Snackbar` — custom action button, dismiss behavior, styled snackbar
- FAB + scroll behavior — `TopAppBarScrollBehavior` hide-on-scroll, FAB hide-on-scroll

**Key concepts:** BottomSheetScaffold, ModalNavigationDrawer, SnackbarHost, scroll behavior

#### 8. CustomModifierActivity — Modifier.Node & Custom Drawing

**Purpose:** Build custom Modifiers using the new Modifier.Node API.

**Demos:**
- `Modifier.Node` basics — create a custom `Node` that logs draw events (vs old `composed {}` approach)
- `drawBehind` / `drawWithContent` — custom gradient border around a composable
- `pointerInput` — custom long-press detector with ripple feedback
- Real-world: gradient border Modifier — `Modifier.gradientBorder(colors, width, shape)` reusable modifier
- Comparison: `Modifier.composed {}` vs `Modifier.Node` — performance difference explanation

**Key concepts:** Modifier.Node, drawBehind, pointerInput, custom Modifier patterns

---

## Activity Registration

Each new Activity must be:
1. Registered in `AndroidManifest.xml`
2. Added to `MainActivity`'s demo list (level category)
3. Follow the existing naming convention: `<Topic>Activity`

## File Structure

```
compose-demo/src/main/java/com/peter/compose/demo/
  level3/
    StateRestoreActivity.kt
    SnapshotFlowActivity.kt
  level6/
    InfiniteTransitionActivity.kt
    GraphicsLayerActivity.kt
  level7/
    StabilityActivity.kt
    AnnotatedStringActivity.kt
    ScaffoldAdvancedActivity.kt
    CustomModifierActivity.kt
```

## Dependencies

No new dependencies required. All APIs are available in the existing Compose BOM (2026.03.01):
- `rememberSaveable` — `compose-runtime`
- `snapshotFlow` — `compose-runtime`
- `GraphicsLayer` / `RenderEffect` — `compose-ui`
- `AnnotatedString` — `compose-foundation`
- `BottomSheetScaffold` — `compose-material3`
- `ModalNavigationDrawer` — `compose-material3`
- `Modifier.Node` — `compose-ui`
- `spring()` / `animateDecay` — `compose-animation`

## Testing

- Unit tests for custom `Saver` implementations in `StateRestoreActivity`
- Screenshot tests for visual effects (blur, shader) if test infrastructure supports it
- Manual verification of process death recovery in `StateRestoreActivity`

## Out of Scope

- Wear OS / TV form factors
- KMP / Compose Multiplatform
- Accessibility (semantics) demos — deferred to a future iteration
