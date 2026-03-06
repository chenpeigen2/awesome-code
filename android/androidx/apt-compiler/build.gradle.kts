plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":apt-annotation"))
    implementation(libs.javapoet)
    
    // AutoService - 需要 annotationProcessor 来处理 @AutoService 注解
    implementation(libs.auto.service)
    annotationProcessor(libs.auto.service)
}