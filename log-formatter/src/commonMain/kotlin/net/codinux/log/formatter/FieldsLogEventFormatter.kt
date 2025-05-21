package net.codinux.log.formatter

import net.codinux.log.LogEvent
import net.codinux.log.formatter.fields.LogLinePartFormatter

open class FieldsLogEventFormatter(
    protected open val fields: List<LogLinePartFormatter>
) : LogEventFormatter {

    override fun formatEvent(event: LogEvent): String = buildString {
        fields.forEach { field ->
            append(field.convertTo(event))
        }
    }

}