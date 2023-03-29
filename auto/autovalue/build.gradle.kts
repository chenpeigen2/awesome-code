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
    // https://mvnrepository.com/artifact/com.google.auto.value/auto-value
    implementation("com.google.auto.value:auto-value:1.10.1")
// https://mvnrepository.com/artifact/com.google.auto.value/auto-value-annotations
    implementation("com.google.auto.value:auto-value-annotations:1.10.1")
    annotationProcessor("com.google.auto.value:auto-value-annotations:1.10.1")
}

tasks.test {
    useJUnitPlatform()
}