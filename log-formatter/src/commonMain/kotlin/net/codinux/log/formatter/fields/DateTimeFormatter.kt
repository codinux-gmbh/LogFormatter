package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent
import net.codinux.log.error.ErrorReporter
import net.dankito.datetime.format.pattern.DateTimeComponentFormatter

open class DateTimeFormatter(
    format: FieldFormat? = null,
    options: String? = null,
    protected val formatter: DateTimeComponentFormatter = DateTimeComponentFormatter.Default
) : FieldFormatter(format, options) {

    companion object {
        const val DefaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss,SSS" // the same default as in Logback and JBoss Logging
    }


    protected open val dateTimePattern: String? by lazy { firstOption }

    protected open val timeZone: String? by lazy { secondOption }


    override fun getField(event: LogEvent): String =
        if (event.timestamp == null) {
            FieldValueNotAvailable
        } else {
            val timestamp = event.timestamp!!
            val dateTime = if ("UTC".equals(timeZone, ignoreCase = true)) timestamp.toLocalDateTimeAtUtc()
                            else if (timeZone == null) timestamp.toLocalDateTimeAtSystemTimeZone()
                            else {
                                ErrorReporter.reportError("Time zone for date time format has been set to '$timeZone', " +
                                        "but the only currently supported time zone value is 'UTC'. Falling back to system time zone")
                                timestamp.toLocalDateTimeAtSystemTimeZone()
                            }

            val pattern = dateTimePattern ?: DefaultDateTimeFormat

            formatter.format(dateTime, pattern)
        }

}