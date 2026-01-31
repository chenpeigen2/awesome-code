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
    // https://mvnrepository.com/artifact/io.vertx/vertx-config
    implementation("io.vertx:vertx-config:4.5.24")
    // https://mvnrepository.com/artifact/io.vertx/vertx-config-yaml
    implementation("io.vertx:vertx-config-yaml:4.5.24")
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}