plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/io.vertx/vertx-config
    implementation("io.vertx:vertx-config:4.5.0")
    // https://mvnrepository.com/artifact/io.vertx/vertx-config-yaml
    implementation("io.vertx:vertx-config-yaml:4.5.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}