import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}


kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // suppresses compiler warning: [EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING] 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")

        // avoid "variable has been optimised out" in debugging mode
        if (System.getProperty("idea.debugger.dispatch.addr") != null) {
            freeCompilerArgs.add("-Xdebug")
        }
    }


    jvmToolchain(8)

    jvm()

    js(IR) {
        binaries.library()

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {
            testTask {
                useMocha {
                    timeout = "20s" // Mocha times out after 2 s, which is too short for bufferExceeded() test
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }
    }


    linuxX64()
    mingwX64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()

    applyDefaultHierarchyTemplate()


    val kotlinxSerializationVersion: String by project

    val logDataVersion: String by project
    val kmpDateTimeVersion: String by project
    val kmpBaseVersion: String by project

    val jacksonVersion: String by project

    val assertKVersion: String by project

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")

            api("net.codinux.log:log-data:$logDataVersion")
            implementation("net.dankito.datetime:kmp-datetime:$kmpDateTimeVersion")
            api("net.codinux.kotlin:kmp-base:$kmpBaseVersion")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))

            implementation("com.willowtreeapps.assertk:assertk:$assertKVersion")
        }


        val javaAndNativeCommonMain by creating {
            dependsOn(commonMain.get())

            jvmMain.get().dependsOn(this)
            nativeMain.get().dependsOn(this)
        }
        val javaAndNativeCommonTest by creating {
            dependsOn(commonTest.get())

            jvmTest.get().dependsOn(this)
            nativeTest.get().dependsOn(this)
        }

        jvmMain.dependencies {
            compileOnly("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
        }
    }
}


if (File(projectDir, "../gradle/scripts/publish-codinux.gradle.kts").exists()) {
    apply(from = "../gradle/scripts/publish-codinux.gradle.kts")
}