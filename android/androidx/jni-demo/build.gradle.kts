import org.gradle.api.tasks.Exec

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.peter.jni.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkMinor = libs.versions.compileSdkMinor.get().toInt()

    defaultConfig {
        applicationId = "com.peter.jni.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
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

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    ndkVersion = "27.2.12479018"

    sourceSets {
        getByName("main") {
            jniLibs.srcDir(file("${project.layout.buildDirectory.get().asFile}/rustJniLibs"))
        }
    }
}

// Rust JNI 编译任务
val buildRustRelease by tasks.registering(Exec::class) {
    description = "Build Rust JNI library for Android (release)"
    group = "rust"

    workingDir = file("${project.projectDir}/rust")
    commandLine("cargo", "ndk",
        "--manifest-path", "Cargo.toml",
        "-t", "arm64-v8a",
        "-t", "x86_64",
        "-o", "${project.layout.buildDirectory.get().asFile}/rustJniLibs",
        "build", "--release"
    )
}

val buildRustDebug by tasks.registering(Exec::class) {
    description = "Build Rust JNI library for Android (debug)"
    group = "rust"

    workingDir = file("${project.projectDir}/rust")
    commandLine("cargo", "ndk",
        "--manifest-path", "Cargo.toml",
        "-t", "arm64-v8a",
        "-t", "x86_64",
        "-o", "${project.layout.buildDirectory.get().asFile}/rustJniLibs",
        "build"
    )
}

afterEvaluate {
    tasks.matching { it.name.contains("mergeDebugJniLibFolders") }.configureEach {
        dependsOn(buildRustDebug)
    }
    tasks.matching { it.name.contains("mergeReleaseJniLibFolders") }.configureEach {
        dependsOn(buildRustRelease)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
