package net.codinux.log.formatter

import net.codinux.log.LogEvent
import net.codinux.log.formatter.fields.LogLinePartFormatter
import net.codinux.log.formatter.pattern.PatternParser

interface LogEventFormatter {

    companion object {
        val Default by lazy { DefaultLogEventFormatter.Default }

        fun fields(vararg fields: LogLinePartFormatter) = FieldsLogEventFormatter(*fields)

        fun fields(fields: List<LogLinePartFormatter>) = FieldsLogEventFormatter(fields)

        fun pattern(pattern: String, parser: PatternParser = PatternParser.Default) = PatternLogEventFormatter(pattern, parser)
    }


    fun formatEvent(event: LogEvent): String

}