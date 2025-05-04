plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "KtServer based on Java non-blocking I/O"

dependencies {
    api(project(":ktserver"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.quark.uri)
}
