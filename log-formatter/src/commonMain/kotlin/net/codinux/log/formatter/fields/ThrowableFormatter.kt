package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent
import net.codinux.log.stacktrace.StackTraceFormatter
import net.codinux.log.stacktrace.StackTraceFormatterOptions

open class ThrowableFormatter(
    format: FieldFormat? = null,
    protected open val stackTraceFormatter: StackTraceFormatter =
        StackTraceFormatter(StackTraceFormatterOptions(addLineSeparatorAtEnd = true))
) : FieldFormatter(format) {

    constructor(options: StackTraceFormatterOptions) : this(null, options)

    constructor(format: FieldFormat? = null, options: StackTraceFormatterOptions)
            : this(format, StackTraceFormatter(options))


    override fun getField(event: LogEvent): String =
        event.exception?.let { stackTraceFormatter.format(it) }
            ?: FieldValueNotAvailable

}