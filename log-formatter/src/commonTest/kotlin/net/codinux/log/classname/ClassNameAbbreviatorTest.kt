package net.codinux.log.classname

import assertk.assertThat
import assertk.assertions.hasLength
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ClassNameAbbreviatorTest {

    companion object {
        private const val LongPackageName = "org.company.project.module.submodule.feature.service."
        private const val LongPackageNameFirstCharPerSegmentOnly = "o.c.p.m.s.f.s."
        private val CountLongPackageNameSegments = LongPackageName.split('.').size

        private const val LongClassName = "ServiceWithALongClassName"
        private const val ShortClassName = "NameAbbreviator"
    }

    private val underTest = ClassNameAbbreviator()


    /*          ClassNameAbbreviationStrategy tests             */

    @Test
    fun classNameShorterThanMaxLength_KeepClassNameEvenIfLonger() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, ShortClassName.length - 1,
            options(ClassNameAbbreviationStrategy.KeepClassNameEvenIfLonger))

        assertThat(result).isEqualTo(ShortClassName)
    }

    @Test
    fun classNameShorterThanMaxLength_ClipStart() {
        val maxLength = 8

        val result = underTest.abbreviate(LongPackageName + LongClassName, maxLength,
            options(ClassNameAbbreviationStrategy.ClipStart))

        assertThat(result).hasLength(maxLength)
        assertThat(result).isEqualTo("lassName")
    }

    @Test
    fun classNameShorterThanMaxLength_ClipEnd() {
        val maxLength = 8

        val result = underTest.abbreviate(LongPackageName + LongClassName, maxLength,
            options(ClassNameAbbreviationStrategy.ClipEnd))

        assertThat(result).hasLength(maxLength)
        assertThat(result).isEqualTo("ServiceW")
    }

    @Test
    fun classNameShorterThanMaxLength_EllipsisStart() {
        val maxLength = 8

        val result = underTest.abbreviate(LongPackageName + LongClassName, maxLength,
            options(ClassNameAbbreviationStrategy.EllipsisStart))

        assertThat(result).hasLength(maxLength)
        assertThat(result).isEqualTo("..ssName")
    }

    @Test
    fun classNameShorterThanMaxLength_EllipsisMiddle_EvenPartsLength() {
        val maxLength = 18

        val result = underTest.abbreviate(LongPackageName + LongClassName, maxLength,
            options(ClassNameAbbreviationStrategy.EllipsisMiddle))

        assertThat(result).hasLength(maxLength)
        assertThat(result).isEqualTo("ServiceW..lassName")
    }

    @Test
    fun classNameShorterThanMaxLength_EllipsisMiddle_OddPartsLength() {
        val maxLength = 19

        val result = underTest.abbreviate(LongPackageName + LongClassName, maxLength,
            options(ClassNameAbbreviationStrategy.EllipsisMiddle))

        assertThat(result).hasLength(maxLength)
        // if length of start and end parts is odd, start part is supposed to get the additional char
        assertThat(result).isEqualTo("ServiceWi..lassName")
    }

    @Test
    fun classNameShorterThanMaxLength_EllipsisEnd() {
        val maxLength = 8

        val result = underTest.abbreviate(LongPackageName + LongClassName, maxLength,
            options(ClassNameAbbreviationStrategy.EllipsisEnd))

        assertThat(result).hasLength(maxLength)
        assertThat(result).isEqualTo("Servic..")
    }


    /*              MinPackageNameTooLongStrategy tests             */

    @Test
    fun classNameShorterThanMaxLength_MinPackageNameTooLongStrategy_KeepEvenIfLongerThanMaxLength() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length + 1,
            options(MinPackageNameTooLongStrategy.KeepEvenIfLongerThanMaxLength))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + LongClassName)
    }

    @Test
    fun classNameShorterThanMaxLength_MinPackageNameTooLongStrategy_KeepOnlyIfMaxLengthLongerThanClassName() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length + 1,
            options(MinPackageNameTooLongStrategy.KeepOnlyIfMaxLengthLongerThanClassName))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + LongClassName)
    }

    @Test
    fun classNameShorterThanMaxLength_MinPackageNameTooLongStrategy_Omit() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length + 1,
            options(MinPackageNameTooLongStrategy.Omit))

        assertThat(result).isEqualTo(LongClassName)
    }

    @Test
    fun classNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepEvenIfLongerThanMaxLength() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length,
            options(MinPackageNameTooLongStrategy.KeepEvenIfLongerThanMaxLength))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + LongClassName)
    }

    @Test
    fun classNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepOnlyIfMaxLengthLongerThanClassName() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length,
            options(MinPackageNameTooLongStrategy.KeepOnlyIfMaxLengthLongerThanClassName))

        assertThat(result).isEqualTo(LongClassName)
    }

    @Test
    fun classNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_Omit() {
        val result = underTest.abbreviate(LongPackageName + LongClassName, LongClassName.length,
            options(MinPackageNameTooLongStrategy.Omit))

        assertThat(result).isEqualTo(LongClassName)
    }


    @Test
    fun minPackageNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepEvenIfLongerThanMaxLength() {
        val result = underTest.abbreviate(LongPackageName + "ClassName", LongPackageNameFirstCharPerSegmentOnly.length,
            options(MinPackageNameTooLongStrategy.KeepEvenIfLongerThanMaxLength))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + "ClassName")
    }

    @Test
    fun minPackageNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepOnlyIfMaxLengthLongerThanClassName_ClassNameShorterThanMaxLength() {
        val result = underTest.abbreviate(LongPackageName + "ClassName", LongPackageNameFirstCharPerSegmentOnly.length,
            options(MinPackageNameTooLongStrategy.KeepOnlyIfMaxLengthLongerThanClassName))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + "ClassName")
    }

    @Test
    fun minPackageNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepOnlyIfMaxLengthLongerThanClassName_ClassNameLengthEqualsMaxLength() {
        val maxLength = LongPackageNameFirstCharPerSegmentOnly.length
        val className = LongClassName.take(maxLength)

        val result = underTest.abbreviate(LongPackageName + className, maxLength,
            options(MinPackageNameTooLongStrategy.KeepOnlyIfMaxLengthLongerThanClassName))

        assertThat(result).isEqualTo(className)
    }

    @Test
    fun minPackageNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_Omit() {
        val result = underTest.abbreviate(LongPackageName + "net.codinux.log.ClassName", LongPackageNameFirstCharPerSegmentOnly.length,
            options(MinPackageNameTooLongStrategy.Omit))

        assertThat(result).isEqualTo("ClassName")
    }

    @Test
    fun minPackageNameWithClassNameLengthShorterThanMaxLength_MinPackageNameTooLongStrategy_KeepEvenIfLongerThanMaxLength() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, ShortClassName.length + 1,
            options(MinPackageNameTooLongStrategy.KeepEvenIfLongerThanMaxLength))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + ShortClassName)
    }

    @Test
    fun minPackageNameWithClassNameLengthShorterThanMaxLength_MinPackageNameTooLongStrategy_KeepOnlyIfMaxLengthLongerThanClassName() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, ShortClassName.length + 1,
            options(MinPackageNameTooLongStrategy.KeepOnlyIfMaxLengthLongerThanClassName))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + ShortClassName)
    }

    @Test
    fun minPackageNameWithClassNameLengthShorterThanMaxLength_MinPackageNameTooLongStrategy_Omit() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, ShortClassName.length + 1,
            options(MinPackageNameTooLongStrategy.Omit))

        assertThat(result).isEqualTo(ShortClassName)
    }

    @Test
    fun minPackageNameWithClassNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepEvenIfLongerThanMaxLength() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, LongPackageNameFirstCharPerSegmentOnly.length + ShortClassName.length,
            options(MinPackageNameTooLongStrategy.KeepEvenIfLongerThanMaxLength))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + ShortClassName)
    }

    @Test
    fun minPackageNameWithClassNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_KeepOnlyIfMaxLengthLongerThanClassName() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, LongPackageNameFirstCharPerSegmentOnly.length + ShortClassName.length,
            options(MinPackageNameTooLongStrategy.KeepOnlyIfMaxLengthLongerThanClassName))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + ShortClassName)
    }

    @Test
    fun minPackageNameWithClassNameLengthEqualsMaxLength_MinPackageNameTooLongStrategy_Omit() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName, LongPackageNameFirstCharPerSegmentOnly.length + ShortClassName.length,
            options(MinPackageNameTooLongStrategy.Omit))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + ShortClassName)
    }


    /*              PackageAbbreviationStrategy tests           */

    @Test
    fun fillPackageSegmentsEqually_OneCharPerSegment() {
        val result = underTest.abbreviate(LongPackageName + LongClassName,
            LongClassName.length + CountLongPackageNameSegments * 2,
            options(PackageAbbreviationStrategy.FillSegmentsEqually))

        assertThat(result).isEqualTo(LongPackageNameFirstCharPerSegmentOnly + LongClassName)
    }

    @Test
    fun fillPackageSegmentsEqually_ThreeCharsPerSegment() {
        val result = underTest.abbreviate(LongPackageName + LongClassName,
            LongClassName.length + CountLongPackageNameSegments * 4,
            options(PackageAbbreviationStrategy.FillSegmentsEqually))

        assertThat(result).isEqualTo("org.com.pro.mod.sub.fea.ser." + LongClassName)
    }

    @Test
    fun fillPackageSegmentsEqually_MoreCharsThenSomeSegmentsHave() {
        val result = underTest.abbreviate(LongPackageName + LongClassName,
            LongClassName.length + CountLongPackageNameSegments * 6,
            options(PackageAbbreviationStrategy.FillSegmentsEqually))

        assertThat(result).isEqualTo("org.compa.proje.modul.submo.featu.servi." + LongClassName)
    }


    @Test
    fun fillPackageSegmentsFromTheStart_FirstSegmentOnly() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 2,
            options(PackageAbbreviationStrategy.FillSegmentsFromStart))

        assertThat(result).isEqualTo("org.c.p.m.s.f.s." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheStart_FirstFourSegments() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 2 + 6 + 6 + 5,
            options(PackageAbbreviationStrategy.FillSegmentsFromStart))

        assertThat(result).isEqualTo("org.company.project.module.s.f.s." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheStart_FirstSegmentGetsFilledTillMaxLength() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 1,
            options(PackageAbbreviationStrategy.FillSegmentsFromStart))

        assertThat(result).isEqualTo("or.c.p.m.s.f.s." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheStart_LastSegmentGetsFilledTillMaxLength() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageName.length - 1,
            options(PackageAbbreviationStrategy.FillSegmentsFromStart))

        assertThat(result).isEqualTo("org.company.project.module.submodule.feature.servic." + ShortClassName)
    }


    @Test
    fun fillPackageSegmentsFromTheEnd_LastSegmentOnly() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 6,
            options(PackageAbbreviationStrategy.FillSegmentsFromEnd))

        assertThat(result).isEqualTo("o.c.p.m.s.f.service." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheEnd_LastThreeSegments() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 6 + 6 + 8,
            options(PackageAbbreviationStrategy.FillSegmentsFromEnd))

        assertThat(result).isEqualTo("o.c.p.m.submodule.feature.service." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheEnd_LastSegmentGetsFilledTillMaxLength() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageNameFirstCharPerSegmentOnly.length + 5,
            options(PackageAbbreviationStrategy.FillSegmentsFromEnd))

        assertThat(result).isEqualTo("o.c.p.m.s.f.servic." + ShortClassName)
    }

    @Test
    fun fillPackageSegmentsFromTheEnd_FirstSegmentGetsFilledTillMaxLength() {
        val result = underTest.abbreviate(LongPackageName + ShortClassName,
            ShortClassName.length + LongPackageName.length - 1,
            options(PackageAbbreviationStrategy.FillSegmentsFromEnd))

        assertThat(result).isEqualTo("or.company.project.module.submodule.feature.service." + ShortClassName)
    }


    private fun options(packageAbbreviationStrategy: PackageAbbreviationStrategy = ClassNameAbbreviatorOptions.Default.packageAbbreviation) =
        options(ClassNameAbbreviatorOptions.Default.classNameAbbreviation, packageAbbreviationStrategy = packageAbbreviationStrategy)

    private fun options(minPackageNameTooLongStrategy: MinPackageNameTooLongStrategy) =
        options(ClassNameAbbreviatorOptions.Default.classNameAbbreviation, minPackageNameTooLongStrategy)

    private fun options(
        classNameAbbreviationStrategy: ClassNameAbbreviationStrategy = ClassNameAbbreviatorOptions.Default.classNameAbbreviation,
        minPackageNameTooLongStrategy: MinPackageNameTooLongStrategy = MinPackageNameTooLongStrategy.Omit,
        packageAbbreviationStrategy: PackageAbbreviationStrategy = ClassNameAbbreviatorOptions.Default.packageAbbreviation,
        classNameAbbreviationEllipsis: String = ClassNameAbbreviatorOptions.Default.classNameAbbreviationEllipsis,
    ) =
        ClassNameAbbreviatorOptions(classNameAbbreviationStrategy, minPackageNameTooLongStrategy, packageAbbreviationStrategy, classNameAbbreviationEllipsis)

}