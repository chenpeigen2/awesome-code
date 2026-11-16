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
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    implementation(libs.mongodb.driver.sync)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
tasks.register<JavaExec>("demo") {
    group = "application"
    description = "运行 MongoDB 演示程序（离线，不需要 mongod）"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.mongo.MongoDemo")
}
