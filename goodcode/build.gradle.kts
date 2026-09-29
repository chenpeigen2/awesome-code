plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

// 为 Kotlin 源文件设置源目录
sourceSets {
    main {
        kotlin {
            srcDirs(".")
            // Kotlin 2.4+ 会编译源目录中的 .kts 脚本，排除 Gradle 构建脚本避免误编译
            exclude("*.gradle.kts")
        }
    }
}