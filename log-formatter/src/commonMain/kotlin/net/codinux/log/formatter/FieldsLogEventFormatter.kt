package net.codinux.log.formatter

import net.codinux.log.LogEvent
import net.codinux.log.formatter.fields.*

open class FieldsLogEventFormatter(
    protected open val fields: List<LogLinePartFormatter> = DefaultFields
) : LogEventFormatter {

    companion object {
        private val DefaultFields = listOf(
            LogLevelFormatter(),
            WhitespaceFormatter,
            LoggerNameFormatter(),
            LiteralFormatter(" ["),
            ThreadNameFormatter(),
            LiteralFormatter("] "),
            MessageFormatter(),
            LineSeparatorFormatter(),
            ThrowableFormatter()
        )
    }


    constructor(vararg fields: LogLinePartFormatter) : this(fields.toList())


    override fun formatEvent(event: LogEvent): String = buildString {
        fields.forEach { field ->
            append(field.convertTo(event))
        }
    }

}