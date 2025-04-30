plugins {
    id("build.kotlin")
    alias(libs.plugins.shadowJar)
    application
}

description = "Kttp Server - samples"

application {
    mainClass.set("com.coditory.ktserver.sample.SampleRunner")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass.get(),
            ),
        )
    }
}

dependencies {
    implementation(project(":kttp-server:jdk"))
}
