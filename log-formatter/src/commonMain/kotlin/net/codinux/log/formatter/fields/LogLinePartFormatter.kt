package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

interface LogLinePartFormatter {

    fun format(event: LogEvent): String

}