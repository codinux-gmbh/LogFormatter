package net.codinux.log.formatter.fields

open class FieldFormat(
    open val minWidth: Int? = null,
    maxWidth: Int? = null,
    open val pad: Padding = Padding.Start, // don't like this default, but it's the default value of Logback
    open val truncate: Truncate = Truncate.Start, // don't like this default, but it's the default value of Logback
) {
    enum class Padding {
        Start,
        End
    }

    enum class Truncate {
        Start,
        End
    }


    open val maxWidth: Int? = if (maxWidth == 0) null else maxWidth

    open val isMinWidthOrMaxWidthSet: Boolean
        get() = (minWidth != null && (minWidth ?: -1) > 0)
                || (maxWidth != null && maxWidth!! > 0)


    override fun toString() = "minWidth=$minWidth, maxWidth=$maxWidth, pad=$pad, truncate=$truncate"

}