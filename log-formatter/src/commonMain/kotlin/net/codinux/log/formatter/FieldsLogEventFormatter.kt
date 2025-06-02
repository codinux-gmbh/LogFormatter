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


    protected open val messageFields: List<LogLinePartFormatter> = getMessageFields(fields)


    override fun formatMessage(event: LogEvent): String = formatFields(messageFields, event)

    override fun formatEvent(event: LogEvent): String = formatFields(fields, event)

    protected open fun formatFields(fields: Collection<LogLinePartFormatter>, event: LogEvent): String = buildString {
        fields.forEach { field ->
            append(field.format(event))
        }
    }


    protected open fun getMessageFields(fields: List<LogLinePartFormatter>): List<LogLinePartFormatter> {
        val messageIndex = fields.indexOfFirst { it is MessageFormatter }
        val throwableIndex = fields.indexOfFirst { it is ThrowableFormatter }

        if (messageIndex == -1 && throwableIndex == -1) {
            return emptyList()
        } else if (messageIndex == -1) {
            return listOf(fields[throwableIndex])
        } else if (throwableIndex == -1) {
            return listOf(fields[messageIndex])
        }

        val messageAndThrowablePart = fields.subList(messageIndex, throwableIndex)

        return messageAndThrowablePart.filter {
            it is MessageFormatter || it is ThrowableFormatter || it is LineSeparatorFormatter || it is LiteralFormatter
        }
    }


    override fun toString() = "Fields LogEventFormatter with fields: $fields"

}