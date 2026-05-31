plugins {
    kotlin("multiplatform")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

kotlin {
    linuxX64()
    if (System.getProperty("os.name")?.contains("Mac") == true
        && File("/Applications/Xcode.app").exists()) {
        macosArm64()
    }
    mingwX64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
