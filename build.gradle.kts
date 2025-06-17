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
    version = "1.7.4-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux-gmbh/LogFormatter"

    ext["projectDescription"] = "Utils common to loggers like stacktrace shortener and class name abbreviator"
}



gradle.taskGraph.whenReady {
    setVersion(project.version.toString())
}

fun setVersion(version: String) {
    val projectDir = project.projectDir

    val parentPomVersionRegex = Regex("^    <version>[\\dSNAPSHOT.-]+</version>", RegexOption.MULTILINE)
    val childPomVersionRegex = Regex("^        <version>[\\dSNAPSHOT.-]+</version>", RegexOption.MULTILINE)

    val parentPomFile = projectDir.resolve("pom.xml")
    val parentPomText = parentPomFile.readText()
    val parentPomTextUpdated = parentPomText.replaceFirst(parentPomVersionRegex, "    <version>$version</version>")
    parentPomFile.writeText(parentPomTextUpdated)

    listOf(projectDir.resolve("quarkus-log-formatter-deployment"), projectDir.resolve("quarkus-log-formatter-runtime"))
        .map { it.resolve("pom.xml") }
        .forEach { childPomFile ->
            val childPomText = childPomFile.readText()
            val childPomTextUpdated = childPomText.replaceFirst(childPomVersionRegex, "        <version>$version</version>")
            childPomFile.writeText(childPomTextUpdated)
        }


    if (version.endsWith("-SNAPSHOT") == false) {
        val readme = file("README.md")

        val updatedReadmeContent = readme.readText()
            .replace(Regex("""(implementation\("net\.codinux\.log:log-formatter:)[^"]*""""), "$1$version\"")
            .replace(Regex("""(^\s*<version>)[\d.]+(<\/version>$)""", RegexOption.MULTILINE), "$1$version$2")
        readme.writeText(updatedReadmeContent)
    }
}
