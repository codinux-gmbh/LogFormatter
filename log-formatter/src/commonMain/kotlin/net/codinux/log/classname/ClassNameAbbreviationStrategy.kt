package net.codinux.log.classname

enum class ClassNameAbbreviationStrategy {

    /**
     * Always keep the class name, even if it exceeds the specified `maxLength`.
     */
    KeepClassNameEvenIfLonger,

    /**
     * Keep the class name and the first character of each package segments,
     * even if the resulting string exceeds `maxLength`.
     *
     * Default behavior of Logback.
     *
     * E.g. `o.c.l.s.ClassNameAbbreviator`.
     */
    KeepClassNameAndFirstCharacterOfEachPackageSegmentEvenIfLonger,

    /**
     * Truncate characters from the beginning of the class name to fit `maxLength`.
     *
     * No ellipsis is added.
     */
    ClipStart,

    /**
     * Truncate characters from the end of the class name to fit `maxLength`.
     *
     * No ellipsis is added.
     */
    ClipEnd,

    /**
     * Truncate the start of the class name and prepend an ellipsis
     * (e.g., "...NameAbbreviator").
     *
     * The ellipsis string can be configured with [ClassNameAbbreviatorOptions.classNameAbbreviationEllipsis].
     */
    EllipsisStart,

    /**
     * Truncate the middle of the class name and insert an ellipsis
     * (e.g., "ClassNa...eviator").
     *
     * The ellipsis string can be configured with [ClassNameAbbreviatorOptions.classNameAbbreviationEllipsis].
     */
    EllipsisMiddle,

    /**
     * Truncate the end of the class name and append an ellipsis
     * (e.g., "ClassNameAbbr...").
     *
     * The ellipsis string can be configured with [ClassNameAbbreviatorOptions.classNameAbbreviationEllipsis].
     */
    EllipsisEnd

}