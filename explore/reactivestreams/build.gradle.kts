plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-core
    implementation("io.projectreactor:reactor-core:3.6.0")
    // https://mvnrepository.com/artifact/org.reactivestreams/reactive-streams
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}