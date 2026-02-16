plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.okio)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        allWarningsAsErrors.set(false)
    }
}
