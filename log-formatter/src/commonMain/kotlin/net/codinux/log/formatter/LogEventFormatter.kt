package net.codinux.log.formatter

import net.codinux.log.LogEvent

interface LogEventFormatter {

    fun formatEvent(event: LogEvent): String

}