package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

open class LiteralFormatter(
    open val literal: String
) : LogLinePartFormatter {

    override fun convertTo(event: LogEvent) = literal


    override fun toString() = "Literal: '$literal'"
}