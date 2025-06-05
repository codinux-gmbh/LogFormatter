package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

abstract class FieldFormatter(
    open val format: FieldFormat? = null,
    options: String? = null,
) : LogLinePartFormatter {

    companion object {
        const val FieldValueNotAvailable = ""
    }


    protected abstract fun getField(event: LogEvent): String


    protected open val optionsList: List<String> by lazy { options?.split(",") ?: emptyList() }

    open val firstOption: String? by lazy { unwrapOption(optionsList.firstOrNull()) }

    open val firstOptionAsInt: Int? by lazy { firstOption?.toIntOrNull() }

    open val secondOption: String? by lazy {
        if (optionsList.size >= 2) {
            unwrapOption(optionsList[1])
        } else {
            null
        }
    }

    open val secondOptionAsInt: Int? by lazy { secondOption?.toIntOrNull() }

    protected open fun unwrapOption(option: String?): String? =
        if (option == null) {
            null
        } else if (option.startsWith('"') && option.endsWith('"')) {
            option.substring(1, option.length - 1)
        } else if (option.startsWith('\'') && option.endsWith('\'')) {
            option.substring(1, option.length - 1)
        } else {
            option
        }


    override fun format(event: LogEvent): String {
        val value = getField(event)

        val format = format
        if (format == null || format.isMinWidthOrMaxWidthSet == false) { // if min width or max width is not set, then format cannot be applied
            return value
        }

        return formatValue(value, format)
    }

    protected open fun formatValue(value: String, format: FieldFormat): String {
        val minWidth = format.minWidth ?: -1
        val maxWidth = format.maxWidth ?: Int.MAX_VALUE

        return if (value.length < minWidth) {
            when (format.pad) {
                FieldFormat.Padding.Start -> return value.padStart(minWidth, ' ')
                FieldFormat.Padding.End -> return value.padEnd(minWidth, ' ')
            }
        } else if (value.length > maxWidth) {
            when (format.truncate) {
                FieldFormat.Truncate.Start -> value.substring(value.length - maxWidth)
                FieldFormat.Truncate.End -> value.substring(0, maxWidth)
            }
        } else {
            value
        }
    }

}