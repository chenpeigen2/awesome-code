plugins {
    id("java")
    id("groovy")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.apache.groovy/groovy-all
    implementation("org.apache.groovy:groovy-all:4.0.26")
}

tasks.test {
    useJUnitPlatform()
}