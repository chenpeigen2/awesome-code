import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        // 阿里云镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/central")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        // 腾讯云镜像
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        }
        // 官方仓库作为备用
        mavenCentral()
        gradlePluginPortal()
    }

    tasks.withType<JavaCompile> {
        options.release.set(25)
        options.isIncremental = true
        options.isFork = true
        options.isFailOnError = true
    }

    tasks.withType<KotlinCompile> {
        compilerOptions.freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    }
}

dependencies {
    implementation(libs.nashorn)
    implementation(libs.commons.codec)
    implementation(libs.bouncycastle)
    implementation(libs.guava)
    implementation(libs.iflow.cli.sdk)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}