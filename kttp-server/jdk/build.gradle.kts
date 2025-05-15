plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "Kttp Server - implementation with JDK HttpServer"

dependencies {
    api(project(":kttp-server:api"))
    implementation(project(":kttp-server:core"))
    api(libs.kotlinx.coroutines.core)
    implementation(libs.quark.uri)
}
