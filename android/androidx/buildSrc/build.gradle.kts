plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("MethodStatsPlugin") {
            id = "com.peter.androidx.methodstats"
            implementationClass = "com.peter.androidx.methodstats.MethodStatsPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("com.android.tools.build:gradle:8.7.2")
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-util:9.7.1")
}
