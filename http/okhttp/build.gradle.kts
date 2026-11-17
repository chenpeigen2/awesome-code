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
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockwebserver3)
    implementation(libs.okhttp)
    implementation(libs.guava)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register<JavaExec>("demo") {
    group = "application"
    description = "起本地 HTTP 服务并发一次请求（离线）"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.http.HttpDemo")
}