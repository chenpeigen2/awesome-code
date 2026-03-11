# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a multi-module Android project focused on learning and demonstrating AndroidX libraries and Android internals. It contains reusable library modules alongside demo applications showcasing various Android concepts.

## Build Commands

```bash
# Build the entire project
./gradlew build

# Build a specific module
./gradlew :app:build
./gradlew :datastore:build

# Install a specific app module on connected device
./gradlew :app:installDebug
./gradlew :window-demo:installDebug

# Run unit tests for a specific module
./gradlew :datastore:test

# Run Android instrumented tests for a specific module
./gradlew :app:connectedAndroidTest

# Clean build
./gradlew clean
```

## Project Structure

### Library Modules
- **`dynamicanimation`** - Custom animation library with press animations and interpolators
- **`datastore`** - DataStore wrapper with JSON serialization and transaction support
- **`apt-annotation`** / **`apt-compiler`** - Annotation processing for view binding (similar to ButterKnife pattern)
- **`aidl-common`** - Shared AIDL interfaces for IPC demos
- **`autodensity`** - Screen density adaptation library for multi-device UI consistency

### Application Modules (Demos)
- **`app`** - Main app using custom APT plugins and lifecycle components
- **`window-demo`** - Android Window/ViewRootImpl internals demo
- **`lifecycle-demo`** - Lifecycle, ViewModel, LiveData, Flow demos
- **`recyclerview-demo`** - RecyclerView patterns, nested scrolling, diff util
- **`constraintlayout-demo`** - ConstraintLayout usage patterns
- **`workmanager-demo`** - WorkManager chains, constraints, periodic work
- **`layoutinflater-demo`** - LayoutInflater customization
- **`context-demo`** - Android Context exploration
- **`datastore-demo`** - DataStore usage examples
- **`koin`** - Koin dependency injection demo
- **`coroutine-demo`** - Kotlin coroutines and Flow examples
- **`animation-demo`** - Android animation examples
- **`touch-demo`** - Touch event dispatch examples
- **`compose-demo`** - Jetpack Compose learning examples
- **`notification-demo`** - Android notification system examples
- **`components-demo`** - Android four major components examples
- **`autodensity-demo`** - Screen density adaptation demo
- **`aidl_server`** / **`aidl_client`** - AIDL IPC client-server demo
- **`appdisplayapp`** / **`apprenderapp`** - Multi-process rendering demo

### Build Logic
- **`build-logic/convention`** - Custom Gradle plugins using ASM for bytecode instrumentation:
  - `MethodStatsPlugin` - Logs method calls at runtime
  - `OnClickPlugin` - Tracks click events

### Documentation
- **`docs/plans/`** - Design documents and implementation plans for demo modules

## Configuration

- **SDK Versions**: compileSdk=36, targetSdk=36, minSdk=33
- **Java**: VERSION_11 (source and target compatibility)
- **Kotlin**: JVM target JVM_11
- **Version Catalog**: `gradle/libs.versions.toml` manages all dependency versions

## Custom Gradle Plugins

The `build-logic` module provides custom plugins applied via ID:
```kotlin
// In module's build.gradle.kts
id("com.peter.androidx.methodstats")  // Method call logging
id("com.peter.androidx.methodclick")  // Click event tracking
```

These use the Android Gradle Plugin's instrumentation API with ASM to transform bytecode at build time.

## APT (Annotation Processing)

The project includes a custom annotation processor:
- `@Bindable` - Marks a class for binding class generation
- `@BindView` - View binding annotation

Configure annotation processor options in build.gradle.kts:
```kotlin
javaCompileOptions {
    annotationProcessorOptions {
        arguments(
            mapOf(
                "apt.debug" to "true",
                "apt.logLevel" to "2"
            )
        )
    }
}
```
