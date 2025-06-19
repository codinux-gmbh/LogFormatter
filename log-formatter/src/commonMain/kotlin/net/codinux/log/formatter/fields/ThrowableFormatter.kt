package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent
import net.codinux.log.stacktrace.StackTraceFormatter
import net.codinux.log.stacktrace.StackTraceFormatterOptions
import net.codinux.log.stacktrace.StackTraceShortenerOptions

open class ThrowableFormatter(
    format: FieldFormat? = null,
    options: String? = null,
    protected open val rootCauseFirst: Boolean = false,
) : FieldFormatter(format, options) {

    protected open val stackTraceFormatter: StackTraceFormatter by lazy {
        StackTraceFormatter(StackTraceFormatterOptions(addLineSeparatorAtEnd = true),
            StackTraceShortenerOptions(maxFramesPerThrowable = firstOptionAsInt, maxNestedThrowables = secondOptionAsInt, rootCauseFirst = rootCauseFirst)) }


    override fun getField(event: LogEvent): String =
        event.exception?.let { stackTraceFormatter.format(it) }
            ?: FieldValueNotAvailable

}