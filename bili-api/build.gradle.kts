import com.google.protobuf.gradle.proto

plugins {
    alias(gradleLibs.plugins.google.protobuf)
    alias(gradleLibs.plugins.kotlin.jvm)
    alias(gradleLibs.plugins.kotlin.serialization)
}

group = "dev.aaa1115910"

dependencies {
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.okhttp)
    api(libs.grpc.protobuf)
    api(libs.grpc.stub)
    api(libs.protobuf.kotlin)
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

sourceSets["main"].proto {
    srcDir("./proto")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        create("java") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.asProvider().get()}"
        }
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.asProvider().get()}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${libs.versions.grpc.kotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                named("java") {
                    //option("lite")
                }
                create("kotlin") {
                    //option("lite")
                }
            }
            it.plugins {
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
        }
    }
}
