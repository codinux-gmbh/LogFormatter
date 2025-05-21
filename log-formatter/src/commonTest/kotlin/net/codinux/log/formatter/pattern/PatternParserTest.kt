package net.codinux.log.formatter.pattern

import assertk.assertThat
import assertk.assertions.*
import net.codinux.log.LogEvent
import net.codinux.log.LogLevel
import net.codinux.log.formatter.fields.*
import kotlin.test.Test

class PatternParserTest {

    companion object {
        private val Event = LogEvent(LogLevel.Info, "Just a test message", "UserService", "main")
    }


    private val underTest = PatternParser()


    @Test
    fun parse() {
        val result = underTest.parse(PatternParser.DefaultPattern)

        assertThat(result).hasSize(9)

        assertThat(result[0]).isInstanceOf<LogLevelFormatter>()
        assertThat(result[1]).isInstanceOf<LiteralFormatter>()
        assertThat(result[2]).isInstanceOf<LoggerNameFormatter>()
        assertThat(result[3]).isInstanceOf<LiteralFormatter>()
        assertThat(result[4]).isInstanceOf<ThreadNameFormatter>()
        assertThat(result[5]).isInstanceOf<LiteralFormatter>()
        assertThat(result[6]).isInstanceOf<MessageFormatter>()
        assertThat(result[7]).isInstanceOf<LineSeparatorFormatter>()
        assertThat(result[8]).isInstanceOf<ThrowableFormatter>()
    }


    /*          Format specifiers         */

    @Test
    fun padStart() {
        val result = underTest.parse("%5level")

        val formatted = assertFieldFormat(result, minWidth = 5, padding = FieldFormat.Padding.Start, maxWidth = null)
        assertThat(formatted).isEqualTo(" Info")
    }

    @Test
    fun padEnd() {
        val result = underTest.parse("%-5level")

        val formatted = assertFieldFormat(result, minWidth = 5, padding = FieldFormat.Padding.End, maxWidth = null)
        assertThat(formatted).isEqualTo("Info ")
    }

    @Test
    fun truncateStart() {
        val result = underTest.parse("%.3level")

        val formatted = assertFieldFormat(result, maxWidth = 3, truncate = FieldFormat.Truncate.Start, minWidth = null)
        assertThat(formatted).isEqualTo("nfo")
    }

    @Test
    fun truncateEnd() {
        val result = underTest.parse("%.-3level")

        val formatted = assertFieldFormat(result, maxWidth = 3, truncate = FieldFormat.Truncate.End, minWidth = null)
        assertThat(formatted).isEqualTo("Inf")
    }


    private fun assertFieldFormat(result: List<LogLinePartFormatter>, minWidth: Int? = null, maxWidth: Int? = null,
                                  padding: FieldFormat.Padding = FieldFormat.Padding.Start, truncate: FieldFormat.Truncate = FieldFormat.Truncate.Start): String {
        assertThat(result).hasSize(1)

        val fieldFormatter = result.first()
        assertThat(fieldFormatter).isInstanceOf<LogLevelFormatter>()

        assertThat((fieldFormatter as LogLevelFormatter).format).isNotNull()
        assertThat(fieldFormatter.format!!.minWidth).isEqualTo(minWidth)
        assertThat(fieldFormatter.format!!.pad).isEqualTo(padding)
        assertThat(fieldFormatter.format!!.maxWidth).isEqualTo(maxWidth)
        assertThat(fieldFormatter.format!!.truncate).isEqualTo(truncate)

        return fieldFormatter.format(Event)
    }

}