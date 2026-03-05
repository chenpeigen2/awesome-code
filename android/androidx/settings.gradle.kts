pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21" apply false
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
//include(":appdisplayapp")
//include(":myapplication")
include(":apprenderapp")
include(":appdisplayapp")
//include(":aidl_server")
//include(":aidl_client")
include(":dynamicanimation")
include(":datastore")
include(":datastore-demo")
include(":koin")

// APT modules
include(":apt-annotation")
include(":apt-compiler")

// Lifecycle demo
include(":lifecycle-demo")
