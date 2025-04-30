plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "Kttp Server - API"

dependencies {
    api(project(":kttp-api"))
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json.io)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.io.core)
    implementation(libs.quark.uri)
}
