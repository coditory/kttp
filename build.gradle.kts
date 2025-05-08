plugins {
    id("build.version")
    id("build.coverage")
    id("build.publish-root")
}

allprojects {
    group = "com.coditory.kttp"
    description = "Kotlin HTTP layer"
}

dependencies {
    project(":api")
    project(":server:api")
    project(":server:core")
    project(":server:jdk")
    project(":server:samples")

    // merged coverage report
    kover(project(":api"))
    kover(project(":server:api"))
    kover(project(":server:core"))
    kover(project(":server:jdk"))
}
