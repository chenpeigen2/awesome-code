plugins {
    kotlin("jvm")
}

dependencies {
    // 基础 Kotlin 依赖已由根项目提供
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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