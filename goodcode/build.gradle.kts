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
        }
    }
}