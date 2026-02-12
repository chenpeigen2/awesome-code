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
    implementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    implementation(libs.gson)
    implementation(libs.json.org)
    implementation(libs.error.prone.core)
}

tasks.test {
    useJUnitPlatform()
}