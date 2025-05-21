package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

open class LoggerNameFormatter(format: FieldFormat? = null) : FieldFormatter(format) {

    override fun getField(event: LogEvent): String =
        event.loggerName

}