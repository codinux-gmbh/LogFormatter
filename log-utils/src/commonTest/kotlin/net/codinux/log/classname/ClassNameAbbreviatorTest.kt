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
        val result = underTest.abbreviate(LongPackageName + LongClassName,
            LongClassName.length + CountLongPackageNameSegments * 2,
            PackageAbbreviationStrategy.FillSegmentsEqually)

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + LongClassName)
    }

    @Test
    fun fillPackageSegmentsEqually_ThreeCharsPerSegment() {
        val result = underTest.abbreviate(LongPackageName + LongClassName,
            LongClassName.length + CountLongPackageNameSegments * 4,
            PackageAbbreviationStrategy.FillSegmentsEqually)

        assertThat(result).isEqualTo("org.com.pro.mod.sub.fea.ser." + LongClassName)
    }

    @Test
    fun fillPackageSegmentsEqually_MoreCharsThenSomeSegmentsHave() {
        val result = underTest.abbreviate(LongPackageName + LongClassName,
            LongClassName.length + CountLongPackageNameSegments * 6,
            PackageAbbreviationStrategy.FillSegmentsEqually)

        assertThat(result).isEqualTo("org.compa.proje.modul.submo.featu.servi." + LongClassName)
    }


    @Test
    fun fillPackageSegmentsFromTheStart_FirstSegmentOnly() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 2, PackageAbbreviationStrategy.FillSegmentsFromStart)

        assertThat(result).isEqualTo("org.c.p.m.s.f.s." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheStart_FirstFourSegments() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 2 + 6 + 6 + 5,
            PackageAbbreviationStrategy.FillSegmentsFromStart)

        assertThat(result).isEqualTo("org.company.project.module.s.f.s." + ShortClassName)
    }


    @Test
    fun fillPackageSegmentsFromTheEnd_LastSegmentOnly() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 6,
            PackageAbbreviationStrategy.FillSegmentsFromEnd)

        assertThat(result).isEqualTo("o.c.p.m.s.f.service." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheEnd_LastThreeSegments() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 6 + 6 + 8,
            PackageAbbreviationStrategy.FillSegmentsFromEnd)

        assertThat(result).isEqualTo("o.c.p.m.submodule.feature.service." + ShortClassName)
    }

}