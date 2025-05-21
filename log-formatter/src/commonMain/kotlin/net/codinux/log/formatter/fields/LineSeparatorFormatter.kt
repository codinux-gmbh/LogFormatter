package net.codinux.log.formatter.fields

import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent

open class LineSeparatorFormatter(
    protected open val lineSeparator: String = LineSeparator.System,
    format: FieldFormat? = null
) : FieldFormatter(format) {

    override fun getField(event: LogEvent): String = lineSeparator


    override fun toString() = "Line separator: ${lineSeparator.map { when (it) {
            '\n' -> "\\n"
            '\r' -> "\\r"
            else -> it.toString()
        }
    }}"

}