plugins {
    id("java")
}

group = "org.peter.vertx.web"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/io.vertx/vertx-web
    implementation("io.vertx:vertx-web:5.0.3")
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}