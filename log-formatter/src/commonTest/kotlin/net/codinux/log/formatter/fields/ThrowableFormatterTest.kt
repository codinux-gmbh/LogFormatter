package net.codinux.log.formatter.fields

import assertk.assertThat
import assertk.assertions.*
import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.LogEvent
import net.codinux.log.LogLevel
import kotlin.test.Ignore
import kotlin.test.Test

class ThrowableFormatterTest {

    @Test
    fun defaultOptions() {
        val result = ThrowableFormatter().format(event())

        val lines = result.lines()
        assertThat(lines.size).isGreaterThan(8)

        assertThat(lines.first()).endsWith("Throwable: Outer Exception")
        assertThat(lines.any { it.endsWith("Throwable: Root cause") }).isTrue()

        assertThat(result).endsWith(LineSeparator.System)
    }

    @Test
    fun rootCauseFirst() {
        val result = ThrowableFormatter(rootCauseFirst = true).format(event())

        val lines = result.lines()
        assertThat(lines.size).isGreaterThan(8)

        assertThat(lines.first()).endsWith("Throwable: Root cause")
        assertThat(lines.any { it.endsWith("Throwable: Outer Exception") }).isTrue()

        assertThat(result).endsWith(LineSeparator.System)
    }


    @Test
    fun maxStackFrames() {
        val result = ThrowableFormatter(null, "2").format(event())

        val lines = result.lines()
        assertThat(lines.size).isIn(7, 9)

        assertThat(lines.first()).endsWith("Throwable: Outer Exception")
        assertThat(lines[4]).endsWith("Throwable: Root cause")

        assertThat(result).endsWith(LineSeparator.System)
    }

    @Test
    fun maxStackFrames_rootCauseFirst() {
        val result = ThrowableFormatter(null, "2", true).format(event())

        val lines = result.lines()
        assertThat(lines.size).isIn(7, 9)

        assertThat(lines.first()).endsWith("Throwable: Root cause")
        assertThat(lines.any { it.endsWith("Throwable: Outer Exception") }).isTrue()

        assertThat(result).endsWith(LineSeparator.System)
    }


    @Test
    fun maxNestedThrowables() {
        val result = ThrowableFormatter(null, "-1,0").format(event())

        val lines = result.lines()

        assertThat(lines.first()).endsWith("Throwable: Outer Exception")
        assertThat(result).doesNotContain("Throwable: Root cause")

        assertThat(result).endsWith(LineSeparator.System)
    }

    @Test
    fun maxNestedThrowables_rootCauseFirst() {
        val result = ThrowableFormatter(null, "-1,0", true).format(event())

        val lines = result.lines()

        assertThat(lines.first()).endsWith("Throwable: Root cause")
        assertThat(result).doesNotContain("Throwable: Outer Exception")

        assertThat(result).endsWith(LineSeparator.System)
    }


    private fun event(throwable: Throwable? = Throwable("Outer Exception", Throwable("Root cause"))) = LogEvent(
        LogLevel.Error, "Test message", "ThrowableFormatterTest", null, throwable
    )

}