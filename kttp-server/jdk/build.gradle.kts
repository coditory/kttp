plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "Kttp Server - implementation with JDK HttpServer"

dependencies {
    api(project(":server:core"))
    implementation(project(":server:core"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.quark.uri)
}
