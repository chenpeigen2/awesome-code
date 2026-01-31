plugins {
    id("java")
    kotlin("jvm")
    kotlin("kapt")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.dagger:dagger:2.59")
    annotationProcessor("com.google.dagger:dagger-compiler:2.59")
}

tasks.test {
    useJUnitPlatform()
}