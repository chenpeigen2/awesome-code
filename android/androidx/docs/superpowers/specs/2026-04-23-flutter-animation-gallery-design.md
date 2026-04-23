# Flutter Animation Gallery Design

## Overview

Add a Flutter-based animation gallery module to the existing AndroidX project, integrated into the `app` module as a `FlutterFragment`. The gallery showcases 12 Flutter animations across 4 categories, accessible via a new button in `MainActivity`.

## Project Structure

```
androidx/
├── flutter_animation_module/        # Flutter Module (flutter create --template module)
│   ├── lib/
│   │   ├── main.dart                # Entry point, route config
│   │   ├── gallery_home.dart        # Gallery home page (category grid)
│   │   ├── animations/
│   │   │   ├── loading/
│   │   │   │   ├── wave_loading.dart
│   │   │   │   ├── spinning_dots.dart
│   │   │   │   └── bouncing_loader.dart
│   │   │   ├── interaction/
│   │   │   │   ├── press_button.dart
│   │   │   │   ├── expand_collapse.dart
│   │   │   │   └── toggle_switch.dart
│   │   │   ├── particle/
│   │   │   │   ├── ripple_effect.dart
│   │   │   │   ├── fireworks.dart
│   │   │   │   └── snowfall.dart
│   │   │   └── transition/
│   │   │       ├── page_transition.dart
│   │   │       ├── fade_through.dart
│   │   │       └── shared_axis.dart
│   │   └── widgets/
│   │       └── animation_card.dart  # Gallery card widget
│   └── pubspec.yaml
├── app/
│   ├── src/main/
│   │   ├── java/com/peter/androidx/
│   │   │   ├── MainActivity.kt           # Add entry button
│   │   │   └── FlutterAnimationActivity.kt  # New Activity
│   │   ├── res/layout/
│   │   │   ├── activity_main.xml         # Modified
│   │   │   └── activity_flutter_animation.xml  # New
│   │   └── AndroidManifest.xml           # Register new Activity
│   └── build.gradle.kts                  # Add Flutter dependency
└── settings.gradle.kts                   # Include Flutter module
```

## Integration Method

- **Flutter Module** created with `flutter create --template module flutter_animation_module`
- **Android side** uses `FlutterFragment` embedded in a new `FlutterAnimationActivity`
- **Dependency**: app module depends on Flutter Module via Gradle project dependency
- **Navigation**: `MainActivity` button click → `FlutterAnimationActivity` → `FlutterFragment` loads Flutter gallery

## Animation Content

### 1. Loading Animations (3)

#### Wave Loading
3-5 dots moving up/down in a wave pattern with staggered delays. Uses `AnimationController` with `Interval` for stagger effect.

#### Spinning Dots
Dots rotating along a circular path with size gradient. Uses `Transform.rotate` and `AnimationController.repeat()`.

#### Bouncing Loader
3 balls bouncing sequentially with squash/stretch deformation. Uses `Curves.elasticOut` and `Transform.scale`.

### 2. Interaction Animations (3)

#### Press Button
Button shrinks on press with spring-back, releases with scale restore and ripple expansion. Uses `GestureDetector` + `AnimatedScale`.

#### Expand Collapse
Card expands/collapses on tap with height animation and content fade-in. Uses `AnimatedCrossFade` or `AnimatedContainer`.

#### Toggle Switch
Custom switch with sliding ball and background color gradient transition. Uses `AnimatedAlign` + `AnimatedContainer`.

### 3. Particle Effects (3)

#### Ripple Effect
Concentric circles expand from tap position, fading outward. Uses `CustomPainter` with multiple `AnimationController` instances.

#### Fireworks
Tap to launch firework; particles explode from center, fall with gravity, color gradient. Uses `CustomPainter` + particle system.

#### Snowfall
Continuous snowfall with random size/speed/sway. Touch to disturb nearby flakes. Uses `AnimationController.repeat()` + `CustomPainter`.

### 4. Transition Animations (3)

#### Page Transition
Inter-page transitions with slide/zoom/rotation effects. Uses `PageRouteBuilder` with custom `Tween` animations.

#### Fade Through
Material Design FadeThrough: elements fade out then fade in. Uses `AnimatedSwitcher` or custom transition.

#### Shared Axis
Shared axis transition along X/Y/Z axis with slide + fade. Uses `PageTransitionsTheme` or custom `PageRouteBuilder`.

## Gallery Home Page

- Top: title bar "Flutter Animation Gallery"
- Body: 4 category sections, each containing animation cards
- Each card: animation name + small preview or icon
- Tap card → navigate to animation demo page with interactive controls
- Routing via Flutter `Navigator` with named routes:
  - `/` → gallery home
  - `/loading/wave`, `/loading/spinning_dots`, `/loading/bouncing_loader`
  - `/interaction/press`, `/interaction/expand`, `/interaction/toggle`
  - `/particle/ripple`, `/particle/fireworks`, `/particle/snowfall`
  - `/transition/page`, `/transition/fade`, `/transition/shared_axis`

## Android-side Changes

### FlutterAnimationActivity.kt
- Extends `FlutterActivity` or hosts a `FlutterFragment`
- Sets `initialRoute` to `/`
- Full-screen display with translucent status bar

### activity_flutter_animation.xml
- FrameLayout container for FlutterFragment

### activity_main.xml
- Add a "Flutter 动画画廊" button in the existing layout

### AndroidManifest.xml
- Register `FlutterAnimationActivity`

### app/build.gradle.kts
- Add `implementation(project(":flutter_animation_module"))`

### settings.gradle.kts
- Add `setBinding(new Binding([name: "flutter_animation_module"]))` and include Flutter module

## Technical Constraints

- Flutter 3.41.7 (stable channel)
- AGP 9.1.1 compatibility may require Flutter Module Gradle plugin version alignment
- minSdk 33 (Flutter minimum is 21, no conflict)
- No additional Flutter packages needed - all animations use Flutter SDK built-in widgets and CustomPainter
