plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20230227")
    // https://mvnrepository.com/artifact/com.google.errorprone/error_prone_core
    implementation("com.google.errorprone:error_prone_core:2.21.1")
}

tasks.test {
    useJUnitPlatform()
}