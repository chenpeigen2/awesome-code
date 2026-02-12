plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    implementation(libs.okhttp)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.guava)
}

tasks.test {
    useJUnitPlatform()
}