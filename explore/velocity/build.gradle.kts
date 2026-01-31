plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.apache.velocity/velocity-engine-core
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    // https://mvnrepository.com/artifact/org.apache.velocity/velocity-engine-examples
    implementation("org.apache.velocity:velocity-engine-examples:2.4.1")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}