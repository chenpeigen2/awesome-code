plugins {
    id("java")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.apache.calcite/calcite-example-csv
    implementation("org.apache.calcite:calcite-example-csv:1.21.0")
// https://mvnrepository.com/artifact/org.apache.calcite/calcite-elasticsearch
    implementation("org.apache.calcite:calcite-elasticsearch:1.39.0")
    // https://mvnrepository.com/artifact/org.apache.calcite/calcite-linq4j
    implementation("org.apache.calcite:calcite-linq4j:1.36.0")
    // https://mvnrepository.com/artifact/org.apache.calcite/calcite-core
    implementation("org.apache.calcite:calcite-core:1.36.0")
// https://mvnrepository.com/artifact/org.apache.calcite/calcite-linq4j
    implementation("org.apache.calcite:calcite-linq4j:1.36.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}