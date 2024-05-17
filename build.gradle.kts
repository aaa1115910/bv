plugins {
    alias(gradleLibs.plugins.android.application) apply false
    alias(gradleLibs.plugins.android.library) apply false
    alias(gradleLibs.plugins.compose.compiler) apply false
    alias(gradleLibs.plugins.google.ksp) apply false
    alias(gradleLibs.plugins.kotlin.android) apply false
    alias(gradleLibs.plugins.kotlin.jvm) apply false
    alias(gradleLibs.plugins.kotlin.serialization) apply false
    alias(gradleLibs.plugins.versions)
}