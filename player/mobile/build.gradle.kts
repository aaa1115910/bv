@file:Suppress("UnstableApiUsage")

plugins {
    alias(gradleLibs.plugins.android.library)
    alias(gradleLibs.plugins.compose.compiler)
    alias(gradleLibs.plugins.kotlin.android)
}

android {
    namespace = "${AppConfiguration.appId}.player.mobile"
    compileSdk = AppConfiguration.compileSdk

    defaultConfig {
        minSdk = AppConfiguration.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("r8Test") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("alpha") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":player:core"))
    implementation(project(":player:shared"))
    implementation(androidx.activity.compose)
    implementation(androidx.compose.constraintlayout)
    implementation(androidx.compose.material)
    implementation(androidx.compose.material.icons)
    implementation(androidx.compose.material3)
    implementation(androidx.compose.ui)
    implementation(androidx.compose.ui.tooling.preview)
    implementation(androidx.compose.ui.util)
    implementation(androidx.core.ktx)
    implementation(androidx.media3.common)
    implementation(androidx.media3.decoder)
    implementation(androidx.media3.exoplayer)
    implementation(androidx.media3.exoplayer.dash)
    implementation(androidx.media3.exoplayer.hls)
    implementation(androidx.media3.ui)
    implementation(libs.logging)
    implementation(libs.material)
    debugImplementation(androidx.compose.ui.test.manifest)
    debugImplementation(androidx.compose.ui.tooling)
}