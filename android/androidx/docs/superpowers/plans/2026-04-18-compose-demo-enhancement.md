# Compose Demo Enhancement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expand compose-demo from 20 activities (5 levels) to 31 activities (7 levels), covering full Compose capabilities including Material3 components, Navigation, Paging, Shared Element Transition, adaptive layout, and more.

**Architecture:** Each Activity is a standalone `ComponentActivity` with `setContent { MaterialTheme { Scaffold { ... } } }` pattern. All activities registered in AndroidManifest.xml. MainActivity.kt serves as the navigation hub with a LazyColumn listing all demos grouped by level.

**Tech Stack:** Jetpack Compose (BOM 2026.03.01), Material3, Navigation Compose, Paging 3, Material3 Window Size Class, Compose UI Test

---

## File Structure

```
compose-demo/src/main/
├── AndroidManifest.xml (modify - add 11 new activities)
├── java/com/peter/compose/demo/
│   ├── MainActivity.kt (modify - restructure to 7 levels)
│   ├── level1/
│   │   ├── PreviewActivity.kt (keep)
│   │   ├── BasicComponentsActivity.kt (keep)
│   │   ├── Material3ComponentsActivity.kt (create)
│   │   └── LayoutComponentsActivity.kt (keep)
│   ├── level2/
│   │   ├── ModifiersActivity.kt (keep)
│   │   ├── ElevationActivity.kt (keep)
│   │   ├── DynamicThemeActivity.kt (create)
│   │   └── TextFieldAdvancedActivity.kt (create)
│   ├── level3/
│   │   ├── StateBasicsActivity.kt (keep)
│   │   ├── StateHoistingActivity.kt (keep)
│   │   ├── SideEffectsActivity.kt (keep)
│   │   └── GesturesActivity.kt (modify - add advanced gestures)
│   ├── level4/
│   │   ├── ComposeNavigationActivity.kt (create)
│   │   ├── ViewModelIntegrationActivity.kt (keep)
│   │   ├── FlowLiveDataActivity.kt (keep)
│   │   ├── MviPatternActivity.kt (keep)
│   │   └── DependencyInjectionActivity.kt (keep)
│   ├── level5/
│   │   ├── LazyListsActivity.kt (keep)
│   │   ├── PagingActivity.kt (create)
│   │   ├── PullToRefreshActivity.kt (create)
│   │   └── CompositionLocalActivity.kt (keep)
│   ├── level6/
│   │   ├── AnimationsActivity.kt (keep)
│   │   ├── ContentTransformActivity.kt (keep)
│   │   ├── SharedElementTransitionActivity.kt (create)
│   │   └── CanvasDrawingActivity.kt (keep)
│   └── level7/
│       ├── CustomLayoutActivity.kt (keep)
│       ├── AdaptiveLayoutActivity.kt (create)
│       ├── DragAndDropActivity.kt (create)
│       ├── PerformanceActivity.kt (keep)
│       ├── InteropActivity.kt (keep)
│       └── ComposeTestingActivity.kt (create)
```

---

### Task 1: Update Dependencies

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `compose-demo/build.gradle.kts`

- [ ] **Step 1: Add navigation and paging versions to libs.versions.toml**

In the `[versions]` section, add after the `splashscreen` line:

```toml
# Navigation 版本
navigation = "2.9.0"

# Paging 版本
paging = "3.3.6"
```

In the `[libraries]` section, add after the `koin-compose` line:

```toml
# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }

# Paging 3
androidx-paging-runtime = { group = "androidx.paging", name = "paging-runtime", version.ref = "paging" }
androidx-paging-compose = { group = "androidx.paging", name = "paging-compose", version.ref = "paging" }
```

- [ ] **Step 2: Add dependencies to compose-demo/build.gradle.kts**

In the `dependencies` block, add after the existing Coroutines block:

```kotlin
// Navigation
implementation(libs.androidx.navigation.compose)

// Paging 3
implementation(libs.androidx.paging.runtime)
implementation(libs.androidx.paging.compose)

// Adaptive / Window Size
implementation("androidx.compose.material3:material3-window-size-class")

// Compose Testing
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
debugImplementation("androidx.compose.ui:ui-test-manifest")
```

- [ ] **Step 3: Verify build sync**

Run: `./gradlew :compose-demo:dependencies --configuration implementation 2>&1 | head -50`
Expected: No errors, dependencies resolved.

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml compose-demo/build.gradle.kts
git commit -m "feat(compose-demo): add navigation, paging, adaptive, and testing dependencies"
```

---

### Task 2: Update MainActivity.kt for 7 Levels

**Files:**
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Replace getDemoLevels() function**

Replace the entire `getDemoLevels()` function (lines 88-257) with the new 7-level structure. The function returns `List<DemoLevel>` with these levels:

- Level 1 "基础组件" (color: Color(0xFF6200EE)): PreviewActivity, BasicComponentsActivity, Material3ComponentsActivity, LayoutComponentsActivity
- Level 2 "样式与主题" (color: Color(0xFF795548)): ModifiersActivity, ElevationActivity, DynamicThemeActivity, TextFieldAdvancedActivity
- Level 3 "状态与交互" (color: Color(0xFFE91E63)): StateBasicsActivity, StateHoistingActivity, SideEffectsActivity, GesturesActivity
- Level 4 "导航与架构" (color: Color(0xFF009688)): ComposeNavigationActivity, ViewModelIntegrationActivity, FlowLiveDataActivity, MviPatternActivity, DependencyInjectionActivity
- Level 5 "列表与数据" (color: Color(0xFFFF9800)): LazyListsActivity, PagingActivity, PullToRefreshActivity, CompositionLocalActivity
- Level 6 "动画与图形" (color: Color(0xFF3F51B5)): AnimationsActivity, ContentTransformActivity, SharedElementTransitionActivity, CanvasDrawingActivity
- Level 7 "高级进阶" (color: Color(0xFF455A64)): CustomLayoutActivity, AdaptiveLayoutActivity, DragAndDropActivity, PerformanceActivity, InteropActivity, ComposeTestingActivity

Also update the subtitle text from "5 个层级 · 20 个示例" to "7 个层级 · 31 个示例".

- [ ] **Step 2: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt
git commit -m "feat(compose-demo): restructure MainActivity to 7 levels with 31 activities"
```

---

### Task 3: Update AndroidManifest.xml

**Files:**
- Modify: `compose-demo/src/main/AndroidManifest.xml`

- [ ] **Step 1: Add 11 new activity declarations**

Add these entries in the `<application>` block, grouped by level:

```xml
<!-- Level 1 - 基础组件 -->
<activity
    android:name=".level1.Material3ComponentsActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />

<!-- Level 2 - 样式与主题 -->
<activity
    android:name=".level2.DynamicThemeActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />
<activity
    android:name=".level2.TextFieldAdvancedActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />

<!-- Level 4 - 导航与架构 -->
<activity
    android:name=".level4.ComposeNavigationActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />

<!-- Level 5 - 列表与数据 -->
<activity
    android:name=".level5.PagingActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />
<activity
    android:name=".level5.PullToRefreshActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />

<!-- Level 6 - 动画与图形 -->
<activity
    android:name=".level6.SharedElementTransitionActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />

<!-- Level 7 - 高级进阶 -->
<activity
    android:name=".level7.AdaptiveLayoutActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />
<activity
    android:name=".level7.DragAndDropActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />
<activity
    android:name=".level7.ComposeTestingActivity"
    android:exported="false"
    android:theme="@style/Theme.Compose" />
```

- [ ] **Step 2: Commit**

```bash
git add compose-demo/src/main/AndroidManifest.xml
git commit -m "feat(compose-demo): register 11 new activities in manifest"
```

---

### Task 4: Create Material3ComponentsActivity (Level 1)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level1/Material3ComponentsActivity.kt`

- [ ] **Step 1: Create the activity file**

Create a `ComponentActivity` following the established pattern (enableEdgeToEdge, setContent, MaterialTheme, Scaffold). The main composable `Material3ComponentsScreen` uses a scrollable Column with sections demonstrating:

1. **TopAppBar** - CenterAlignedTopAppBar, `TopAppBarDefaults` colors and scroll behavior
2. **NavigationBar** - NavigationBarItem with icons, selected state
3. **NavigationDrawer** - DismissibleNavigationDrawer with drawer content
4. **BottomSheetScaffold** - standardBottomSheetState, sheetContent, sheetPeekHeight
5. **Chips** - AssistChip, FilterChip, InputChip, SuggestionChip with icons and checked state
6. **Slider** - Slider, RangeSlider with value display
7. **Switch/RadioButton/Checkbox** - Toggleable components with state
8. **ProgressIndicator** - LinearProgressIndicator, CircularProgressIndicator with determinate/indeterminate modes
9. **Snackbar** - SnackbarHostState, launchSnackbar
10. **DatePicker/TimePicker** - DatePickerDialog, TimePicker with state
11. **Dialog** - AlertDialog, Dialog with custom content
12. **FAB** - FloatingActionButton, ExtendedFloatingActionButton
13. **Tooltip** - PlainTooltip, RichTooltip with TooltipBox
14. **ExposedDropdownMenu** - ExposedDropdownMenuBox with expandable list

Use `@OptIn(ExperimentalMaterial3Api::class)` for experimental APIs. Each section is a separate composable function with a title Text and the component demo. Target ~800 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level1/Material3ComponentsActivity.kt
git commit -m "feat(compose-demo): add Material3 components demo (L1)"
```

---

### Task 5: Create DynamicThemeActivity (Level 2)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level2/DynamicThemeActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable `DynamicThemeScreen` demonstrates:

1. **DynamicColor** - `dynamicColor()` check, isSystemInDarkTheme(), applying dynamic ColorScheme on Android 12+
2. **Custom ColorScheme** - `lightColorScheme()` / `darkColorScheme()` with custom primary/secondary/tertiary/surface colors
3. **Typography System** - Creating custom `Typography()` with different font sizes, weights, letter spacing; applying via MaterialTheme
4. **Shape System** - `Shapes()` with custom CornerBasedShape for small/medium/large
5. **Theme Switching** - A toggle to switch between light/dark/custom themes, wrapping content in different MaterialTheme configurations
6. **Custom Theme Function** - Encapsulating theme logic into a reusable `@Composable fun AppTheme(darkTheme: Boolean, dynamicColor: Boolean, content: @Composable () -> Unit)` function

Each section shows the theme being applied to Material3 components (cards, buttons, text) so the visual difference is clear. Target ~500 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level2/DynamicThemeActivity.kt
git commit -m "feat(compose-demo): add dynamic theme demo (L2)"
```

---

### Task 6: Create TextFieldAdvancedActivity (Level 2)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level2/TextFieldAdvancedActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable `TextFieldAdvancedScreen` demonstrates:

1. **TextField vs OutlinedTextField** - Side-by-side comparison with different configurations
2. **BasicTextField with Decoration** - `BasicTextField` with `TextFieldDefaults.decorator()` for custom styled text input
3. **KeyboardOptions/Actions** - `KeyboardOptions(keyboardType, imeAction, capitalization)`, `KeyboardActions(onDone, onSearch, onGo)`
4. **Text Selection** - `SelectionContainer` / `SelectableGroup`, programmatic selection
5. **ClickableText** - `ClickableText` with `AnnotatedString` and `PressGestureDetector` for inline links
6. **Password Input** - VisualTransformation.PasswordToggle, custom VisualTransformation
7. **Search Box** - Search bar pattern with filtering, trailing icon, clear button
8. **Input Validation** - Real-time validation with errorText, isError, character count

Target ~600 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level2/TextFieldAdvancedActivity.kt
git commit -m "feat(compose-demo): add TextField advanced usage demo (L2)"
```

---

### Task 7: Enhance GesturesActivity (Level 3)

**Files:**
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/level3/GesturesActivity.kt`

- [ ] **Step 1: Add advanced gesture sections**

Append new sections to the existing `GesturesScreen` composable (before the closing of the main Column). Add imports for `detectTransformableGestures`, `transformable`, `scrollable`, `ScrollableState`, `Orientation`. New sections:

1. **Transformable Gestures** - `Modifier.pointerInput` with `detectTransformableGestures` for pinch-to-zoom and rotation on an image/box, displaying scale/rotation/offset values
2. **Multi-touch Tracking** - `pointerInput` with `awaitPointerEventScope` / `awaitEachGesture` to track multiple active pointers, showing pointer count and positions
3. **Scrollable Modifier** - `Modifier.scrollable` with `ScrollableState` and `Orientation.Horizontal`/`Vertical`, showing scroll offset value, with FlingBehavior
4. **Nested Scrolling** - Nested scroll connection demo with outer/inner scrollable containers showing how nested scroll events propagate

Target: append ~350 lines to existing ~550 lines = ~900 lines total.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level3/GesturesActivity.kt
git commit -m "feat(compose-demo): enhance GesturesActivity with transformable, multi-touch, scrollable (L3)"
```

---

### Task 8: Create ComposeNavigationActivity (Level 4)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level4/ComposeNavigationActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable uses a `NavHost` with a bottom navigation bar. Demonstrates:

1. **NavHost + NavGraph** - Define `rememberNavController()`, `NavHost(navController, startDestination)`, composable destinations
2. **Navigation Parameters** - Pass basic types (String, Int) via route patterns like `"detail/{itemId}"`, retrieve with `backStackEntry.arguments`
3. **Optional Parameters** - Route with query parameters `"?title={title}"` with default values using `navArgument`
4. **DeepLink** - Register deep links with `navDeepLink { uriPattern }`, launch via adb
5. **Nested NavGraph** - `navigation(startDestination, route)` for grouping related destinations
6. **Bottom Navigation** - `NavigationBar` + `NavigationBarItem` synced with `navController.currentBackStackEntryAsState()`
7. **NavBackStackEntry** - Access previous back stack entries, `navController.previousBackStackEntry`, sharing data via `SavedStateHandle`
8. **Animated Transitions** - `AnimatedNavHost` (or custom `enterTransition`/`exitTransition` on NavHost) with slide/fade effects

Define 4-5 simple composable destinations (Home, Profile, Settings, Detail) within the same file. Target ~700 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level4/ComposeNavigationActivity.kt
git commit -m "feat(compose-demo): add Compose Navigation demo (L4)"
```

---

### Task 9: Create PagingActivity (Level 5)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level5/PagingActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable `PagingScreen` demonstrates:

1. **PagingSource** - Custom `PagingSource<Int, String>` that generates mock data pages with simulated delay, `LoadResult.Page`
2. **Pager Configuration** - `Pager(PagingConfig(pageSize, enablePlaceholders, initialLoadSize)) { pagingSource }`
3. **collectAsLazyPagingItems** - Collect `Flow<PagingData>` as `LazyPagingItems` in composable
4. **LazyColumn Integration** - `items(count = pagingItems.itemCount) { pagingItems[it] }` with loading placeholder items
5. **LoadState Handling** - `pagingItems.loadState` (Loading, NotLoading, Error), `loadState.append` / `loadState.refresh`, displaying loading spinner and error with retry button
6. **LoadStateAdapter Equivalent** - `item { }` blocks for header (refresh loading) and footer (append loading) states in LazyColumn
7. **Error & Retry** - `pagingItems.retry()` on error state, displaying error message with retry button

All within a single file - define a ViewModel with `Pager` + `PagingSource` as inner classes. Target ~600 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level5/PagingActivity.kt
git commit -m "feat(compose-demo): add Paging 3 integration demo (L5)"
```

---

### Task 10: Create PullToRefreshActivity (Level 5)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level5/PullToRefreshActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable `PullToRefreshScreen` demonstrates:

1. **PullToRefreshBox Basic** - `PullToRefreshBox(isRefreshing, onRefresh)` wrapping a LazyColumn, with `rememberPullToRefreshState()`
2. **Refresh State Management** - `isRefreshing` state, simulating network request with `LaunchedEffect` + `delay`, auto-set `isRefreshing = false` when done
3. **PullToRefreshState** - `PullToRefreshState` distance threshold, progress indicator customization
4. **Custom Indicator** - Customizing the refresh indicator appearance (color, shape, size)
5. **Refresh + Data Update** - Pull-to-refresh triggers new data load, items update after refresh completes with animation
6. **Nested Scroll** - PullToRefreshBox with nested scrollable content (LazyColumn + sticky header)

Use `@OptIn(ExperimentalMaterial3Api::class)` for PullToRefresh APIs. Target ~500 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level5/PullToRefreshActivity.kt
git commit -m "feat(compose-demo): add Pull-to-Refresh demo (L5)"
```

---

### Task 11: Create SharedElementTransitionActivity (Level 6)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level6/SharedElementTransitionActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Uses `SharedTransitionLayout` and `SharedTransitionScope`. Main composable demonstrates:

1. **SharedTransitionLayout** - Wrapping content with `SharedTransitionLayout` providing `SharedTransitionScope` and `AnimatedVisibilityScope`
2. **sharedElement Modifier** - `Modifier.sharedElement(rememberSharedContentState(key), animatedVisibilityScope)` for image/shape element sharing between two states
3. **sharedBounds Modifier** - `Modifier.sharedBounds(rememberSharedContentState(key), animatedVisibilityScope)` for sharing bounds (container size/position) between elements
4. **List to Detail** - Grid of cards → tap → detail view with shared image and text transition
5. **Text Morphing** - sharedBounds on Text composables showing text size/style morphing during transition
6. **Custom Animations** - `sharedElement` with `renderInSharedContentScope`, `clipInOverlayDuringTransition`, custom `fadeIn`/`fadeOut`/`slideIn`/`slideOut` spec

The activity maintains state to toggle between "list" and "detail" views. Use `AnimatedVisibility` with `SharedTransitionScope`. Target ~600 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level6/SharedElementTransitionActivity.kt
git commit -m "feat(compose-demo): add Shared Element Transition demo (L6)"
```

---

### Task 12: Create AdaptiveLayoutActivity (Level 7)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/AdaptiveLayoutActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable `AdaptiveLayoutScreen` demonstrates:

1. **calculateWindowSizeClass** - `calculateWindowSizeClass(activity)` to get `WindowWidthSizeClass` and `WindowHeightSizeClass`
2. **Compact Layout** - `WindowWidthSizeClass.Compact` (phone): vertical layout with NavigationBar
3. **Medium Layout** - `WindowWidthSizeClass.Medium` (tablet portrait): side-by-side with NavigationRail
4. **Expanded Layout** - `WindowWidthSizeClass.Expanded` (tablet landscape / desktop): permanent navigation drawer + two-pane layout
5. **Adaptive Content** - Same data rendered differently based on size class: list vs grid vs list+detail
6. **Responsive Scaffold** - Switching between NavigationBar/NavigationRail/PermanentDrawer based on width size class

Use `import androidx.compose.material3.windowsizeclass.*`. Show the current size class value on screen for educational purposes. Target ~500 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level7/AdaptiveLayoutActivity.kt
git commit -m "feat(compose-demo): add adaptive layout with WindowSizeClass demo (L7)"
```

---

### Task 13: Create DragAndDropActivity (Level 7)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/DragAndDropActivity.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. Main composable `DragAndDropScreen` demonstrates:

1. **Basic Drag** - `Modifier.draggable` with `rememberDraggableState` for horizontal/vertical drag with offset tracking
2. **List Reorder** - LazyColumn with drag handles: track `itemPositions` state, swap items on drag completion, `animateItemPlacement()` for smooth reorder animation
3. **Drag Visual Feedback** - Shadow/elevation on dragged item, placeholder at original position, color change during drag
4. **Grid Reorder** - LazyVerticalGrid drag-to-reorder showing 2D position calculation and swapping
5. **Long Press Trigger** - `Modifier.combinedClickable(onLongClick = ...)` to initiate drag mode, preventing accidental drags
6. **Drop Target** - `Modifier.dropTarget` (or custom pointerInput) to highlight valid drop zones

Implement list reorder using `LazyColumn` + `itemsIndexed` + manual offset calculation with `Modifier.offset` driven by `pointerInput` detecting drag gestures. Target ~550 lines.

- [ ] **Step 2: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level7/DragAndDropActivity.kt
git commit -m "feat(compose-demo): add drag-and-drop reorder demo (L7)"
```

---

### Task 14: Create ComposeTestingActivity (Level 7)

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/ComposeTestingActivity.kt`
- Create: `compose-demo/src/androidTest/java/com/peter/compose/demo/ComposeTestingDemoTest.kt`

- [ ] **Step 1: Create the activity file**

Following the established pattern. This activity is unique — it displays a **reference UI** alongside **test result output**. Main composable `ComposeTestingScreen` shows:

1. **Testing Overview** - Text explaining Compose testing principles: semantics tree, finders, assertions, actions
2. **Demo Counter** - A simple counter (button + text) used as the test target
3. **Demo Todo List** - A simple todo (text field + add button + list) used as the test target
4. **Test Code Display** - Cards showing the test code that tests the above UI components
5. **Run Tests Button** - Button that explains "run tests via ./gradlew :compose-demo:connectedAndroidTest"

The activity serves as a visual guide. Actual tests live in the androidTest file. Target ~500 lines.

- [ ] **Step 2: Create the test file**

Create `compose-demo/src/androidTest/java/com/peter/compose/demo/ComposeTestingDemoTest.kt` with:

```kotlin
package com.peter.compose.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
```

Tests demonstrating:
1. `createComposeRule` setup
2. `onNodeWithText("Click me").performClick()` - click and verify counter
3. `onNodeWithTag("todoInput").performTextInput("Buy milk")` - text input
4. `onNodeWithText("Add").performClick()` - add button
5. `onNodeWithText("Buy milk").assertIsDisplayed()` - assertion
6. `onNodeWithTag("todoInput").assertTextEquals("")` - text assertion
7. `assertCountEquals(3)` - list count assertion

Target ~200 lines.

- [ ] **Step 3: Verify build**

Run: `./gradlew :compose-demo:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add compose-demo/src/main/java/com/peter/compose/demo/level7/ComposeTestingActivity.kt compose-demo/src/androidTest/java/com/peter/compose/demo/ComposeTestingDemoTest.kt
git commit -m "feat(compose-demo): add Compose Testing demo with example tests (L7)"
```

---

### Task 15: Final Build Verification

- [ ] **Step 1: Full build**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Verify all activities registered**

Run: `grep -c 'android:name=".level' compose-demo/src/main/AndroidManifest.xml`
Expected: 32 (1 MainActivity + 31 demo activities)

- [ ] **Step 3: Final commit if any fixes needed**

```bash
git add -A compose-demo/
git commit -m "fix(compose-demo): fix build issues after full enhancement"
```
