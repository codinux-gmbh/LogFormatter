package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.PlatformType
import net.codinux.kotlin.platform.isJavaScript
import net.codinux.kotlin.platform.isJsBrowserOrNodeJs
import net.codinux.log.platform.LogFormatterPlatform
import net.codinux.log.test.*
import kotlin.reflect.KClass
import kotlin.test.Test

class QualifiedClassNameParserTest {

    companion object {
        private const val ThisClassName = "QualifiedClassNameParserTest"

        private const val ThisClassPackageName = "net.codinux.log.classname"
    }


    private val underTest = QualifiedClassNameParser()


    @Test
    fun `object`() {
        val result = extractClassAndPackageName(TestObject::class)

        assertClassName(result, "TestObject", ClassTypeCategory.TopLevel)
    }

    @Test
    fun normalClass() {
        val result = extractClassAndPackageName(DeclaringClass::class)

        assertClassName(result, "DeclaringClass", ClassTypeCategory.TopLevel)
    }

    @Test
    fun companionObject() {
        val result = extractClassAndPackageName(DeclaringClass.Companion::class)

        if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "Companion", ClassTypeCategory.Nested)
        } else if (Platform.isJsBrowserOrNodeJs) { // for Companions JS only returns "Companion_<index>"
            assertClassName(result, "Companion_3", ClassTypeCategory.Nested)
        } else {
            assertClassName(result, "DeclaringClass.Companion",
                ClassTypeCategory.Nested, "DeclaringClass")
        }
    }

    @Test
    fun inlineClass() {
        val result = extractClassAndPackageName(TestInlineClass::class)

        assertClassName(result, "TestInlineClass", ClassTypeCategory.TopLevel)
    }


    @Test
    fun innerClass_GuessClassHierarchy() {
        val result = extractClassAndPackageName(DeclaringClass.InnerClass::class, true)

        if (Platform.isJavaScript) {
            assertClassName(result, "InnerClass", ClassTypeCategory.TopLevel)
        } else {
            assertClassName(result, "DeclaringClass.InnerClass", ClassTypeCategory.Nested, "DeclaringClass")
        }
    }

    @Test
    fun innerClassCompanion_GuessClassHierarchy() {
        val result = extractClassAndPackageName(DeclaringClass.InnerClass.Companion::class, true)

        if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "Companion", ClassTypeCategory.Nested)
        } else if (Platform.isJsBrowserOrNodeJs) { // for Companions JS only returns "Companion_<index>"
            assertClassName(result, "Companion_2", ClassTypeCategory.Nested)
        } else {
            assertClassName(result, "DeclaringClass.InnerClass.Companion", ClassTypeCategory.Nested, "DeclaringClass.InnerClass")
        }
    }

    @Test
    fun secondLevelInnerClass_GuessClassHierarchy() {
        val result = extractClassAndPackageName(DeclaringClass.InnerClass.InnerClassInInnerClass::class, true)

        if (Platform.isJavaScript) {
            assertClassName(result, "InnerClassInInnerClass", ClassTypeCategory.TopLevel)
        } else {
            assertClassName(result, "DeclaringClass.InnerClass.InnerClassInInnerClass", ClassTypeCategory.Nested, "DeclaringClass.InnerClass")
        }
    }


    @Test
    fun localClass() {
        class LocalClass

        val result = extractClassAndPackageName(LocalClass::class)

        if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "LocalClass", ClassTypeCategory.TopLevel) // for WASM we have not chance to detect local classes
        } else {
            assertClassName(result, "QualifiedClassNameParserTest\$localClass\$LocalClass",
                ClassTypeCategory.LocalClassAnonymousClassOrFunction, ThisClassName, ThisClassPackageName)
            assertThat(result::classNameWithDotSeparatedAnonymousParts).isEqualTo("QualifiedClassNameParserTest.localClass.LocalClass")
        }
    }

    @Test
    fun anonymousClass() {
        val anonymous = object : Throwable() {}


        val result = extractClassAndPackageName(anonymous::class)

        if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "<anonymous class>", ClassTypeCategory.LocalClassAnonymousClassOrFunction)
        } else {
            assertClassName(result, "QualifiedClassNameParserTest\$anonymousClass\$anonymous\$1",
                ClassTypeCategory.LocalClassAnonymousClassOrFunction, ThisClassName, ThisClassPackageName)
            assertThat(result::classNameWithDotSeparatedAnonymousParts).isEqualTo("QualifiedClassNameParserTest.anonymousClass.anonymous.1")
        }
    }

    @Test
    fun lambda() {
        val lambda = { x: Int -> x * 2 }

        val result = extractClassAndPackageName(lambda::class)

        if (Platform.isJsBrowserOrNodeJs) { // for lambdas JS only returns "Function<index>"
            assertClassName(result, "Function1", ClassTypeCategory.LocalClassAnonymousClassOrFunction)
        } else if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "QualifiedClassNameParserTest\$lambda\$lambda",
                ClassTypeCategory.LocalClassAnonymousClassOrFunction, ThisClassName)
            assertThat(result::classNameWithDotSeparatedAnonymousParts).isEqualTo("QualifiedClassNameParserTest.lambda.lambda")
        } else {
            assertClassName(result, "QualifiedClassNameParserTest\$lambda\$lambda\$1",
                ClassTypeCategory.LocalClassAnonymousClassOrFunction, ThisClassName, ThisClassPackageName)
            assertThat(result::classNameWithDotSeparatedAnonymousParts).isEqualTo("QualifiedClassNameParserTest.lambda.lambda.1")
        }
    }


    private fun assertClassName(result: ClassAndPackageName, expectedClassName: String, expectedCategory: ClassTypeCategory,
                                expectedEnclosingClassName: String? = null,
                                expectedPackageName: String = TestObject.packageName) {
        assertThat(result::className).isEqualTo(expectedClassName)
        assertThat(result::category).isEqualTo(expectedCategory)
        assertThat(result::enclosingClassName).isEqualTo(expectedEnclosingClassName)

        if (TestPlatform.SupportsPackageNames) {
            assertThat(result::packageName).isEqualTo(expectedPackageName)
        }
    }


    private fun <T : Any> extractClassAndPackageName(kClass: KClass<T>, guessClassHierarchy: Boolean = false): ClassAndPackageName {
        val classInfo = LogFormatterPlatform.getClassInfo(kClass)

        return underTest.extractClassAndPackageName(classInfo.qualifiedClassName ?: classInfo.classNameWithoutPackageName ?: kClass.toString(), guessClassHierarchy)
    }

}