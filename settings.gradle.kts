pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "LogFormatter"


include("log-formatter")
include("quarkus-log-formatter-logic")