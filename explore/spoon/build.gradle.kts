plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    implementation(libs.spoon)
}

tasks.test {
    useJUnitPlatform()
}