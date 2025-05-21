package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent
import net.codinux.log.stacktrace.StackTraceFormatter
import net.codinux.log.stacktrace.StackTraceFormatterOptions

open class ThrowableFormatter(
    format: FieldFormat? = null,
    protected open val stackTraceFormatter: StackTraceFormatter =
        StackTraceFormatter(StackTraceFormatterOptions(addLineSeparatorAtEnd = true))
) : FieldFormatter(format) {

    override fun getField(event: LogEvent): String =
        event.exception?.let { stackTraceFormatter.format(it) }
            ?: FieldValueNotAvailable

}