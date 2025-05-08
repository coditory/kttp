plugins {
    `java-library`
    `maven-publish`
    signing
}

publishing {
    publications.create<MavenPublication>("jvm") {
        artifactId = artifactName()
        from(components["java"])
        versionMapping {
            usage("java-api") {
                fromResolutionOf("runtimeClasspath")
            }
            usage("java-runtime") {
                fromResolutionResult()
            }
        }
        pom {
            name.set(project.name)
            description.set(project.description ?: rootProject.description ?: "Kotlin logging library")
            url.set("https://github.com/coditory/kttp")
            organization {
                name = "Coditory"
                url = "https://coditory.com"
            }
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("ogesaku")
                    name.set("ogesaku")
                    email.set("ogesaku@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/coditory/kttp.git")
                url.set("https://github.com/coditory/kttp")
            }
            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/coditory/kttp/issues")
            }
        }
    }
}

signing {
    if (System.getenv("SIGNING_KEY")?.isNotBlank() == true && System.getenv("SIGNING_PASSWORD")?.isNotBlank() == true) {
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
    }
    sign(publishing.publications["jvm"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.register("artifactName") {
    doLast {
        println(artifactName())
    }
}

fun artifactName(): String {
    var result = project.name
    var proj = project.parent
    while (proj != null) {
        result = proj.name + "-" + result
        proj = proj.parent
    }
    return if (result.startsWith("kttp-")) {
        result
    } else {
        "kttp-$result"
    }
}
