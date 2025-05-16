package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.log.test.TestInlineClass
import net.codinux.log.test.DeclaringClass
import net.codinux.log.test.TestObject
import net.codinux.log.test.TestPlatform
import kotlin.test.Test

class ClassNameResolverTest {

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

        assertClassName(result, "DeclaringClass.Companion", companionOwnerClassName = "DeclaringClass")
    }

    @Test
    fun getClassNameComponents_InnerClass() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass::class)

        assertClassName(result, "DeclaringClass.InnerClass", "DeclaringClass")
    }

    @Test
    fun getClassNameComponents_InnerClassCompanionObject() {
        val result = underTest.getClassNameComponents(DeclaringClass.InnerClass.Companion::class)

        assertClassName(result, "DeclaringClass.InnerClass.Companion", "DeclaringClass", "DeclaringClass.InnerClass")
    }

    @Test
    fun getClassNameComponents_InlineClass() {
        val result = underTest.getClassNameComponents(TestInlineClass::class)

        assertClassName(result, "TestInlineClass")
    }


    private fun assertClassName(result: ClassNameComponents, className: String, declaringClassName: String? = null,
                                companionOwnerClassName: String? = null, packageName: String = DeclaringClass.packageName) {

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