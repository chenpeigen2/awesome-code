plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
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
android.applicationVariants.all {
    val variantName = name.capitalize()
    
    tasks.register("preparePluginDex${variantName}") {
        group = "build"
        description = "Package plugin-module as dex for dynamic loading"
        
        // 依赖 plugin-module 的编译任务
        dependsOn(":plugin-module:compileDebugKotlin", ":plugin-module:bundleLibCompileToJarDebug")
        
        doLast {
            val assetsDir = file("src/main/assets")
            val outputDex = File(assetsDir, "plugin.dex")
            
            if (!assetsDir.exists()) {
                assetsDir.mkdirs()
            }
            
            // 使用 plugin-module 编译生成的 jar
            val pluginJar = file("../plugin-module/build/intermediates/compile_library_classes_jar/debug/bundleLibCompileToJarDebug/classes.jar")
            
            // 查找 Android SDK 路径
            val androidSdkPath = System.getenv("ANDROID_SDK_ROOT") 
                ?: System.getenv("ANDROID_HOME")
                ?: "${System.getProperty("user.home")}/Android/Sdk"
            val buildToolsDir = File(androidSdkPath, "build-tools")
            val latestBuildTools = buildToolsDir.listFiles()
                ?.filter { it.isDirectory && File(it, "d8").exists() }
                ?.maxByOrNull { it.name }
            
            if (latestBuildTools != null && pluginJar.exists()) {
                val d8Path = File(latestBuildTools, "d8")
                
                // 使用 d8 转换为 dex
                exec {
                    commandLine(d8Path.absolutePath, "--output", assetsDir.absolutePath, pluginJar.absolutePath)
                }
                
                // d8 输出的是 classes.dex，重命名为 plugin.dex
                val classesDex = File(assetsDir, "classes.dex")
                if (classesDex.exists() && classesDex.name != outputDex.name) {
                    classesDex.renameTo(outputDex)
                }
                
                println("✅ Plugin dex created at: ${outputDex.absolutePath}")
                println("   Source JAR: ${pluginJar.absolutePath}")
                println("   Size: ${outputDex.length()} bytes")
            } else {
                println("⚠️ Warning: Could not create plugin.dex")
                println("   build-tools found: ${latestBuildTools?.absolutePath ?: "no"}")
                println("   plugin.jar exists: ${pluginJar.exists()}")
                println("   plugin.jar path: ${pluginJar.absolutePath}")
            }
        }
    }
    
    // 在打包前生成 plugin.dex
    tasks.findByName("merge${variantName}Assets")?.dependsOn("preparePluginDex${variantName}")
}
