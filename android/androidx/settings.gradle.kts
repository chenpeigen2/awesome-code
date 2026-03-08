pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "androidx"
include(":app")
include(":apprenderapp")
include(":appdisplayapp")
include(":aidl-common")
include(":aidl_server")
include(":aidl_client")
include(":dynamicanimation")
include(":datastore")
include(":datastore-demo")
include(":koin")

// APT modules
include(":apt-annotation")
include(":apt-compiler")

// Lifecycle demo
include(":lifecycle-demo")

// RecyclerView demo
include(":recyclerview-demo")

// ConstraintLayout demo
include(":constraintlayout-demo")

// WorkManager demo
include(":workmanager-demo")

// LayoutInflater demo
include(":layoutinflater-demo")

// Context demo
include(":context-demo")

// Window demo
include(":window-demo")

// AutoDensity - 屏幕密度适配库
include(":autodensity")
include(":autodensity-demo")

// Components Demo - 四大组件示例
include(":components-demo")

// Coroutine Demo - 协程示例
include(":coroutine-demo")

// Animation Demo - 动画示例
include(":animation-demo")
