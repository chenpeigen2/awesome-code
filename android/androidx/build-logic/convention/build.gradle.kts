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
    compileOnly("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation(libs.asm)
    implementation(libs.asm.util)
}
