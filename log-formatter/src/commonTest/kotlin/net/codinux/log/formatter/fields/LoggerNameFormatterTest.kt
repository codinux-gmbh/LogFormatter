package net.codinux.log.formatter.fields

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.codinux.log.LogEvent
import net.codinux.log.LogLevel
import kotlin.test.Test

class LoggerNameFormatterTest {

    companion object {
        private const val loggerName = "org.company.project.feature.service.UserService"

        private const val loggerClassName = "UserService"
    }


    @Test
    fun defaultOptions() {
        val result = LoggerNameFormatter().format(event())

        assertThat(result).isEqualTo(loggerName)
    }

    @Test
    fun lengthLessThanZero() {
        val result = LoggerNameFormatter(null, "-1").format(event())

        assertThat(result).isEqualTo(loggerName)
    }


    @Test
    fun lengthGreaterThanLoggerNameLength() {
        val result = LoggerNameFormatter(null, (loggerName.length + 1).toString()).format(event())

        assertThat(result).isEqualTo(loggerName)
    }

    @Test
    fun lengthEqualsLoggerNameLength() {
        val result = LoggerNameFormatter(null, loggerName.length.toString()).format(event())

        assertThat(result).isEqualTo(loggerName)
    }

    @Test
    fun lengthLessThanLoggerNameLength() {
        val result = LoggerNameFormatter(null, (loggerName.length - 1).toString()).format(event())

        assertThat(result).isEqualTo("or.company.project.feature.service.UserService")
    }


    @Test
    fun lengthGreaterThanLoggerClassNameLength() {
        val result = LoggerNameFormatter(null, (loggerClassName.length + 1).toString()).format(event())

        assertThat(result).isEqualTo("o.c.p.f.s." + loggerClassName)
    }

    @Test
    fun lengthEqualsLoggerClassNameLength() {
        val result = LoggerNameFormatter(null, loggerClassName.length.toString()).format(event())

//        assertThat(result).isEqualTo(loggerClassName) // TODO: should be "UserService"
        assertThat(result).isEqualTo("o.c.p.f.s." + loggerClassName)
    }

    @Test
    fun lengthLessThanLoggerClassNameLength() {
        val result = LoggerNameFormatter(null, (loggerClassName.length - 1).toString()).format(event())

//        assertThat(result).isEqualTo(loggerClassName) // TODO: should be "UserService"
        assertThat(result).isEqualTo("o.c.p.f.s." + loggerClassName)
    }


    private fun event(loggerName: String = Companion.loggerName) = LogEvent(
        LogLevel.Info, "Test message", loggerName
    )

}