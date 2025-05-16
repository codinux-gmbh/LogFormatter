package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.log.test.TestInlineClass
import net.codinux.log.test.DeclaringClass
import net.codinux.log.test.TestObject
import net.codinux.log.test.TestPlatform
import kotlin.test.Test

class ClassNameResolverTest {

    companion object {
        private const val ThisClassPackageName = "net.codinux.log.classname"
    }


    private val underTest = ClassNameResolver


    @Test
    fun getClassNameComponents_Object() {
        val result = underTest.getClassNameComponents(TestObject::class)

        assertClassName(result, "TestObject")
    }

    @Test
    fun getClassNameComponents_NormalClass() {
        val result = underTest.getClassNameComponents(DeclaringClass::class)

        assertClassName(result, "DeclaringClass")
    }

    @Test
    fun getClassNameComponents_CompanionObject() {
        val result = underTest.getClassNameComponents(DeclaringClass.Companion::class)

        // assert companionOwnerClassName correctly removes ".Companion" from class name
        assertClassName(result, "DeclaringClass.Companion", companionOwnerClassName = "DeclaringClass")
    }


    // TODO: detecting class name and declaring class name does not work on Native

    @Test
    fun getClassNameComponents_InnerClass() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass::class)

        assertClassName(result, "DeclaringClass.InnerClass", "DeclaringClass")
    }

    @Test
    fun getClassNameComponents_InnerClassCompanionObject() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass.Companion::class)

        // assert companionOwnerClassName correctly removes ".Companion" from class name and that declaring class
        // differs from companion object owner is detected
        assertClassName(result, "DeclaringClass.InnerClass.Companion", "DeclaringClass", "DeclaringClass.InnerClass")
    }

    @Test
    fun getClassNameComponents_InlineClass() {
        val result = underTest.getClassNameComponents(TestInlineClass::class)

        assertClassName(result, "TestInlineClass")
    }



    @Test
    fun getClassNameComponents_LocalClass() {
        class LocalClass

        val result = underTest.getClassNameComponents(LocalClass::class)

        assertClassName(result, "ClassNameResolverTest.getClassNameComponents_LocalClass.LocalClass", "ClassNameResolverTest", null, ThisClassPackageName)
    }


    // TODO: getting class name components of anonymous classes and lambdas does not work on JS and WASM

    @Test
    fun getClassNameComponents_ReferencedAnonymousClass() {
        val result = underTest.getClassNameComponents(TestObject.AnonymousClass::class)

        assertClassName(result, "TestObject.AnonymousClass", "TestObject", null)
    }

    @Test
    fun getClassNameComponents_LocalAnonymousClass() {
        val anonymous = object : Throwable() {}

        val result = underTest.getClassNameComponents(anonymous::class)

        assertClassName(result, "ClassNameResolverTest.getClassNameComponents_LocalAnonymousClass.anonymous", "ClassNameResolverTest", null, ThisClassPackageName)
    }

    @Test
    fun getClassNameComponents_ReferencedLambda() {
        val result = underTest.getClassNameComponents(TestObject.Lambda::class)

        assertClassName(result, "TestObject.Lambda", "TestObject", null)
    }

    @Test
    fun getClassNameComponents_LocalLambda() {
        val lambda = { x: Int -> x * 2 }

        val result = underTest.getClassNameComponents(lambda::class)

        assertClassName(result, "ClassNameResolverTest.getClassNameComponents_LocalLambda.lambda", "ClassNameResolverTest", null, ThisClassPackageName)
    }


    private fun assertClassName(result: ClassNameComponents, className: String, declaringClassName: String? = null,
                                companionOwnerClassName: String? = null, packageName: String = TestObject.packageName) {

        println("components = $result") // TODO: remove again

        if (TestPlatform.SupportsDetailedClassName) {
            assertThat(result::className).isEqualTo(className)

            assertThat(result::companionOwnerClassName).isEqualTo(companionOwnerClassName)
        } else {
            assertThat(result::className).isEqualTo(className.substringAfterLast('.'))
        }

        if (TestPlatform.SupportsDeterminingDeclaringClassName) {
            assertThat(result::declaringClassName).isEqualTo(declaringClassName)
        }

        if (TestPlatform.SupportsPackageNames) {
            assertThat(result::packageName).isEqualTo(packageName)
        }
    }

}