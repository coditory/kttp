plugins {
    id("org.jetbrains.kotlinx.kover")
}

tasks.register("coverage") {
    dependsOn("koverXmlReport", "koverHtmlReport", "koverLog")
}
