plugins {
    id("build.version")
    id("build.coverage")
    id("build.publish-root")
}

allprojects {
    group = "com.coditory.ktserver"
    description = "Async Kotlin Http server"
}

dependencies {
    project(":ktserver-api")
    project(":ktserver-core")
    project(":ktserver-jdk")
    project(":ktserver-sample")

    // merged coverage report
    kover(project(":ktserver-api"))
    kover(project(":ktserver-core"))
    kover(project(":ktserver-jdk"))
}
