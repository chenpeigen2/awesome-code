plugins {
    id("java")
    alias(libs.plugins.dokka)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

// 注解处理器模块：只依赖 JDK 自带的 javax.annotation.processing / javax.lang.model，
// 不需要任何第三方库。作为 processor 被 :compile:apt 使用。
dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// 处理器本身不需要 java 25 之外的特殊配置
java {
    withSourcesJar()
}
