package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

abstract class FieldFormatter(
    protected open val format: FieldFormat? = null
) : LogLinePartFormatter {

    companion object {
        const val FieldValueNotAvailable = ""
    }


    protected abstract fun getField(event: LogEvent): String


    override fun convertTo(event: LogEvent): String {
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