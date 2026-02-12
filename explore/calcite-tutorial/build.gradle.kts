plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.calcite.example.csv)
    implementation(libs.calcite.elasticsearch)
    implementation(libs.calcite.linq4j)
    implementation(libs.calcite.core)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}