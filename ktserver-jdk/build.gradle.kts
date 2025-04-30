plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "KtServer based on JDK HttpServer"

dependencies {
    api(project(":ktserver"))
    implementation(libs.slf4j.api)
}
