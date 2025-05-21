package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

open class LogLevelFormatter : FieldFormatter() {

    override fun getField(event: LogEvent): String =
        event.level.toString()

}