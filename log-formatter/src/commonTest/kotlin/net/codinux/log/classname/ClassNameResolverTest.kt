package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.PlatformType
import net.codinux.kotlin.platform.isJavaScript
import net.codinux.kotlin.platform.isJsBrowserOrNodeJs
import net.codinux.log.test.*
import kotlin.test.Test

class ClassNameResolverTest {

    companion object {
        private const val ThisClassPackageName = "net.codinux.log.classname"
    }


    private val underTest = ClassNameResolver()


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

        if (Platform.isJsBrowserOrNodeJs) {
            assertClassName(result, DeclaringClass.Companion.JsName)
        } else {
            // assert enclosingClassName correctly removes ".Companion" from class name
            assertClassName(result, "DeclaringClass.Companion", "DeclaringClass", "DeclaringClass")
        }
    }

    @Test
    fun getClassNameComponents_InlineClass() {
        val result = underTest.getClassNameComponents(TestInlineClass::class)

        assertClassName(result, "TestInlineClass")
    }


    // TODO: detecting class name and declaring class name does not work on Native

    @Test
    fun getClassNameComponents_InnerClass_GuessClassHierarchy() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass::class, guessClassHierarchy = true)

        assertClassName(result, "DeclaringClass.InnerClass", "DeclaringClass", "DeclaringClass")
    }

    @Test
    fun getClassNameComponents_InnerClassCompanionObject_GuessClassHierarchy() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass.Companion::class, guessClassHierarchy = true)

        if (Platform.isJsBrowserOrNodeJs) {
            assertClassName(result, DeclaringClass.InnerClass.Companion.JsName)
        } else {
            // assert enclosingClassName correctly removes ".Companion" from class name and that declaring class
            // differs from companion object owner is detected
            assertClassName(result, "DeclaringClass.InnerClass.Companion", "DeclaringClass", "DeclaringClass.InnerClass")
        }
    }

    @Test
    fun getClassNameComponents_SecondLevelInnerClass_GuessClassHierarchy() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass.InnerClassInInnerClass::class, guessClassHierarchy = true)

        if (Platform.isJavaScript) {
            assertClassName(result, "InnerClassInInnerClass")
        } else {
            assertClassName(result, "DeclaringClass.InnerClass.InnerClassInInnerClass", "DeclaringClass", "DeclaringClass.InnerClass")
        }
    }



    @Test
    fun getClassNameComponents_LocalClass() {
        class LocalClass

        val result = underTest.getClassNameComponents(LocalClass::class)

        assertClassName(result, "ClassNameResolverTest.getClassNameComponents_LocalClass.LocalClass", "ClassNameResolverTest", "ClassNameResolverTest", ThisClassPackageName)
    }

    @Test
    fun getClassNameComponents_ReferencedAnonymousClass() {
        val result = underTest.getClassNameComponents(TestObject.AnonymousClass::class)

        if (Platform.type == PlatformType.WasmJs) { // for anonymous classes WASM only returns "<no name provided>"
            assertClassName(result, "<anonymous class>")
        } else {
            assertClassName(result, "TestObject.AnonymousClass", "TestObject", "TestObject")
        }
    }

    @Test
    fun getClassNameComponents_LocalAnonymousClass() {
        val anonymous = object : Throwable() {}

        val result = underTest.getClassNameComponents(anonymous::class)

        if (Platform.type == PlatformType.WasmJs) { // for anonymous classes WASM only returns "<no name provided>"
            assertClassName(result, "<anonymous class>")
        } else {
            assertClassName(result, "ClassNameResolverTest.getClassNameComponents_LocalAnonymousClass.anonymous", "ClassNameResolverTest", "ClassNameResolverTest", ThisClassPackageName)
        }
    }

    @Test
    fun getClassNameComponents_ReferencedLambda() {
        val result = underTest.getClassNameComponents(TestObject.Lambda::class)

        if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "TestObject.Lambda.lambda", "TestObject", "TestObject")
        } else if (Platform.isJsBrowserOrNodeJs) { // for lambdas JS only returns "Function<index>"
            assertClassName(result, "Function1")
        } else {
            assertClassName(result, "TestObject.Lambda", "TestObject", "TestObject")
        }
    }

    @Test
    fun getClassNameComponents_LocalLambda() {
        val lambda = { x: Int -> x * 2 }

        val result = underTest.getClassNameComponents(lambda::class)

        if (Platform.isJsBrowserOrNodeJs) { // for lambdas JS only returns "Function<index>"
            assertClassName(result, "Function1")
        } else {
            assertClassName(result, "ClassNameResolverTest.getClassNameComponents_LocalLambda.lambda", "ClassNameResolverTest", "ClassNameResolverTest", ThisClassPackageName)
        }
    }


    private fun assertClassName(result: ClassNameComponents, className: String, declaringClassName: String? = null,
                                enclosingClassName: String? = null, packageName: String = TestObject.packageName) {

        // JS supports getting enclosing class only for local and anonymous classes, WASM only for Lambdas
        if (TestPlatform.SupportsDetailedClassName || result.declaringClassName != null) {
            assertThat(result::className).isEqualTo(className)

            assertThat(result::enclosingClassName).isEqualTo(enclosingClassName)
        } else {
            assertThat(result::className).isEqualTo(className.substringAfterLast('.'))
        }

        // JS supports getting enclosing class only for local and anonymous classes, WASM only for Lambdas
        if (TestPlatform.SupportsDeterminingDeclaringClassName || result.declaringClassName != null) {
            assertThat(result::declaringClassName).isEqualTo(declaringClassName)
        }

        if (TestPlatform.SupportsPackageNames) {
            assertThat(result::packageName).isEqualTo(packageName)
        }
    }

}