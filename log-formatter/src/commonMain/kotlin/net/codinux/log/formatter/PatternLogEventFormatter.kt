package net.codinux.log.formatter

import net.codinux.log.LogEvent
import net.codinux.log.formatter.pattern.PatternParser

/**
 * Formats log events according to the format pattern passed to the constructor.
 */
open class PatternLogEventFormatter(
    val pattern: String,
    parser: PatternParser = PatternParser.Default
) : LogEventFormatter {

    protected open val fieldsFormatter = parsePattern(pattern, parser)

    protected open fun parsePattern(pattern: String, parser: PatternParser): LogEventFormatter {
        val fields = parser.parse(pattern)

        return FieldsLogEventFormatter(fields)
    }


    override fun formatMessage(event: LogEvent) = fieldsFormatter.formatMessage(event)

    override fun formatEvent(event: LogEvent) = fieldsFormatter.formatEvent(event)


    override fun toString() = "Pattern LogEventFormatter with pattern '$pattern'.\n" +
            "Formatting of parsed pattern delegated to: $fieldsFormatter."

}