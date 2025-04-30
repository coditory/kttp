plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "KtServer - Kotlin async http server"

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)
}
