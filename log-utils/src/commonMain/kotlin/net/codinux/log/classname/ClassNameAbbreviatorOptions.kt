package net.codinux.log.classname

data class ClassNameAbbreviatorOptions(
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
        val Default = ClassNameAbbreviatorOptions()
    }
}