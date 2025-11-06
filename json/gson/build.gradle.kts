plugins {
    id("java")
    kotlin("jvm")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.13.2")
    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20250517")
    // https://mvnrepository.com/artifact/com.google.errorprone/error_prone_core
    implementation("com.google.errorprone:error_prone_core:2.43.0")
}

tasks.test {
    useJUnitPlatform()
}