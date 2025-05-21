package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent

abstract class FieldFormatter : LogLinePartFormatter {

    companion object {
        const val FieldValueNotAvailable = ""
    }


    protected abstract fun getField(event: LogEvent): String


    override fun convertTo(event: LogEvent): String =
        getField(event)

}