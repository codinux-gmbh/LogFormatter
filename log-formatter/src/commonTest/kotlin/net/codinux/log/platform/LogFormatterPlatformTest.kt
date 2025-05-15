package net.codinux.log.platform

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import net.codinux.log.test.TestClasses
import kotlin.test.Test

class LogFormatterPlatformTest {

    @Test
    fun getQualifiedClassName_Object() {
        val result = LogFormatterPlatform.getQualifiedClassName(TestClasses::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses")
        } else {
            assertThat(result).isEqualTo("TestClasses")
        }
    }

    @Test
    fun getQualifiedClassName_NormalClass() {
        val result = LogFormatterPlatform.getQualifiedClassName(TestClasses.OuterClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass")
        } else {
            assertThat(result).isEqualTo("OuterClass")
        }
    }

    @Test
    fun getQualifiedClassName_CompanionObject() {
        val result = LogFormatterPlatform.getQualifiedClassName(TestClasses.OuterClass.Companion::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.Companion")
        } else {
            assertThat(result).isEqualTo("Companion")
        }
    }

    @Test
    fun getQualifiedClassName_InnerClass() {
        val result = LogFormatterPlatform.getQualifiedClassName(TestClasses.OuterClass.InnerClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.InnerClass")
        } else {
            assertThat(result).isEqualTo("InnerClass")
        }
    }

    @Test
    fun getQualifiedClassName_InnerClassCompanionObject() {
        val result = LogFormatterPlatform.getQualifiedClassName(TestClasses.OuterClass.InnerClass.Companion::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.InnerClass.Companion")
        } else {
            assertThat(result).isEqualTo("Companion")
        }
    }

    @Test
    fun getQualifiedClassName_InlineClass() {
        val result = LogFormatterPlatform.getQualifiedClassName(TestClasses.InlineClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.InlineClass")
        } else {
            assertThat(result).isEqualTo("InlineClass")
        }
    }


    @Test
    fun getQualifiedClassName_AnonymousClass() {
        val anonymous = object : Throwable() {}

        val result = LogFormatterPlatform.getQualifiedClassName(anonymous::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.platform.LogFormatterPlatformTest.getQualifiedClassName_AnonymousClass.anonymous")
        } else {
            assertThat(result).isIn("LogFormatterPlatformTest\$getQualifiedClassName_AnonymousClass\$anonymous${'$'}1", "<no name provided>")
        }
    }

    @Test
    fun getQualifiedClassName_LocalClass() {
        class LocalClass

        val result = LogFormatterPlatform.getQualifiedClassName(LocalClass::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.platform.LogFormatterPlatformTest.getQualifiedClassName_LocalClass.LocalClass")
        } else {
            assertThat(result).isEqualTo("LocalClass")
        }
    }

    @Test
    fun getQualifiedClassName_Lambda() {
        val lambda = { x: Int -> x * 2 }

        val result = LogFormatterPlatform.getQualifiedClassName(lambda::class)

        if (supportsQualifiedClassName) {
            assertThat(result).isEqualTo("net.codinux.log.platform.LogFormatterPlatformTest.getQualifiedClassName_Lambda.lambda")
        } else {
            assertThat(result).isIn("Function1", "LogFormatterPlatformTest\$getQualifiedClassName_Lambda\$lambda")
        }
    }


    private val supportsQualifiedClassName: Boolean = LogFormatterPlatform.supportsPackageNames
    
}