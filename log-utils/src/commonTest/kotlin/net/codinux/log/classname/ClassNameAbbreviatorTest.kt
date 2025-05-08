package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ClassNameAbbreviatorTest {

    companion object {
        private const val LongPackageName = "org.company.project.module.submodule.feature.service."
        private const val LongPackageNameFirstCharPerSegmentOnly = "o.c.p.m.s.f.s."
        private val CountLongPackageNameSegments = LongPackageName.split('.').size

        private const val LongClassName = "VeryLongClassNameThatSimplyDoesNotEnd"
        private const val ShortClassName = "NameAbbreviator"
    }

    private val underTest = ClassNameAbbreviator()


    @Test
    fun classNameShorterThanMaxLength_KeepClassName() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, ShortClassName.length - 1)

        assertThat(result).isEqualTo(ShortClassName)
    }


    @Test
    fun fillPackageSegmentsEqually_OneCharPerSegment() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length + CountLongPackageNameSegments * 2)

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + LongClassName)
    }

    @Test
    fun fillPackageSegmentsEqually_ThreeCharsPerSegment() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length + CountLongPackageNameSegments * 4)

        assertThat(result).isEqualTo("org.com.pro.mod.sub.fea.ser." + LongClassName)
    }

    @Test
    fun fillPackageSegmentsEqually_MoreCharsThenSomeSegmentsHave() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length + CountLongPackageNameSegments * 6)

        assertThat(result).isEqualTo("org.compa.proje.modul.submo.featu.servi." + LongClassName)
    }

}