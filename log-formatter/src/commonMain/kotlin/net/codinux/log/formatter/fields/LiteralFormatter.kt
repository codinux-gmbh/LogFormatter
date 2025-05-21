package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

open class LiteralFormatter(
    open val literal: String
) : LogLinePartFormatter {

    companion object {
        val Whitespace = LiteralFormatter(" ")
    }


    override fun format(event: LogEvent) = literal


    override fun toString() = "Literal: '$literal'"
}