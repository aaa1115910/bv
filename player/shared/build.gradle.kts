plugins {
    alias(gradleLibs.plugins.android.library)
    alias(gradleLibs.plugins.compose.compiler)
    alias(gradleLibs.plugins.kotlin.android)
}

android {
    namespace = "${AppConfiguration.appId}.player.shared"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


dependencies {
    implementation(androidx.activity.compose)
    implementation(androidx.core.ktx)
    implementation(androidx.compose.ui)
    implementation(androidx.compose.ui.util)
    implementation(androidx.compose.ui.tooling.preview)
    implementation(androidx.compose.tv.foundation)
    implementation(androidx.compose.tv.material)
    implementation(androidx.compose.material)
    implementation(libs.material)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(androidx.compose.ui.test.junit4)
    debugImplementation(androidx.compose.ui.test.manifest)
    debugImplementation(androidx.compose.ui.tooling)
}