pluginManagement {
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

rootProject.name = "SupplyLens"

include(":app")
include(":core:ui")
include(":core:network")
include(":core:domain")
include(":core:data")
include(":core:billing")
include(":core:ai")
include(":feature:search")
include(":feature:token")
include(":feature:watchlist")
include(":feature:trade")
include(":feature:alerts")
include(":feature:settings")
include(":feature:forecast")
