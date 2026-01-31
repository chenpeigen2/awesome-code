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
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.apache.groovy/groovy-all
    implementation("org.apache.groovy:groovy-all:5.0.2")
}

tasks.test {
    useJUnitPlatform()
}