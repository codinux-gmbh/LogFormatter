package net.codinux.log.formatter.pattern

import net.codinux.log.formatter.fields.*
import net.codinux.log.stacktrace.StackTraceFormatterOptions

open class PatternParser(
    protected open val parsePatternRegex: Regex = DefaultParsePatternRegex,
    protected open val customFieldsParser: ((fieldSpecifier: String, format: FieldFormat?, options: String?) -> FieldFormatter?)? = null
) {

    companion object {

        // original: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"; Logback adds exception automatically
        val LogbackDefaultPattern = "[%thread] %-5level %logger{36} - %msg%n%ex"

        // original: "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c{3.}] (%t) %s%e%n"; error message gets really written right after the log message
        val QuarkusDefaultPattern = "%-5p [%c{3.}] (%t) %msg%n%ex"

        val DefaultPattern = "%-5level [%logger{36}] (%thread) %msg%n%ex"

        val DefaultParsePatternRegex = Regex(
            "([^%]+)|" + // matches all parts that do not start with format specifier '%' = literal text
                    "(?:%" + // matches the format specifier '%' and therefore a format string...
                    "(?:(-)?(\\d+))?" + // optional minimum field width and pad field at end flag ('-')
                    "(?:\\.(-)?(\\d+))?" + // optional after a dot maximum field width and truncate field at end flag ('-')
                    "(\\w+)" + // the actual conversion word, like 'level', 'message', 'msg', ...
                    "(?:\\{([^}]*)\\})?" + // and optionally conversion parameters in curly braces
                    ")"
        )


        val Default = PatternParser()
    }


    open fun parse(pattern: String): List<LogLinePartFormatter> {
        val matches = parsePatternRegex.findAll(pattern).toList()

        return matches.map { match ->
            val literalText = match.groupValues[1]
            if (literalText.isNotEmpty()) {
                return@map LiteralFormatter(literalText)
            }

            val fieldSpecifier = match.groupValues[6]
            val options = match.groupValues[7].takeUnless { it.isBlank() } // not used yet
            val fieldFormat = parseFieldFormat(match)

            mapFieldFormatter(fieldSpecifier, fieldFormat, options)
        }
    }


    protected open fun mapFieldFormatter(fieldSpecifier: String, format: FieldFormat?, options: String?): FieldFormatter = when (fieldSpecifier) {
        "level", "le", "l" -> LogLevelFormatter(format)
        "logger", "lo", "c" -> LoggerNameFormatter(format) // TODO: option is: length (-> abbreviate logger name)
        "thread", "th", "t" -> ThreadNameFormatter(format)
        "message", "msg", "m" -> MessageFormatter(format)
        "exception", "throwable", "ex", "e" -> ThrowableFormatter(format) // TODO: option is: short, full or number of stack frames (per Throwable?)
        "rootException", "rEx" -> ThrowableFormatter(format, StackTraceFormatterOptions(rootCauseFirst = true)) // TODO: option is: short, full or number of stack frames (per Throwable?)
        "n" -> LineSeparatorFormatter(format = format)
        else -> customFieldsParser?.invoke(fieldSpecifier, format, options)
                ?: throw IllegalArgumentException("Unknown field specifier: $fieldSpecifier")
    }


    protected open fun parseFieldFormat(match: MatchResult): FieldFormat? {
        val padEnd = match.groupValues[2]
        val minWidth = match.groupValues[3]
        val truncateEnd = match.groupValues[4]
        val maxWidth = match.groupValues[5]

        return if (padEnd.isEmpty() && minWidth.isEmpty() && truncateEnd.isEmpty() && maxWidth.isEmpty()) {
            null
        } else {
            FieldFormat(
                minWidth = if (minWidth.isEmpty()) null else minWidth.toInt(),
                maxWidth = if (maxWidth.isEmpty()) null else maxWidth.toInt(),
                pad = if (padEnd == "-") FieldFormat.Padding.End else FieldFormat.Padding.Start,
                truncate = if (truncateEnd == "-") FieldFormat.Truncate.End else FieldFormat.Truncate.Start
            )
        }
    }

}