import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("com.google.protobuf") version "0.9.4"
}

group = "org.peter"
version = "1.0-SNAPSHOT"

val grpcVersion = "1.73.0" // CURRENT_GRPC_VERSION
val protobufVersion = "4.29.5"
val protocVersion = protobufVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/io.grpc/grpc-protobuf
    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
    implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")

    runtimeOnly("io.grpc:grpc-netty-shaded:${grpcVersion}")

    testImplementation("io.grpc:grpc-testing:${grpcVersion}")
    testImplementation("org.mockito:mockito-core:5.14.2")

// https://mvnrepository.com/artifact/io.vertx/vertx-grpc-server
    implementation("io.vertx:vertx-grpc-server:4.5.11")  // Vert.x 5.0.0目前只有候选版本
    implementation("io.vertx:vertx-grpc-client:4.5.11")  // 保持当前稳定版本
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protocVersion}"
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
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
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