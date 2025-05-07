
allprojects {
    repositories {
        mavenCentral()
    }

    group = "net.codinux.log"
    version = "1.0.0-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux-gmbh/LogUtils"

    ext["projectDescription"] = "Utils common to loggers like stacktrace shortener and class name abbreviator"
}