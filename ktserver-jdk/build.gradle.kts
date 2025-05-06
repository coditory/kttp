plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "KtServer - implementation with JDK HttpServer"

dependencies {
    api(project(":ktserver-core"))
    implementation(project(":ktserver-core"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.quark.uri)
}
