buildscript {
    repositories {
        mavenCentral()
    }

    val kotlinVersion: String by extra

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}


allprojects {
    repositories {
        mavenCentral()
    }

    group = "net.codinux.log"
    version = "1.0.1-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux-gmbh/LogFormatter"

    ext["projectDescription"] = "Utils common to loggers like stacktrace shortener and class name abbreviator"
}