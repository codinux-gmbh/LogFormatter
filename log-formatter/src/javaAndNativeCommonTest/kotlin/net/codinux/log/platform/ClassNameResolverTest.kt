package net.codinux.log.platform

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.log.classname.ClassNameResolver
import net.codinux.log.test.TestClasses
import kotlin.test.Test

class ClassNameResolverTest {

    @Test
    fun getQualifiedClassName_Object() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses::class)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses")
    }

    @Test
    fun getQualifiedClassName_NormalClass() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.OuterClass::class)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass")
    }

    @Test
    fun getQualifiedClassName_CompanionObject() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.OuterClass.Companion::class)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.Companion")
    }

    @Test
    fun getQualifiedClassName_CompanionObject_getDeclaringClass() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.OuterClass.Companion::class, getDeclaringClass = true)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses") // assert 'Companion' gets removed from class name
    }

    @Test
    fun getQualifiedClassName_InnerClass() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.OuterClass.InnerClass::class)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.InnerClass")
    }

    @Test
    fun getQualifiedClassName_InnerClassCompanionObject() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.OuterClass.InnerClass.Companion::class)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.OuterClass.InnerClass.Companion")
    }

    @Test
    fun getQualifiedClassName_InnerClassCompanionObject_getDeclaringClass() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.OuterClass.InnerClass.Companion::class, getDeclaringClass = true)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses") // assert 'Companion' gets removed from class name
    }

    @Test
    fun getQualifiedClassName_InlineClass() {
        val result = ClassNameResolver.getQualifiedClassName(TestClasses.InlineClass::class)

        assertThat(result).isEqualTo("net.codinux.log.test.TestClasses.InlineClass")
    }


    @Test
    fun getQualifiedClassName_AnonymousClass() {
        val anonymous = object : Throwable() {}

        val result = ClassNameResolver.getQualifiedClassName(anonymous::class)

        assertThat(result).isEqualTo("net.codinux.log.platform.ClassNameResolverTest.getQualifiedClassName_AnonymousClass.anonymous")
    }

    @Test
    fun getQualifiedClassName_AnonymousClass_getDeclaringClass() {
        val anonymous = object : Throwable() {}

        val result = ClassNameResolver.getQualifiedClassName(anonymous::class, getDeclaringClass = true)

        assertThat(result).isEqualTo("net.codinux.log.platform.ClassNameResolverTest")
    }

    @Test
    fun getQualifiedClassName_LocalClass() {
        class LocalClass

        val result = ClassNameResolver.getQualifiedClassName(LocalClass::class)

        assertThat(result).isEqualTo("net.codinux.log.platform.ClassNameResolverTest.getQualifiedClassName_LocalClass.LocalClass")
    }

    @Test
    fun getQualifiedClassName_LocalClass_getDeclaringClass() {
        class LocalClass

        val result = ClassNameResolver.getQualifiedClassName(LocalClass::class, getDeclaringClass = true)

        assertThat(result).isEqualTo("net.codinux.log.platform.ClassNameResolverTest")
    }

    @Test
    fun getQualifiedClassName_Lambda() {
        val lambda = { x: Int -> x * 2 }

        val result = ClassNameResolver.getQualifiedClassName(lambda::class)

        assertThat(result).isEqualTo("net.codinux.log.platform.ClassNameResolverTest.getQualifiedClassName_Lambda.lambda")
    }

    @Test
    fun getQualifiedClassName_Lambda_getDeclaringClass() {
        val lambda = { x: Int -> x * 2 }

        val result = ClassNameResolver.getQualifiedClassName(lambda::class, getDeclaringClass = true)

        assertThat(result).isEqualTo("net.codinux.log.platform.ClassNameResolverTest")
    }

}