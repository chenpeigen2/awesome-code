plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.dokka)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.collections.immutable)
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

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("org.peter.immutable.MainKt")
}
