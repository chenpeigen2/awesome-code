plugins {
    id("java")
    id("groovy")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    implementation(libs.groovy.all)
}

tasks.test {
    useJUnitPlatform()
}