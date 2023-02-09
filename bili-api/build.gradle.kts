plugins {
    alias(gradleLibs.plugins.kotlin.jvm)
    alias(gradleLibs.plugins.kotlin.serialization)
}

group = "dev.aaa1115910"

dependencies {
    implementation(libs.jsoup)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.core)
    implementation(libs.ktor.encoding)
    implementation(libs.ktor.jsoup)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.serialization.kotlinx)
    implementation(libs.logging)
    implementation(libs.slf4j.simple)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}
