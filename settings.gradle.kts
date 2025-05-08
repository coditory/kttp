rootProject.name = "kttp"

includeBuild("build-logic")
include(":api")
include(":server:api")
include(":server:core")
include(":server:jdk")
include(":server:samples")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":api").projectDir = file("./kttp-api")
project(":server:api").projectDir = file("./kttp-server/api")
project(":server:core").projectDir = file("./kttp-server/core")
project(":server:jdk").projectDir = file("./kttp-server/jdk")
project(":server:samples").projectDir = file("./kttp-server/samples")

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
