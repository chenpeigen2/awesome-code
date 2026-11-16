plugins {
    id("java")
    alias(libs.plugins.dokka)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

dependencies {
    // 注解与处理器都来自 :compile:apt-processor
    implementation(project(":compile:apt-processor"))
    annotationProcessor(project(":compile:apt-processor"))

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

// 演示：PersonBuilder 由处理器在编译期生成，运行期直接用它
tasks.register<JavaExec>("demo") {
    group = "application"
    description = "运行注解处理器演示程序"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.apt.demo.AptDemo")
}
