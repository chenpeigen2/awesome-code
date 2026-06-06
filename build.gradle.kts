import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dokka)
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

// Dokka 多模块文档聚合
// 每个子模块已在各自的 build.gradle.kts 中通过 plugins { alias(libs.plugins.dokka) } 应用
// 此处将所有子模块聚合到根项目，生成统一的多模块文档站点
val excludedFromDokka = setOf(
    "native-kotlin",
    "kotlin-spring-coroutines",
    "simple-webflux",
)

dependencies {
    subprojects
        .filter { it.name !in excludedFromDokka }
        .filter { it.file("src").isDirectory }
        .forEach { dokka(it) }
}

dokka {
    dokkaPublications.html {
        moduleName.set("awesome-code")
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}
