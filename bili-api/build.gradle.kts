plugins {
    alias(gradleLibs.plugins.kotlin.jvm)
    alias(gradleLibs.plugins.kotlin.serialization)
}

group = "dev.aaa1115910"

dependencies {
    implementation(project(":bili-api-grpc"))
    implementation(libs.jsoup)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.jsoup)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.serialization.kotlinx)
    implementation(libs.logging)
    implementation(libs.slf4j.simple)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}