# file-demo Module Design

## Overview

A demo module showcasing Android file storage options, styled after the notification-demo module with Material 3 design, tab-based navigation, and interactive demonstrations.

## Architecture

### Pattern
- Single Activity + ViewPager2 + TabLayout + 4 Fragments
- Material 3 styling with tab-specific theme colors
- RecyclerView with card items for each demo operation
- ViewBinding enabled

### Tab Structure & Theme Colors

| Tab | Primary Color | Container Color | Content |
|-----|---------------|-----------------|---------|
| Internal | `#6750A4` (Purple) | `#EADDFF` | File I/O in app-private storage |
| External | `#0061A4` (Blue) | `#D1E4FF` | External storage access |
| Preferences | `#386A20` (Green) | `#E8F5E9` | SharedPreferences |
| Scoped | `#B3261E` (Orange) | `#FFDAD6` | MediaStore & SAF |

## Fragment Content

### 1. Internal Fragment
Demonstrates internal storage operations:
- Write text file to internal storage
- Read text file from internal storage
- List files in internal directory
- Delete file
- Write/Read cache file
- Get file size & info

### 2. External Fragment
Demonstrates external storage operations:
- Check external storage availability
- Write to external files directory
- Read from external files directory
- List external files
- Delete external file
- External cache operations

### 3. Preferences Fragment
Demonstrates SharedPreferences:
- Put/Get String, Int, Boolean, Float, Long
- Remove key / Clear all
- Register/Unregister change listener
- Commit vs Apply comparison
- Export/Import preferences to file

### 4. Scoped Fragment
Demonstrates modern storage APIs:
- Pick file via SAF (Storage Access Framework)
- Create document via SAF
- MediaStore: Query images
- MediaStore: Save image to Pictures
- Pick image via Photo Picker (Android 13+)
- Request read/write permissions

## UI Components

### Main Layout
- `CoordinatorLayout` as root
- `AppBarLayout` with status badge and title
- `TabLayout` with scrollable tabs
- `ViewPager2` for fragment swiping

### Card Items
- `MaterialCardView` with 16dp corner radius
- Color dot indicator matching tab theme
- Title (15sp, bold)
- Description (13sp, gray)
- Right arrow indicator

### Permission Badge
- Storage permission status in header
- Green dot + "已授权" / Red dot + "未授权"
- Animated color transitions on tab switch

## File Structure

```
file-demo/
├── build.gradle.kts
├── proguard-rules.pro
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/peter/file/demo/
│   │   ├── MainActivity.kt
│   │   ├── ViewPagerAdapter.kt
│   │   ├── FileItem.kt
│   │   ├── FileAdapter.kt
│   │   ├── FileHelper.kt
│   │   └── fragments/
│   │       ├── InternalFragment.kt
│   │       ├── ExternalFragment.kt
│   │       ├── PreferencesFragment.kt
│   │       └── ScopedFragment.kt
│   └── res/
│       ├── drawable/
│       │   ├── ic_launcher_foreground.xml
│       │   ├── ic_launcher_background.xml
│       │   ├── bg_color_dot.xml
│       │   ├── bg_permission_granted.xml
│       │   ├── bg_permission_denied.xml
│       │   ├── ic_arrow_right.xml
│       │   └── (tab-specific backgrounds)
│       ├── layout/
│       │   ├── activity_main.xml
│       │   ├── fragment_internal.xml
│       │   ├── fragment_external.xml
│       │   ├── fragment_preferences.xml
│       │   ├── fragment_scoped.xml
│       │   └── item_file_operation.xml
│       ├── mipmap-*/
│       │   └── ic_launcher.xml
│       ├── values/
│       │   ├── colors.xml
│       │   ├── themes.xml
│       │   ├── strings.xml
│       │   └── dimens.xml
│       └── xml/
│           ├── backup_rules.xml
│           └── data_extraction_rules.xml
```

## Dependencies

```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
```

## Permissions Required

```xml
<!-- For external storage on older Android -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="29" />

<!-- For media access on Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

## Implementation Notes

1. Follow notification-demo styling exactly for consistency
2. Use coroutines for file operations to avoid blocking UI
3. Show Toast/Snackbar feedback after each operation
4. Display actual file contents/paths in results
5. Handle runtime permissions gracefully
