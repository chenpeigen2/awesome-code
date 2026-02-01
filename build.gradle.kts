import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "2.3.0"
    kotlin("kapt") version "2.3.0"
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    // 阿里云镜像
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/gradle-plugin")
    }
    // 腾讯云镜像
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    // 官方仓库作为备用
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // https://mvnrepository.com/artifact/org.openjdk.nashorn/nashorn-core
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
// https://mvnrepository.com/artifact/commons-codec/commons-codec
    implementation("commons-codec:commons-codec:1.20.0")
// https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:33.5.0-jre")
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    // 阿里云镜像
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/gradle-plugin")
    }
    // 腾讯云镜像
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    // 官方仓库作为备用
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // https://mvnrepository.com/artifact/org.openjdk.nashorn/nashorn-core
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
// https://mvnrepository.com/artifact/commons-codec/commons-codec
    implementation("commons-codec:commons-codec:1.20.0")
// https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:33.5.0-jre")
}

tasks.test {
    useJUnitPlatform()
}

allprojects {
    repositories {
        // 阿里云镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/central")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        // 腾讯云镜像
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        }
        // 官方仓库作为备用
        mavenCentral()
        gradlePluginPortal()
    }
    
    tasks.withType<JavaCompile> {
        options.release.set(25)
        options.isIncremental = true
        options.isFork = true
        options.isFailOnError = true
    }

    tasks.withType<KotlinCompile> {
        compilerOptions.freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    }
}