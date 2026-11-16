plugins {
    id("java")
    alias(libs.plugins.dokka)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.bundles.asm.all)

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

// 直接跑演示程序：./gradlew :asm:transform:demo
tasks.register<JavaExec>("demo") {
    group = "application"
    description = "运行 asm-commons 转换 演示程序"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.asm.transform.demo.TransformDemo")
}
