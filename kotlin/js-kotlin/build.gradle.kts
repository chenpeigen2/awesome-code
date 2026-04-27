plugins {
    kotlin("multiplatform")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser {
            commonWebpackConfig {
                outputFileName = "js-kotlin.js"
            }
        }
        nodejs()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }
    }
}
