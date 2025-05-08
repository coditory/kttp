plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "Kttp Server - Internal core"

dependencies {
    api(project(":server:api"))
    api(libs.kotlin.reflect)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.serialization.json.io)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.io.core)
    api(libs.klog)
    api(libs.quark.uri)
}
