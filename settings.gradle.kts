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
        maven("https://jitpack.io")
        maven("https://repo1.maven.org/maven2/")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        //maven("https://artifact.bytedance.com/repository/releases/")
    }
    versionCatalogs {
        create("androidx") { from(files("gradle/androidx.versions.toml")) }
        create("gradleLibs") { from(files("gradle/gradle.versions.toml")) }
    }
}
rootProject.name = "BV"
include(":app")
include(":app:mobile")
include(":app:shared")
include(":app:tv")
include(":bili-api")
include(":bili-api:grpc")
include(":bili-subtitle")
include(":libs:av1Decoder")
include(":libs:ffmpegDecoder")
include(":libs:libVLC")
include(":player")
include(":player:core")
include(":player:mobile")
include(":player:shared")
include(":player:tv")
include(":utils")
