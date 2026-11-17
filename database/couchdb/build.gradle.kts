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
    implementation(libs.gson)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register<JavaExec>("demo") {
    group = "application"
    description = "对真实 CouchDB 跑一遍建库/插文档/查文档（需 COUCHDB_* 环境变量）"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.couchdb.CouchDbDemo")
}