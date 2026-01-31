plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    implementation("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    // https://mvnrepository.com/artifact/com.squareup/javapoet
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.google.auto.service:auto-service-annotations:1.1.1")
}

tasks.test {
    useJUnitPlatform()
}