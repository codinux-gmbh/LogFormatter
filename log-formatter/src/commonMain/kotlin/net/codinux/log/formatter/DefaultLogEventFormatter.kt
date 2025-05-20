package net.codinux.log.formatter

import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent
import net.codinux.log.stacktrace.StackTraceFormatter

open class DefaultLogEventFormatter(
    protected open val stackTraceFormatter: StackTraceFormatter = StackTraceFormatter.Default
) : LogEventFormatter {

    override fun formatEvent(event: LogEvent): String =
        "${event.level.toString().padEnd(5, ' ')} ${event.loggerName} " +
                "${event.threadName?.let { "[$it] " } ?: ""}- ${formatMessage(event.message, event.exception)}"

    open fun formatMessage(message: String, exception: Throwable?): String =
        if (exception != null) {
            "$message:${LineSeparator.System}${stackTraceFormatter.format(exception)}"
        } else {
            message
        }

}