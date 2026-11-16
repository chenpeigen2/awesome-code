plugins {
    id("java")
    alias(libs.plugins.dokka)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

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

// 直接跑演示程序：./gradlew :compile:inmemory:demo
tasks.register<JavaExec>("demo") {
    group = "application"
    description = "运行内存编译演示程序"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.inmemory.demo.CompilerDemo")
}
