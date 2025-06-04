package net.codinux.log.stacktrace

import assertk.assertThat
import assertk.assertions.*
import org.example.log.stack.StackTraceGenerator
import kotlin.test.Test

class StackTraceFormatterTest {

    private val stackTraceShortener = StackTraceShortener()

    private val underTest = StackTraceFormatter()


    @Test
    fun maxStackTraceStringLength() {
        val maxStackTraceStringLength = 100
        val options = StackTraceFormatterOptions(maxStackTraceStringLength = maxStackTraceStringLength)

        val result = underTest.format(StackTraceGenerator.generateTwoCausedBy(), options)

        assertThat(result.length).isEqualTo(maxStackTraceStringLength)
        assertDoesNotEndWithLineSeparator(result, options)
    }


    @Test
    fun maxFramesPerThrowable_2_SingleThrowable() {
        val maxFramesPerThrowable = 2
        val options = StackTraceFormatterOptions.Default

        val throwable = StackTraceGenerator.generateSingle()
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace)


        val lines = result.lines()

        assertTruncatedStackTrace(lines, stackTrace, StackTraceGenerator.RootCauseMessageLineUnqualified, options, maxFramesPerThrowable)
        assertDoesNotEndWithLineSeparator(result, options)
    }

    @Test
    fun maxFramesPerThrowable_2_TwoCausedBy() {
        val maxFramesPerThrowable = 2
        val options = StackTraceFormatterOptions.Default

        val throwable = StackTraceGenerator.generateTwoCausedBy()
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace)


        val lines = result.lines()
        assertThat(lines).hasSize(3 * 4)

        assertTruncatedStackTrace(lines, stackTrace, StackTraceGenerator.SecondCausedByLineUnqualified, options, maxFramesPerThrowable)

        assertThat(stackTrace.causedBy).isNotNull()
        val intermediateCause = stackTrace.causedBy!!
        assertTruncatedStackTrace(lines.drop(4), intermediateCause, StackTraceGenerator.FirstCausedByLineUnqualified, options, maxFramesPerThrowable, "Caused by: ")

        assertThat(intermediateCause.causedBy).isNotNull()
        val rootCause = intermediateCause.causedBy!!
        assertTruncatedStackTrace(lines.drop(8), rootCause, StackTraceGenerator.RootCauseMessageLineUnqualified, options, maxFramesPerThrowable, "Caused by: ")

        assertDoesNotEndWithLineSeparator(result, options)
    }


    @Test
    fun maxFramesPerThrowable_2_SuppressedExceptions() {
        val maxFramesPerThrowable = 1 // on JVM suppressed exception is already truncated to one not-common frame
        val options = StackTraceFormatterOptions.Default

        val throwable = StackTraceGenerator.generateSuppressed()
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace)


        val lines = result.lines()
        assertThat(lines).hasSize(6)

        assertTruncatedStackTrace(lines, stackTrace, StackTraceGenerator.RootCauseMessageLineUnqualified, options, maxFramesPerThrowable)

        assertThat(stackTrace.suppressed).hasSize(1)
        val suppressed = stackTrace.suppressed.first()
        assertTruncatedStackTrace(lines.drop(3), suppressed, StackTraceGenerator.FirstSuppressedExceptionLineUnqualified, options, maxFramesPerThrowable, options.suppressedExceptionMessagePrefix, options.suppressedExceptionIndent)

        assertDoesNotEndWithLineSeparator(result, options)
    }


    // TODO: add a test for a suppressed exception that contains a caused by exception


    @Test
    fun ignoreSuppressedExceptions() {
        val throwable = StackTraceGenerator.generateSuppressed()

        val options = StackTraceFormatterOptions(ignoreSuppressedExceptions = true)


        val result = underTest.format(throwable, options)


        assertThat(result).doesNotContain(options.suppressedExceptionMessagePrefix)
        assertThat(result).doesNotContain("Suppressed #1")

        assertDoesNotEndWithLineSeparator(result, options)
    }


    @Test
    fun rootCauseFirst() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val maxFramesPerThrowable = 2
        val options = StackTraceFormatterOptions(rootCauseFirst = true)
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace, options)


        val lines = result.lines()
        assertThat(lines).hasSize(4 * 4)

        assertThat(lines.first()).isIn(StackTraceGenerator.RootCauseMessageLineUnqualified,
            StackTraceGenerator.ExceptionsNamespace + StackTraceGenerator.RootCauseMessageLineUnqualified)

        var wrappedByLines = lines.drop(4)
        assertThat(wrappedByLines.first()).isIn(options.wrappedByMessagePrefix + StackTraceGenerator.FirstCausedByLineUnqualified,
            options.wrappedByMessagePrefix + StackTraceGenerator.ExceptionsNamespace + StackTraceGenerator.FirstCausedByLineUnqualified)

        wrappedByLines = wrappedByLines.drop(4)
        assertThat(wrappedByLines.first()).isIn(options.wrappedByMessagePrefix + StackTraceGenerator.SecondCausedByLineUnqualified,
            options.wrappedByMessagePrefix + StackTraceGenerator.ExceptionsNamespace + StackTraceGenerator.SecondCausedByLineUnqualified)

        wrappedByLines = wrappedByLines.drop(4)
        assertThat(wrappedByLines.first()).isIn(options.wrappedByMessagePrefix + StackTraceGenerator.ThirdCausedByLineUnqualified,
            options.wrappedByMessagePrefix + StackTraceGenerator.ExceptionsNamespace + StackTraceGenerator.ThirdCausedByLineUnqualified)

        assertDoesNotEndWithLineSeparator(result, options)
    }


    @Test
    fun maxNestedThrowables_1() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()
        val stackTrace = shortenStackTrace(throwable, maxNestedThrowables = 1)
        val options = StackTraceFormatterOptions()


        val result = underTest.format(stackTrace, options)


        assertThat(result).contains("ParentException: Wrapper #3")
        assertThat(result).contains(options.lineSeparator + options.causedByIndent + options.causedByMessagePrefix)
        assertThat(result).contains("Wrapper #2")
        assertThat(result).doesNotContain("Wrapper #1")
        assertThat(result).doesNotContain("Root cause")

        assertThat(result).endsWith(options.lineSeparator + options.causedByIndent + options.ellipsis + " 2 nested cause(s) omitted")
    }

    @Test
    fun maxSuppressedThrowables_1() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()
        val stackTrace = shortenStackTrace(throwable, maxSuppressedThrowables = 1)
        val options = StackTraceFormatterOptions()


        val result = underTest.format(stackTrace, options)


        assertThat(result).contains(options.lineSeparator + options.suppressedExceptionIndent + options.suppressedExceptionMessagePrefix)
        assertThat(result).contains("Suppressed #1")
        assertThat(result).doesNotContain("Suppressed #2")

        assertThat(result).endsWith(options.lineSeparator + options.suppressedExceptionIndent + options.ellipsis + " 1 suppressed exception(s) omitted")
    }


    private fun assertTruncatedStackTrace(lines: List<String>, stackTrace: ShortenedStackTrace, unqualifiedMessageLine: String,
                                          options: StackTraceFormatterOptions, maxFramesPerThrowable: Int, messageLinePrefix: String = "", additionalIndent: String = "") {
        assertThat(lines.size).isGreaterThanOrEqualTo(2 + maxFramesPerThrowable)

        // message line
        assertThat(lines.first()).isIn(additionalIndent + messageLinePrefix + unqualifiedMessageLine,
            additionalIndent + messageLinePrefix + StackTraceGenerator.ExceptionsNamespace + unqualifiedMessageLine)

        // two stack frames
        IntRange(1, maxFramesPerThrowable).forEach { index ->
            val stackFrameLine = lines[index]
            assertThat(stackFrameLine).startsWith(additionalIndent + options.stackFrameIndent)
            assertThat(stackFrameLine).isEqualTo(additionalIndent + options.stackFrameIndent + stackTrace.originalStackTrace[index - 1].line)
        }

        val countTruncatedFramesLine = lines[maxFramesPerThrowable + 1]
        if (stackTrace.countTruncatedFrames > 0) {
            val truncatedFramesLineStart = additionalIndent + options.stackFrameIndent + "... " + stackTrace.countTruncatedFrames + " frames truncated"
            if (stackTrace.countSkippedCommonFrames == 0) {
                assertThat(countTruncatedFramesLine).isEqualTo(truncatedFramesLineStart)
            } else {
                assertThat(countTruncatedFramesLine).isEqualTo(truncatedFramesLineStart + " (including " + stackTrace.countSkippedCommonFrames + " common frames)")
            }
        } else if (stackTrace.countSkippedCommonFrames > 0) {
            assertThat(countTruncatedFramesLine).isEqualTo(additionalIndent + options.stackFrameIndent + "... " + stackTrace.countSkippedCommonFrames + " common frames omitted")
        }
    }

    private fun assertDoesNotEndWithLineSeparator(result: String, options: StackTraceFormatterOptions = StackTraceFormatterOptions.Default) {
        assertThat(result.endsWith(options.lineSeparator)).isFalse()
    }


    private fun shortenStackTrace(throwable: Throwable, maxFramesPerThrowable: Int? = null, maxNestedThrowables: Int? = null, maxSuppressedThrowables: Int? = null) =
        stackTraceShortener.shorten(throwable, StackTraceShortenerOptions(maxFramesPerThrowable, maxNestedThrowables, maxSuppressedThrowables))

}