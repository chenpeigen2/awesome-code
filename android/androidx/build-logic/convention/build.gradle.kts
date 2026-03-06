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
        create("MethodClickPlugin") {
            id = "com.peter.androidx.methodclick"
            implementationClass = "com.peter.androidx.methodstats.OnClickPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("com.android.tools.build:gradle:8.13.2")
    implementation("org.ow2.asm:asm:9.9.1")
    implementation("org.ow2.asm:asm-util:9.9.1")
}
