package net.codinux.log.formatter

import net.codinux.log.LogEvent
import net.codinux.log.formatter.fields.LogLinePartFormatter
import net.codinux.log.formatter.pattern.PatternParser

interface LogEventFormatter {

    companion object {
        val Simple by lazy { SimpleLogEventFormatter.Default }

        fun fields(vararg fields: LogLinePartFormatter) = FieldsLogEventFormatter(*fields)

        fun fields(fields: List<LogLinePartFormatter>) = FieldsLogEventFormatter(fields)

        fun pattern(pattern: String, parser: PatternParser = PatternParser.Default) = PatternLogEventFormatter(pattern, parser)
    }


    /**
     * Formats only the message and exception (may separated by literals) part of a LogEvent.
     *
     * Some logging backends like OSLog (Apple systems) and Logcat (Android) output the log level
     * and logger name themselves and we can only format the message and exception.
     */
    fun formatMessage(event: LogEvent): String

    /**
     * Formats the whole [LogEvent], that is - depending on formatter - next to message the
     * exception, log level, logger name, thread name, timestamp, ...
     */
    fun formatEvent(event: LogEvent): String

}