plugins {
    id("java")
    alias(libs.plugins.dokka)

}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jsprit.core)
    implementation(libs.jsprit.analysis)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}