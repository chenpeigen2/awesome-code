plugins {
    id("java")
    alias(libs.plugins.dokka)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // AutoValue 的处理器在 auto-value 里，注解在 auto-value-annotations 里。
    // 原来把 annotationProcessor 挂到 annotations 上是错的，处理器根本不会跑。
    compileOnly(libs.auto.value.annotations)
    annotationProcessor(libs.auto.value)

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

tasks.register<JavaExec>("demo") {
    group = "application"
    description = "运行 AutoValue 演示程序"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.autovalue.AutoValueDemo")
}
