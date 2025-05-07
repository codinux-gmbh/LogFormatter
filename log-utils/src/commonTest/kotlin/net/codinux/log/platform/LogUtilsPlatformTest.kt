package net.codinux.log.platform

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import net.codinux.kotlin.Platform
import net.codinux.kotlin.PlatformType
import net.codinux.log.test.TestClasses
import kotlin.test.Test

class LogUtilsPlatformTest {

    @Test
    fun getQualifiedClassName_Object() {
        val result = LogUtilsPlatform.getQualifiedClassName(TestClasses::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses")
        } else {
            assertThat(result).isEqualTo("TestClasses")
        }
    }

    @Test
    fun getQualifiedClassName_NormalClass() {
        val result = LogUtilsPlatform.getQualifiedClassName(TestClasses.OuterClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass")
        } else {
            assertThat(result).isEqualTo("OuterClass")
        }
    }

    @Test
    fun getQualifiedClassName_CompanionObject() {
        val result = LogUtilsPlatform.getQualifiedClassName(TestClasses.OuterClass.Companion::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.Companion")
        } else {
            assertThat(result).isEqualTo("Companion")
        }
    }

    @Test
    fun getQualifiedClassName_InnerClass() {
        val result = LogUtilsPlatform.getQualifiedClassName(TestClasses.OuterClass.InnerClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.InnerClass")
        } else {
            assertThat(result).isEqualTo("InnerClass")
        }
    }

    @Test
    fun getQualifiedClassName_InnerClassCompanionObject() {
        val result = LogUtilsPlatform.getQualifiedClassName(TestClasses.OuterClass.InnerClass.Companion::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.InnerClass.Companion")
        } else {
            assertThat(result).isEqualTo("Companion")
        }
    }

    @Test
    fun getQualifiedClassName_InlineClass() {
        val result = LogUtilsPlatform.getQualifiedClassName(TestClasses.InlineClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.InlineClass")
        } else {
            assertThat(result).isEqualTo("InlineClass")
        }
    }


    @Test
    fun getQualifiedClassName_AnonymousClass() {
        val anonymous = object : Throwable() {}

        val result = LogUtilsPlatform.getQualifiedClassName(anonymous::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.platform.LogUtilsPlatformTest.getQualifiedClassName_AnonymousClass.anonymous")
        } else {
            assertThat(result).isIn("LogUtilsPlatformTest\$getQualifiedClassName_AnonymousClass\$anonymous${'$'}1", "<no name provided>")
        }
    }

    @Test
    fun getQualifiedClassName_LocalClass() {
        class LocalClass

        val result = LogUtilsPlatform.getQualifiedClassName(LocalClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.platform.LogUtilsPlatformTest.getQualifiedClassName_LocalClass.LocalClass")
        } else {
            assertThat(result).isEqualTo("LocalClass")
        }
    }

    @Test
    fun getQualifiedClassName_Lambda() {
        val lambda = { x: Int -> x * 2 }

        val result = LogUtilsPlatform.getQualifiedClassName(lambda::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.platform.LogUtilsPlatformTest.getQualifiedClassName_Lambda.lambda")
        } else {
            assertThat(result).isIn("Function1", "LogUtilsPlatformTest\$getQualifiedClassName_Lambda\$lambda")
        }
    }


    private val supportsQualifiedClassName: Boolean = Platform.type !in listOf(PlatformType.JavaScriptBrowser, PlatformType.JavaScriptNodeJS, PlatformType.WasmJs)
    
}