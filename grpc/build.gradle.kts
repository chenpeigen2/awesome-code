import com.google.protobuf.gradle.id

plugins {
    id("java")
    alias(libs.plugins.protobuf)
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    compileOnly(libs.tomcat.annotations)
    implementation(libs.protobuf.java.util)
    runtimeOnly(libs.grpc.netty.shaded)
    testImplementation(libs.grpc.testing)
    testImplementation(libs.mockito.core)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().all {
            plugins {
                id("grpc")
            }
        }
    }

    plugins {
        id("grpc") {
            artifact = libs.grpc.protoc.gen.get().toString()
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}