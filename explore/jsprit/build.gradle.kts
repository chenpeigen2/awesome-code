plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
// https://mvnrepository.com/artifact/com.graphhopper/jsprit-core
    implementation("com.graphhopper:jsprit-core:1.9.0-beta.12")
    implementation("com.graphhopper:jsprit-analysis:1.9.0-beta.12")
//    implementation("com.graphhopper:jsprit-io:1.9.0-beta.12")
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}