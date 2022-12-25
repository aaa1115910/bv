plugins {
    alias(gradleLibs.plugins.kotlin.jvm)
    alias(gradleLibs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}
