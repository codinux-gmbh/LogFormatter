package net.codinux.log.formatter

import assertk.assertThat
import assertk.assertions.*
import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent
import net.codinux.log.LogLevel
import net.codinux.log.formatter.fields.LiteralFormatter
import net.codinux.log.formatter.fields.ThreadNameFormatter
import kotlin.test.Test

class FieldsLogEventFormatterTest {

    companion object {
        private val EventWithoutThrowable = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main")

        private val EventWithThrowable = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main", Throwable("No animals have been harmed"))
    }


    @Test
    fun defaultFields_WithoutThrowable() {
        val result = FieldsLogEventFormatter().formatEvent(EventWithoutThrowable)

        assertThat(result).isEqualTo("Info UserService [main] Just a test message${LineSeparator.System}")
    }

    @Test
    fun defaultFields_WithThrowable() {
        val result = FieldsLogEventFormatter().formatEvent(EventWithThrowable)

        val lines = result.lines()
        assertThat(lines.size).isGreaterThan(2)
        assertThat(lines.first()).isEqualTo("Info UserService [main] Just a test message")
        assertThat(lines[1]).endsWith("Throwable: No animals have been harmed")
        assertThat(result).endsWith(LineSeparator.System)
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