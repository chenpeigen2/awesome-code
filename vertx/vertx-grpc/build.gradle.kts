import com.google.protobuf.gradle.id

plugins {
    id("java")
    alias(libs.plugins.protobuf)
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
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    compileOnly(libs.tomcat.annotations)
    implementation(libs.protobuf.java.util)
    runtimeOnly(libs.grpc.netty.shaded)
    testImplementation(libs.grpc.testing)
    testImplementation(libs.mockito.core)
    implementation(libs.vertx.grpc.server)
    implementation(libs.vertx.grpc.client)
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
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register<JavaExec>("demo") {
    group = "application"
    description = "启动 Vert.x gRPC 服务端并调用一次"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.peter.vertx.grpc.GreeterDemo")
}