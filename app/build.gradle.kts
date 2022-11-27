plugins {
    alias(gradleLibs.plugins.android.application)
    alias(gradleLibs.plugins.kotlin.android)
    alias(gradleLibs.plugins.kotlin.serialization)
}

android {
    namespace = AppConfiguration.appId
    compileSdk = AppConfiguration.compileSdk

    defaultConfig {
        applicationId = AppConfiguration.appId
        minSdk = AppConfiguration.minSdk
        targetSdk = AppConfiguration.targetSdk
        versionCode = AppConfiguration.versionCode
        versionName = AppConfiguration.versionName
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    implementation(platform("${androidx.compose.bom.get()}"))
    implementation(androidx.activity.compose)
    implementation(androidx.core.ktx)
    implementation(androidx.compose.ui)
    implementation(androidx.compose.ui.util)
    implementation(androidx.compose.ui.tooling.preview)
    implementation(androidx.compose.material3)
    implementation(androidx.compose.tv.foundation)
    implementation(androidx.compose.tv.material)
    implementation(androidx.lifecycle.runtime.ktx)
    implementation(androidx.media3.common)
    implementation(androidx.media3.decoder)
    implementation(androidx.media3.exoplayer)
    implementation(androidx.media3.exoplayer.dash)
    implementation(androidx.media3.exoplayer.hls)
    implementation(androidx.media3.ui)
    implementation(libs.akdanmaku)
    implementation(libs.coil.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.cio)
    implementation(libs.koin.core)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.core)
    implementation(libs.ktor.encoding)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.serialization.kotlinx)
    implementation(libs.logging)
    implementation(libs.material)
    implementation(libs.slf4j.android.mvysny)
    implementation(project(mapOf("path" to ":bili-api")))
    androidTestImplementation(platform("${androidx.compose.bom.get()}"))
    androidTestImplementation(androidx.compose.ui.test.junit4)
    debugImplementation(androidx.compose.ui.test.manifest)
    debugImplementation(androidx.compose.ui.tooling)
}