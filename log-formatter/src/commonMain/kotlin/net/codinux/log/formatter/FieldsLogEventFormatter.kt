package net.codinux.log.formatter

import net.codinux.log.LogEvent
import net.codinux.log.formatter.fields.*

/**
 * Formats log events according to the [LogLinePartFormatter] fields passed to the constructor.
 */
open class FieldsLogEventFormatter(
    protected open val fields: List<LogLinePartFormatter> = DefaultFields
) : LogEventFormatter {

    companion object {
        private val DefaultFields by lazy { listOf(
            LogLevelFormatter(FieldFormat(minWidth = 5, pad = FieldFormat.Padding.End)),
            LiteralFormatter.Whitespace,
            LoggerNameFormatter(),
            LiteralFormatter(" ["),
            ThreadNameFormatter(),
            LiteralFormatter("] "),
            MessageFormatter(),
            LineSeparatorFormatter(),
            ThrowableFormatter()
        ) }
    }


    constructor(vararg fields: LogLinePartFormatter) : this(fields.toList())


    override fun formatEvent(event: LogEvent): String = buildString {
        fields.forEach { field ->
            append(field.format(event))
        }
    }


    override fun toString() = "Fields LogEventFormatter with fields: $fields"

}