package net.codinux.log.formatter

import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent
import net.codinux.log.stacktrace.StackTraceFormatter

/**
 * Formats log events with pattern `%-5level [%logger{36}] (%thread) - %msg%n%ex`, e.g.:
 * ```
 * Info  [org.company.domain.user.UserService] (main) - User 'John Doe' was created.
 * Error [org.company.domain.user.UserService] (main) - Failed to create user 'John Doe'
 * java.lang.IllegalStateException: User already exists.
 * <stacktrace>
 * ```
 */
open class SimpleLogEventFormatter(
    protected open val stackTraceFormatter: StackTraceFormatter = StackTraceFormatter.Default
) : LogEventFormatter {

    companion object {
        val Default by lazy { SimpleLogEventFormatter() }
    }


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