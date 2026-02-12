plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jsqlparser)
    implementation(platform(libs.junit.bom))
    implementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}