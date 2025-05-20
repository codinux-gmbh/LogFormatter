package net.codinux.log.formatter

import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent

open class DefaultLogEventFormatter : LogEventFormatter {

    override fun formatEvent(event: LogEvent): String =
        "${event.level.toString().padEnd(5, ' ')} ${event.loggerName} " +
                "${event.threadName?.let { "[$it] " } ?: ""}- ${formatMessage(event.message, event.exception)}"

    open fun formatMessage(message: String, exception: Throwable?): String =
        if (exception != null) {
            "$message:${LineSeparator.System}${exception.stackTraceToString()}"
        } else {
            message
        }

}