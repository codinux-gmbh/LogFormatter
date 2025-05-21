package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

interface LogLinePartFormatter {

    fun convertTo(event: LogEvent): String

}