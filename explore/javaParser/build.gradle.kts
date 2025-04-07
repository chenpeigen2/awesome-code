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
    // https://mvnrepository.com/artifact/com.github.javaparser/javaparser-core
    implementation("com.github.javaparser:javaparser-core:3.26.4")
}

tasks.test {
    useJUnitPlatform()
}