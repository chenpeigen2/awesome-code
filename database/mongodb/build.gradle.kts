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
    // https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync
    implementation("org.mongodb:mongodb-driver-sync:5.2.1")
}

tasks.test {
    useJUnitPlatform()
}