plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.github.jsqlparser/jsqlparser
    implementation("com.github.jsqlparser:jsqlparser:4.7")
    implementation(platform("org.junit:junit-bom:5.9.1"))
    implementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}