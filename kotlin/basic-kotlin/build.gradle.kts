plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.metadata)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okio)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}