import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("com.google.protobuf") version "0.9.4"
}

group = "org.peter"
version = "1.0-SNAPSHOT"

val grpcVersion = "1.73.0" // CURRENT_GRPC_VERSION
val protobufVersion = "4.33.5"
val protocVersion = protobufVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.14.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/io.grpc/grpc-protobuf
    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
    implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")

    runtimeOnly("io.grpc:grpc-netty-shaded:${grpcVersion}")

    testImplementation("io.grpc:grpc-testing:${grpcVersion}")
    testImplementation("org.mockito:mockito-core:5.21.0")

// https://mvnrepository.com/artifact/io.vertx/vertx-grpc-server
    implementation("io.vertx:vertx-grpc-server:4.5.24")  // 回退到Vert.x 4.5.24稳定版本
    implementation("io.vertx:vertx-grpc-client:4.5.24")  // 回退到Vert.x 4.5.24稳定版本
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