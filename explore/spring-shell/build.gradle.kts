plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.shell:spring-shell-starter")
    developmentOnly(libs.spring.boot.devtools)
    annotationProcessor(libs.spring.boot.configuration.processor)
    // HelloWorld 使用 jakarta.validation.constraints.Size 校验参数
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.shell:spring-shell-dependencies:${libs.versions.spring.shell.get()}")
    }
}

tasks.test {
    useJUnitPlatform()
}
