package net.codinux.log.formatter.fields

import net.codinux.kotlin.text.LineSeparator

open class LineSeparatorFormatter(
    lineSeparator: String = LineSeparator.System
) : LiteralFormatter(lineSeparator) {

    override fun toString() = "Line separator: ${literal.map { when (it) {
            '\n' -> "\\n"
            '\r' -> "\\r"
            else -> it.toString()
        }
    }}"

}