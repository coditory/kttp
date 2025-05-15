rootProject.name = "kttp"

includeBuild("build-logic")
include(":kttp-api")
include(":kttp-server:api")
include(":kttp-server:core")
include(":kttp-server:jdk")
include(":kttp-server:samples")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":kttp-api").projectDir = file("./kttp-api")
project(":kttp-server:api").projectDir = file("./kttp-server/api")
project(":kttp-server:core").projectDir = file("./kttp-server/core")
project(":kttp-server:jdk").projectDir = file("./kttp-server/jdk")
project(":kttp-server:samples").projectDir = file("./kttp-server/samples")

plugins {
    id("com.gradle.enterprise").version("3.15.1")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

gradleEnterprise {
    if (!System.getenv("CI").isNullOrEmpty()) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
