@file:Suppress("UnstableApiUsage")

include(":feature:inset-animation")


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

rootProject.name = "android-playground"
include(":app")
include(":base")
include(":feature:window")
include(":feature:compose-layout")
include(":feature:coil")