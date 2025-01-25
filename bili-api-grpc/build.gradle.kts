import com.google.protobuf.gradle.proto

plugins {
    //id("java-library")
    alias(gradleLibs.plugins.google.protobuf)
    alias(gradleLibs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.okhttp)
    api(libs.grpc.protobuf)
    api(libs.grpc.stub)
    api(libs.protobuf.kotlin)
    implementation(libs.kotlinx.coroutines)
}

sourceSets["main"].proto {
    srcDir("./proto")
    ProtobufConfiguration.excludeProtoFiles.forEach(::exclude)
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
                    //option("lite")
                }
                create("grpckt") {
                    //option("lite")
                }
            }
        }
    }
}
