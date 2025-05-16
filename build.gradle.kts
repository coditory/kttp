plugins {
    id("build.version")
    id("build.coverage")
    id("build.publish-root")
}

allprojects {
    group = "com.coditory.kttp"
    description = "Kotlin HTTP library"
}

dependencies {
    project(":kttp-api")
    project(":kttp-server:api")
    project(":kttp-server:core")
    project(":kttp-server:jdk")
    project(":kttp-server:samples")

    // merged coverage report
    kover(project(":kttp-api"))
    kover(project(":kttp-server:api"))
    kover(project(":kttp-server:core"))
    kover(project(":kttp-server:jdk"))
}
