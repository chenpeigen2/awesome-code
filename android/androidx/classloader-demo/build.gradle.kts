plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.peter.classloader.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkMinor = libs.versions.compileSdkMinor.get().toInt()

    defaultConfig {
        applicationId = "com.peter.classloader.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

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

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Plugin module - 用于动态加载演示
    // 只在编译时需要接口定义，运行时通过 DexClassLoader 加载
    compileOnly(project(":plugin-module"))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// 将 plugin-module 打包成 dex 并放入 assets
val preparePluginDex by tasks.registering {
    group = "build"
    description = "Package plugin-module as dex for dynamic loading"
    dependsOn(":plugin-module:compileDebugKotlin", ":plugin-module:bundleLibCompileToJarDebug")

    doLast {
        val assetsDir = file("src/main/assets")
        val outputDex = File(assetsDir, "plugin.dex")

        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        val pluginJar = file("../plugin-module/build/intermediates/compile_library_classes_jar/debug/bundleLibCompileToJarDebug/classes.jar")

        val androidSdkPath = System.getenv("ANDROID_SDK_ROOT")
            ?: System.getenv("ANDROID_HOME")
            ?: "${System.getProperty("user.home")}/Android/Sdk"
        val buildToolsDir = File(androidSdkPath, "build-tools")
        val latestBuildTools = buildToolsDir.listFiles()
            ?.filter { it.isDirectory && File(it, "d8").exists() }
            ?.maxByOrNull { it.name }

        if (latestBuildTools != null && pluginJar.exists()) {
            val d8Path = File(latestBuildTools, "d8")

            val process = ProcessBuilder(
                d8Path.absolutePath, "--output", assetsDir.absolutePath, pluginJar.absolutePath
            ).redirectErrorStream(true).start()
            process.inputStream.bufferedReader().forEachLine { logger.lifecycle(it) }
            process.waitFor()

            val classesDex = File(assetsDir, "classes.dex")
            if (classesDex.exists() && classesDex.name != outputDex.name) {
                classesDex.renameTo(outputDex)
            }

            println("Plugin dex created at: ${outputDex.absolutePath}")
            println("   Source JAR: ${pluginJar.absolutePath}")
            println("   Size: ${outputDex.length()} bytes")
        } else {
            logger.warn("Could not create plugin.dex")
            logger.warn("   build-tools found: ${latestBuildTools?.absolutePath ?: "no"}")
            logger.warn("   plugin.jar exists: ${pluginJar.exists()}")
            logger.warn("   plugin.jar path: ${pluginJar.absolutePath}")
        }
    }
}

androidComponents {
    onVariants { variant ->
        val variantName = variant.name.replaceFirstChar { it.uppercase() }
        try {
            tasks.named("merge${variantName}Assets").configure {
                dependsOn(preparePluginDex)
            }
        } catch (_: Exception) {}
    }
}
