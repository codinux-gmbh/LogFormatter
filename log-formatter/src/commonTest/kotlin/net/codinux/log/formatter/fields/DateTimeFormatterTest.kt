package net.codinux.log.formatter.fields

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.log.LogEvent
import net.codinux.log.LogLevel
import net.dankito.datetime.LocalDateTime
import kotlin.test.Test

class DateTimeFormatterTest {

    companion object {
        private val dateTime = LocalDateTime(2015, 10, 21, 9, 8, 7, 654_321_000)
    }


    @Test
    fun defaultPattern() {
        val result = DateTimeFormatter().format(event())

        assertThat(result).isEqualTo("2015-10-21 09:08:07,654")
    }

    @Test
    fun defaultPattern_MaxWidth() {
        val format = FieldFormat(maxWidth = 12)

        val result = DateTimeFormatter(format).format(event())

        assertThat(result).isEqualTo("09:08:07,654")
    }

    @Test
    fun defaultPattern_MaxWidth_TruncateEnd() {
        val format = FieldFormat(maxWidth = 10, truncate = FieldFormat.Truncate.End)

        val result = DateTimeFormatter(format).format(event())

        assertThat(result).isEqualTo("2015-10-21")
    }


    @Test
    fun patternEnclosingInQuotes() {
        val pattern = "\"dd.MM.yyyy\""

        val result = DateTimeFormatter(null, pattern).format(event())

        assertThat(result).isEqualTo("21.10.2015")
    }

    @Test
    fun patternEnclosingInApostrophes() {
        val pattern = "'dd.MM.yyyy'"

        val result = DateTimeFormatter(null, pattern).format(event())

        assertThat(result).isEqualTo("21.10.2015")
    }


    @Test
    fun timeOnly() {
        val pattern = "HH:mm:ss"

        val result = DateTimeFormatter(null, pattern).format(event())

        assertThat(result).isEqualTo("09:08:07")
    }

    @Test
    fun timeOnly_minWidth() {
        val pattern = "HH:mm:ss"
        val format = FieldFormat(minWidth = 10)

        val result = DateTimeFormatter(format, pattern).format(event())

        assertThat(result).isEqualTo("  09:08:07")
    }

    @Test
    fun timeOnly_minWidth_PadEnd() {
        val pattern = "HH:mm:ss"
        val format = FieldFormat(minWidth = 10, pad = FieldFormat.Padding.End)

        val result = DateTimeFormatter(format, pattern).format(event())

        assertThat(result).isEqualTo("09:08:07  ")
    }


    @Test
    fun customPattern() {
        val pattern = "HH:mm:ss.SSS dd.MM.yyyy"

        val result = DateTimeFormatter(null, pattern).format(event())

        assertThat(result).isEqualTo("09:08:07.654 21.10.2015")
    }


    private fun event(dateTime: LocalDateTime = Companion.dateTime) = LogEvent(
        dateTime.toInstantAtSystemTimeZone(),
        LogLevel.Info, "Test message", "DateTimeFormatterTest"
    )

}