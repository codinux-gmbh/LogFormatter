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

    @Test
    fun defaultFields_WithoutThrowable() {
        val event = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main")


        val result = FieldsLogEventFormatter().formatEvent(event)


        assertThat(result).isEqualTo("Info UserService [main] Just a test message${LineSeparator.System}")
    }

    @Test
    fun defaultFields_WithThrowable() {
        val event = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main", Throwable("No animals have been harmed"))


        val result = FieldsLogEventFormatter().formatEvent(event)

        val lines = result.lines()
        assertThat(lines.size).isGreaterThan(2)
        assertThat(lines.first()).isEqualTo("Info UserService [main] Just a test message")
        assertThat(lines[1]).endsWith("Throwable: No animals have been harmed")
        assertThat(result).endsWith(LineSeparator.System)
    }


    /*      Single fields       */

    @Test
    fun threadNameIsNull() {
        val event = LogEvent(LogLevel.Info, "Just a test message", "UserService", threadName = null)

        val result = FieldsLogEventFormatter(
            LiteralFormatter(" ["),
            ThreadNameFormatter(),
            LiteralFormatter("] ")
        ).formatEvent(event)


        assertThat(result).isEqualTo(" [] ")
    }

}