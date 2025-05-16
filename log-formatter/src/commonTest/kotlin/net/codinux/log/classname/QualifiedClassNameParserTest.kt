package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.PlatformType
import net.codinux.log.platform.LogFormatterPlatform
import net.codinux.log.test.*
import kotlin.reflect.KClass
import kotlin.test.Test

class QualifiedClassNameParserTest {

    companion object {
        private const val ThisClassPackageName = "net.codinux.log.classname"
    }


    private val underTest = QualifiedClassNameParser()


    @Test
    fun `object`() {
        val result = extractClassAndPackageName(TestObject::class)

        assertClassName(result, "TestObject")
    }

    @Test
    fun normalClass() {
        val result = extractClassAndPackageName(DeclaringClass::class)

        assertClassName(result, "DeclaringClass")
    }

    @Test
    fun companionObject() {
        val result = extractClassAndPackageName(DeclaringClass.Companion::class)

        if (Platform.type == PlatformType.WasmJs) {
            assertClassName(result, "Companion")
        } else if (Platform.isJsBrowserOrNodeJs) { // for Companions JS only returns "Companion_<index>"
            assertClassName(result, "Companion_3")
        } else {
            assertClassName(result, "DeclaringClass.Companion")
        }
    }


    @Test
    fun inlineClass() {
        val result = extractClassAndPackageName(TestInlineClass::class)

        assertClassName(result, "TestInlineClass")
    }


    private fun assertClassName(result: ClassAndPackageName, expectedClassName: String, expectedPackageName: String = TestObject.packageName) {
        assertThat(result::className).isEqualTo(expectedClassName)

        if (TestPlatform.SupportsPackageNames) {
            assertThat(result::packageName).isEqualTo(expectedPackageName)
        }
    }


    private fun <T : Any> extractClassAndPackageName(kClass: KClass<T>): ClassAndPackageName {
        val classInfo = LogFormatterPlatform.getClassInfo(kClass)

        return underTest.extractClassAndPackageName(classInfo.qualifiedClassName ?: classInfo.classNameWithoutPackageName ?: kClass.toString())
    }

}