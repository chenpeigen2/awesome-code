plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
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

extra["springShellVersion"] = "3.4.0"

dependencies {
    implementation("org.springframework.shell:spring-shell-starter")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")
    }
}

tasks.test {
    useJUnitPlatform()
}