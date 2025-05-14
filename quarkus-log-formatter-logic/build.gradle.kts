plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)
}


dependencies {
    implementation("io.quarkus:quarkus-core:3.4.0")

    implementation(project(":log-formatter"))


    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}


if (File(projectDir, "../gradle/scripts/publish-codinux.gradle.kts").exists()) {
    apply(from = "../gradle/scripts/publish-codinux.gradle.kts")
}