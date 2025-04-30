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
    project(":ktserver")
    project(":ktserver-jdk")
    project(":ktserver-sample")

    // merged coverage report
    kover(project(":ktserver"))
    kover(project(":ktserver-jdk"))
}
