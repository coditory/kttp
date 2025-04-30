@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm")
    `jvm-test-suite`
    id("build.coverage")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter()
            targets.configureEach {
                testTask {
                    testLogging {
                        events("passed", "failed", "skipped")
                        setExceptionFormat("full")
                    }
                }
            }
        }
        val test by getting(JvmTestSuite::class)
        val integrationTest by registering(JvmTestSuite::class) {
            targets.all {
                testTask.configure {
                    shouldRunAfter(test)
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}

kotlin {
    target.compilations {
        getByName("integrationTest")
            .associateWith(getByName("test"))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

dependencies {
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.awaitility)
}
