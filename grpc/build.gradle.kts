import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("com.google.protobuf") version "0.9.4"
}

group = "org.peter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val grpcVersion = "1.78.0" // CURRENT_GRPC_VERSION
val protobufVersion = "4.33.4"
val protocVersion = protobufVersion

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
    testImplementation("org.mockito:mockito-core:5.14.2")
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