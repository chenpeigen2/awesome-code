# AutoDensity ProGuard Rules

# Keep all public classes and methods
-keep public class com.peter.autodensity.** { *; }

# Keep reflection-related classes
-keep class android.content.res.ResourcesManager { *; }
-keep class android.content.res.ResourcesKey { *; }
-keep class android.content.res.ResourcesImpl { *; }
-keep class android.app.ResourcesManager { *; }

# Keep IDensity interface implementers
-keep class * implements com.peter.autodensity.IDensity { *; }

# Keep SystemProperties reflection
-keep class android.os.SystemProperties { *; }

# Keep Display related
-keep class android.view.Display { *; }
-keep class android.hardware.display.DisplayManager { *; }

# Keep Configuration related
-keep class android.content.res.Configuration { *; }
-keep class android.content.res.DisplayMetrics { *; }
