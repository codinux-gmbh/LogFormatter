package net.codinux.log.formatter

import assertk.assertThat
import assertk.assertions.*
import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent
import net.codinux.log.LogLevel
import net.codinux.log.formatter.fields.*
import kotlin.test.Test

class FieldsLogEventFormatterTest {

    companion object {
        private val EventWithoutThrowable = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main")

        private val EventWithThrowable = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main", Throwable("No animals have been harmed"))
    }


    @Test
    fun defaultFields_WithoutThrowable() {
        val result = FieldsLogEventFormatter().formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("Info  UserService [main] Just a test message${LineSeparator.System}")
    }

    @Test
    fun defaultFields_WithThrowable() {
        val result = FieldsLogEventFormatter().formatEvent(EventWithThrowable)

        val lines = result.lines()
        assertThat(lines.size).isGreaterThan(2)
        assertThat(lines.first()).isEqualTo("Info  UserService [main] Just a test message")
        assertThat(lines[1]).endsWith("Throwable: No animals have been harmed")
        assertThat(result).endsWith(LineSeparator.System)
    }


    @Test
    fun formatMessage_MessageOnly() {
        val formatter = FieldsLogEventFormatter(MessageFormatter())

        val result = formatter.formatEvent(EventWithThrowable) // Throwable may not be formatted

        assertThat(result).isEqualTo(EventWithThrowable.message)
    }

    @Test
    fun formatMessage_ThrowableOnly() {
        val formatter = FieldsLogEventFormatter(ThrowableFormatter())

        val result = formatter.formatEvent(EventWithThrowable) // message may not be formatted

        assertThat(result).doesNotContain(EventWithThrowable.message)
        val lines = result.lines()
        assertThat(lines.first()).endsWith("Throwable: " + EventWithThrowable.exception!!.message!!)
        assertThat(lines.size).isGreaterThan(1)
    }

    @Test
    fun formatMessage_MesssageAndThrowableGetExtractedCorrectly() {
        val literal = " - willingly inserted for test -"
        val lineSeparator = LineSeparator.Unix
        val formatter = FieldsLogEventFormatter(MessageFormatter(), LiteralFormatter(literal), LineSeparatorFormatter(lineSeparator), ThrowableFormatter())

        val result = formatter.formatEvent(EventWithThrowable)

        assertThat(result).startsWith(EventWithThrowable.message + literal + lineSeparator)
        val lines = result.lines()
        assertThat(lines[1]).endsWith("Throwable: " + EventWithThrowable.exception!!.message!!)
        assertThat(lines.size).isGreaterThan(2)
    }


    /*      Formatting          */

    @Test
    fun padStart() {
        val underTest = FieldsLogEventFormatter(LogLevelFormatter(FieldFormat(minWidth = 6, pad = FieldFormat.Padding.Start)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("  Info")
    }

    @Test
    fun padEnd() {
        val underTest = FieldsLogEventFormatter(LogLevelFormatter(FieldFormat(minWidth = 6, pad = FieldFormat.Padding.End)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("Info  ")
    }

    @Test
    fun minWidthNotSet_NoPadding() {
        val underTest = FieldsLogEventFormatter(LogLevelFormatter(FieldFormat(minWidth = null, pad = FieldFormat.Padding.End)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("Info")
    }

    @Test
    fun minWidthZero_NoPadding() {
        val underTest = FieldsLogEventFormatter(LogLevelFormatter(FieldFormat(minWidth = 0)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("Info")
    }


    @Test
    fun truncateStart() {
        val underTest = FieldsLogEventFormatter(LoggerNameFormatter(FieldFormat(maxWidth = 6, truncate = FieldFormat.Truncate.Start)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("ervice")
    }

    @Test
    fun truncateEnd() {
        val underTest = FieldsLogEventFormatter(LoggerNameFormatter(FieldFormat(maxWidth = 6, truncate = FieldFormat.Truncate.End)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("UserSe")
    }

    @Test
    fun maxWidthNotSet_NoTruncation() {
        val underTest = FieldsLogEventFormatter(LoggerNameFormatter(FieldFormat(maxWidth = null, truncate = FieldFormat.Truncate.End)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("UserService")
    }

    @Test
    fun maxWidthZero_NoTruncation() {
        val underTest = FieldsLogEventFormatter(LoggerNameFormatter(FieldFormat(maxWidth = 0)))

        val result = underTest.formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("UserService")
    }


    /*      Single fields       */

    @Test
    fun threadNameIsNull() {
        val underTest = FieldsLogEventFormatter(
            LiteralFormatter(" ["),
            ThreadNameFormatter(),
            LiteralFormatter("] ")
        )
        val event = LogEvent(LogLevel.Info, "Just a test message", "UserService", threadName = null)

        val result = underTest.formatEvent(event)


        assertThat(result).isEqualTo(" [] ")
    }

}