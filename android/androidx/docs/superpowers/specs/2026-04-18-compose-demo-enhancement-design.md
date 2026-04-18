# Compose Demo Enhancement Design

## Overview

Enhance the compose-demo module from 20 activities across 5 levels to 31 activities across 7 levels, covering the full spectrum of Jetpack Compose capabilities.

## Current State

- 5 levels, 20 activities
- Missing: Material3 full components, Navigation, TextField advanced, advanced gestures, Paging 3, Shared Element Transition, Pull-to-Refresh, adaptive layout, dynamic theme, drag-and-drop, Compose Testing

## New Structure

### Level 1 - Basic Components (4 activities)

| Activity | Status | Content |
|----------|--------|---------|
| PreviewActivity | Keep | @Preview, multi-preview, light/dark |
| BasicComponentsActivity | Keep | Text, Button, Image, Icon |
| Material3ComponentsActivity | **New** | TopAppBar, NavigationBar, NavigationDrawer, BottomSheetScaffold, Chip/FilterChip/InputChip, Slider, Switch, RadioButton, Checkbox, Linear/Circular ProgressBar, Snackbar, DatePicker, TimePicker, Dialog, Tooltip, FAB, ExposedDropdownMenu |
| LayoutComponentsActivity | Keep | Column, Row, Box, Card |

### Level 2 - Style & Theme (4 activities)

| Activity | Status | Content |
|----------|--------|---------|
| ModifiersActivity | Keep | padding, size, background, border, clip, shadow, clickable |
| ElevationActivity | Keep | shadow, translationZ, traditional View elevation |
| DynamicThemeActivity | **New** | Material You DynamicColor, custom ColorScheme, Typography system, Shape system, dark/light theme switching, custom Theme function |
| TextFieldAdvancedActivity | **New** | BasicTextField2 (BasicTextField with deco box), TextField/OutlinedTextField comparison, KeyboardOptions/Actions, text selection, AnnotatedString interactive (ClickableText), password input, search box, input validation |

### Level 3 - State & Interaction (4 activities)

| Activity | Status | Content |
|----------|--------|---------|
| StateBasicsActivity | Keep | mutableStateOf, remember, rememberSaveable, mutableStateListOf |
| StateHoistingActivity | Keep | state hoisting pattern, stateless/stateful components |
| SideEffectsActivity | Keep | LaunchedEffect, DisposableEffect, SideEffect, rememberCoroutineScope |
| GesturesActivity | **Enhanced** | Existing: clickable, detectTapGestures, detectDragGestures. New: detectTransformableGestures (pinch-zoom/rotate), multi-touch, pointerInput multi-gesture composition, scrollable |

### Level 4 - Navigation & Architecture (5 activities)

| Activity | Status | Content |
|----------|--------|---------|
| ComposeNavigationActivity | **New** | NavHost + NavGraph, navigation parameters (basic types/Parcelable), optional params & defaults, DeepLink, nested NavGraph, bottom navigation integration, NavBackStackEntry, AnimatedNavHost transitions |
| ViewModelIntegrationActivity | Keep | viewModel(), collectAsStateWithLifecycle |
| FlowLiveDataActivity | Keep | StateFlow, SharedFlow, Cold Flow, LiveData |
| MviPatternActivity | Keep | MVI unidirectional data flow |
| DependencyInjectionActivity | Keep | Koin integration, ViewModel injection |

### Level 5 - Lists & Data (4 activities)

| Activity | Status | Content |
|----------|--------|---------|
| LazyListsActivity | Keep | LazyColumn, LazyRow, LazyVerticalGrid |
| PagingActivity | **New** | Paging 3 basics (Pager/PagingSource/PagingData), collectAsLazyPagingItems, LoadState handling, load more, RemoteMediator, error retry |
| PullToRefreshActivity | **New** | Material3 PullToRefreshState, refresh + LazyColumn, custom indicator, simulated network refresh |
| CompositionLocalActivity | Keep | compositionLocalOf, CompositionLocalProvider, theme switching |

### Level 6 - Animation & Graphics (4 activities)

| Activity | Status | Content |
|----------|--------|---------|
| AnimationsActivity | Keep | animate*AsState, AnimatedVisibility, animateContentSize, rememberInfiniteTransition |
| ContentTransformActivity | Keep | Crossfade, AnimatedContent, slide transitions, SizeTransform |
| SharedElementTransitionActivity | **New** | SharedTransitionLayout/SharedTransitionScope, sharedElement/sharedBounds Modifier, image list-to-detail transition, text transition, custom AnimatedVisibilityScope, Navigation integration |
| CanvasDrawingActivity | Keep | drawRect/Circle/Line/Arc, Brush gradients, Path, touch drawing |

### Level 7 - Advanced (6 activities)

| Activity | Status | Content |
|----------|--------|---------|
| CustomLayoutActivity | Keep | Layout, MeasurePolicy, staggered grid, FlowRow |
| AdaptiveLayoutActivity | **New** | WindowSizeClass, WindowWidthSizeClass (Compact/Medium/Expanded), adaptive navigation, responsive layouts for phone/tablet |
| DragAndDropActivity | **New** | LazyColumn drag-to-reorder, Modifier.draggable list reorder, drag visual feedback (placeholder/shadow), grid drag reorder, long-press trigger |
| PerformanceActivity | Keep | recomposition optimization, derivedStateOf, @Stable/@Immutable, key() |
| InteropActivity | Keep | AndroidView, WebView, two-way binding |
| ComposeTestingActivity | **New** | createComposeRule, onNodeWithText/onNodeWithTag, performClick/performTextInput, assertIsDisplayed/assertTextEquals, semantics tree debugging, state testing, side-effect testing |

## New Dependencies

Add to compose-demo/build.gradle.kts and update version catalog (gradle/libs.versions.toml):

```toml
# In libs.versions.toml - add new version entries:
navigation = "2.9.0"
paging = "3.3.6"

# In libs.versions.toml - add new library entries:
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
androidx-paging-runtime = { group = "androidx.paging", name = "paging-runtime", version.ref = "paging" }
androidx-paging-compose = { group = "androidx.paging", name = "paging-compose", version.ref = "paging" }
```

```kotlin
// In compose-demo/build.gradle.kts:
// Navigation
implementation(libs.androidx.navigation.compose)

// Paging 3
implementation(libs.androidx.paging.runtime)
implementation(libs.androidx.paging.compose)

// Adaptive / Window Size (managed by Compose BOM)
implementation("androidx.compose.material3:material3-window-size-class")

// Compose Testing
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
debugImplementation("androidx.compose.ui:ui-test-manifest")
```

## File Organization

All activities remain in their level packages:
```
src/main/java/com/peter/compose/demo/
├── MainActivity.kt (updated with 7 levels, 31 activities)
├── level1/
│   ├── PreviewActivity.kt
│   ├── BasicComponentsActivity.kt
│   ├── Material3ComponentsActivity.kt  (new)
│   └── LayoutComponentsActivity.kt
├── level2/
│   ├── ModifiersActivity.kt
│   ├── ElevationActivity.kt
│   ├── DynamicThemeActivity.kt  (new)
│   └── TextFieldAdvancedActivity.kt  (new)
├── level3/
│   ├── StateBasicsActivity.kt
│   ├── StateHoistingActivity.kt
│   ├── SideEffectsActivity.kt
│   └── GesturesActivity.kt  (enhanced)
├── level4/
│   ├── ComposeNavigationActivity.kt  (new)
│   ├── ViewModelIntegrationActivity.kt
│   ├── FlowLiveDataActivity.kt
│   ├── MviPatternActivity.kt
│   └── DependencyInjectionActivity.kt
├── level5/
│   ├── LazyListsActivity.kt
│   ├── PagingActivity.kt  (new)
│   ├── PullToRefreshActivity.kt  (new)
│   └── CompositionLocalActivity.kt
├── level6/
│   ├── AnimationsActivity.kt
│   ├── ContentTransformActivity.kt
│   ├── SharedElementTransitionActivity.kt  (new)
│   └── CanvasDrawingActivity.kt
└── level7/
    ├── CustomLayoutActivity.kt
    ├── AdaptiveLayoutActivity.kt  (new)
    ├── DragAndDropActivity.kt  (new)
    ├── PerformanceActivity.kt
    ├── InteropActivity.kt
    └── ComposeTestingActivity.kt  (new)
```

## Implementation Order

1. Update build.gradle.kts with new dependencies
2. Update MainActivity.kt with 7-level navigation
3. Update AndroidManifest.xml with new activities
4. Implement new activities level by level:
   - Level 1: Material3ComponentsActivity
   - Level 2: DynamicThemeActivity, TextFieldAdvancedActivity
   - Level 3: Enhance GesturesActivity
   - Level 4: ComposeNavigationActivity
   - Level 5: PagingActivity, PullToRefreshActivity
   - Level 6: SharedElementTransitionActivity
   - Level 7: AdaptiveLayoutActivity, DragAndDropActivity, ComposeTestingActivity

## Existing Activity Changes

Only GesturesActivity gets enhanced content. All other existing activities remain unchanged. All existing activities stay in their current level packages - only the level grouping in MainActivity.kt changes (from 5 levels to 7 levels).

## Conventions

- Follow existing code style: Chinese comments, single Activity per file, `ComponentActivity` + `setContent`
- Each activity demonstrates 3-8 concepts with interactive examples
- Use MaterialTheme wrapper consistent with existing pattern
- Target ~400-800 lines per new activity
