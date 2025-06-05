package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent
import net.dankito.datetime.format.pattern.DateTimeComponentFormatter

open class DateTimeFormatter(
    format: FieldFormat? = null,
    protected val dateTimePattern: String? = null,
    protected val formatter: DateTimeComponentFormatter = DateTimeComponentFormatter.Default
) : FieldFormatter(format) {

    companion object {
        const val DefaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss,SSS" // the same default as in Logback and JBoss Logging
    }


    override fun getField(event: LogEvent): String =
        if (event.timestamp == null) {
            FieldValueNotAvailable
        } else {
            val dateTime = event.timestamp!!.toLocalDateTimeAtSystemTimeZone()

            val pattern = dateTimePattern ?: DefaultDateTimeFormat

            formatter.format(dateTime, pattern)
        }

}