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
    // https://mvnrepository.com/artifact/io.reactivex.rxjava3/rxjava
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
}

tasks.test {
    useJUnitPlatform()
}