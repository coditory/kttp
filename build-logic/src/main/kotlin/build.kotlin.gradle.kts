plugins {
    id("build.java")
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

ktlint {
    version = libs.versions.ktlint.get()
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
    compilerOptions {
        allWarningsAsErrors = true
    }
}
