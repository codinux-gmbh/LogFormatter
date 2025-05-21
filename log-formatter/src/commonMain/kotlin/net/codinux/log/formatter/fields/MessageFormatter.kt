package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

open class MessageFormatter : FieldFormatter() {

    override fun getField(event: LogEvent): String =
        event.message

}