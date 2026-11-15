allprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/") }
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        // Flutter 中国镜像，用于下载 Flutter Android 引擎组件
        maven { url = uri("https://download.flutter.io") }
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

// 动态版本号（如 1.2+）需要拉取 maven-metadata.xml，
// 国内网络下部分仓库不可达会导致构建失败；固定为具体版本。
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            when {
                requested.group == "androidx.test" && requested.name == "runner" -> useVersion("1.6.2")
                requested.group == "androidx.test" && requested.name == "rules" -> useVersion("1.6.1")
                requested.group == "androidx.test.espresso" && requested.name == "espresso-core" -> useVersion("3.6.1")
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
