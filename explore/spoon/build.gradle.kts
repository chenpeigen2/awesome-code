plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("fr.inria.gforge.spoon:spoon-core:11.2.1")
}

tasks.test {
    useJUnitPlatform()
}