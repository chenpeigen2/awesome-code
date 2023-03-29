plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.ow2.asm/asm
    implementation("org.ow2.asm:asm:9.5")
// https://mvnrepository.com/artifact/org.ow2.asm/asm-commons
    implementation("org.ow2.asm:asm-commons:9.5")
// https://mvnrepository.com/artifact/org.ow2.asm/asm-util
    implementation("org.ow2.asm:asm-util:9.5")
// https://mvnrepository.com/artifact/org.ow2.asm/asm-tree
    implementation("org.ow2.asm:asm-tree:9.5")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:31.1-jre")
}

tasks.test {
    useJUnitPlatform()
}