plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":apt-annotation"))
    implementation("com.squareup:javapoet:1.13.0")
    
    // AutoService - 需要 annotationProcessor 来处理 @AutoService 注解
    implementation("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
}