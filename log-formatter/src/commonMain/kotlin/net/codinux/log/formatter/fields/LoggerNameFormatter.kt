package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

open class LoggerNameFormatter : FieldFormatter() {

    override fun getField(event: LogEvent): String =
        event.loggerName

}