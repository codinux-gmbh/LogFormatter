package net.codinux.log.classname

import kotlin.jvm.JvmOverloads

data class ClassNameAbbreviatorOptions @JvmOverloads constructor(
    /**
     * The abbreviation strategy that is used if class name exceeds `maxLength`.
     *
     * Defaults to [ClassNameAbbreviationStrategy.EllipsisEnd].
     */
    val classNameAbbreviation: ClassNameAbbreviationStrategy = ClassNameAbbreviationStrategy.EllipsisEnd,

    /**
     * The package name abbreviation strategy that is used if class name and package name exceed
     * `maxLength`.
     *
     * Defaults to [PackageAbbreviationStrategy.FillSegmentsEqually].
     */
    val packageAbbreviation: PackageAbbreviationStrategy = PackageAbbreviationStrategy.FillSegmentsEqually,

    /**
     * The ellipsis string that is used if class name exceeds `maxLength` and [classNameAbbreviation] is set
     * to [ClassNameAbbreviationStrategy.EllipsisStart], [ClassNameAbbreviationStrategy.EllipsisMiddle] or
     * [ClassNameAbbreviationStrategy.EllipsisEnd].
     *
     * Defaults to `"..."`.
     */
    val classNameAbbreviationEllipsis: String = "..."
) {
    companion object {
        val Default by lazy { ClassNameAbbreviatorOptions() }

        /**
         * Mimics the same behavior of Logback: Fill package segments from the end and keep class name and
         * first character of each package segment even if it exceeds `maxLength`.
         */
        val Logback = ClassNameAbbreviatorOptions(
            classNameAbbreviation = ClassNameAbbreviationStrategy.KeepClassNameAndFirstCharacterOfEachPackageSegmentEvenIfLonger,
            packageAbbreviation = PackageAbbreviationStrategy.FillSegmentsFromEnd,
            classNameAbbreviationEllipsis = "..."
        )
    }
}