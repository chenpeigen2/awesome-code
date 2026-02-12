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
    implementation(libs.auto.value)
    implementation(libs.auto.value.annotations)
    annotationProcessor(libs.auto.value.annotations)
}

tasks.test {
    useJUnitPlatform()
}