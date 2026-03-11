plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    application
}

group = "org.peter.vertx.coroutines"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.vertx.web)
    implementation(libs.vertx.lang.kotlin)
    implementation(libs.vertx.lang.kotlin.coroutines)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

application {
    mainClass = "org.peter.vertx.coroutines.MainKt"
}

tasks.test {
    useJUnitPlatform()
}

