rootProject.name = "ktserver"

includeBuild("build-logic")
include("ktserver-api")
include("ktserver-core")
include("ktserver-jdk")
include("ktserver-sample")

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
