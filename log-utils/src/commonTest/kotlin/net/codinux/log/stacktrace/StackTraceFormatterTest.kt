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
        val config = StackTraceFormatterConfig(maxStackTraceStringLength = maxStackTraceStringLength)

        val result = underTest.format(StackTraceGenerator.generateTwoCausedBy(), config)

        assertThat(result.length).isEqualTo(maxStackTraceStringLength)
    }
    

    @Test
    fun maxFramesPerThrowable_2_SingleThrowable() {
        val maxFramesPerThrowable = 2
        val config = StackTraceFormatterConfig.Default

        val throwable = StackTraceGenerator.generateSingle()
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace)


        val lines = result.lines()

        assertTruncatedStackTrace(lines, stackTrace, StackTraceGenerator.RootCauseMessageLineUnqualified, config, maxFramesPerThrowable)
    }

    @Test
    fun maxFramesPerThrowable_2_TwoCausedBy() {
        val maxFramesPerThrowable = 2
        val config = StackTraceFormatterConfig.Default

        val throwable = StackTraceGenerator.generateTwoCausedBy()
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace)


        val lines = result.lines()
        assertThat(lines).hasSize(3 * 4)

        assertTruncatedStackTrace(lines, stackTrace, StackTraceGenerator.SecondCausedByLineUnqualified, config, maxFramesPerThrowable)

        assertThat(stackTrace.causedBy).isNotNull()
        val intermediateCause = stackTrace.causedBy!!
        assertTruncatedStackTrace(lines.drop(4), intermediateCause, StackTraceGenerator.FirstCausedByLineUnqualified, config, maxFramesPerThrowable, "Caused by: ")

        assertThat(intermediateCause.causedBy).isNotNull()
        val rootCause = intermediateCause.causedBy!!
        assertTruncatedStackTrace(lines.drop(8), rootCause, StackTraceGenerator.RootCauseMessageLineUnqualified, config, maxFramesPerThrowable, "Caused by: ")
    }


    @Test
    fun maxFramesPerThrowable_2_SuppressedExceptions() {
        val maxFramesPerThrowable = 1 // on JVM suppressed exception is already truncated to one not-common frame
        val config = StackTraceFormatterConfig.Default

        val throwable = StackTraceGenerator.generateSuppressed()
        val stackTrace = stackTraceShortener.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)


        val result = underTest.format(stackTrace)


        val lines = result.lines()
        assertThat(lines).hasSize(6)

        assertTruncatedStackTrace(lines, stackTrace, StackTraceGenerator.RootCauseMessageLineUnqualified, config, maxFramesPerThrowable)

        assertThat(stackTrace.suppressed).hasSize(1)
        val suppressed = stackTrace.suppressed.first()
        assertTruncatedStackTrace(lines.drop(3), suppressed, StackTraceGenerator.FirstSuppressedExceptionLineUnqualified, config, maxFramesPerThrowable, config.suppressedExceptionMessagePrefix, config.suppressedExceptionIndent)
    }


    private fun assertTruncatedStackTrace(lines: List<String>, stackTrace: ShortenedStackTrace, unqualifiedMessageLine: String,
                                          config: StackTraceFormatterConfig, maxFramesPerThrowable: Int, messageLinePrefix: String = "", additionalIndent: String = "") {
        assertThat(lines.size).isGreaterThanOrEqualTo(2 + maxFramesPerThrowable)

        // message line
        assertThat(lines.first()).isIn(additionalIndent + messageLinePrefix + unqualifiedMessageLine,
            additionalIndent + messageLinePrefix + StackTraceGenerator.ExceptionsNamespace + unqualifiedMessageLine)

        // two stack frames
        IntRange(1, maxFramesPerThrowable).forEach { index ->
            val stackFrameLine = lines[index]
            assertThat(stackFrameLine).startsWith(additionalIndent + config.stackFrameIndent)
            assertThat(stackFrameLine).isEqualTo(additionalIndent + config.stackFrameIndent + stackTrace.originalFrames[index - 1].line)
        }

        val countTruncatedFramesLine = lines[maxFramesPerThrowable + 1]
        if (stackTrace.countTruncatedFrames > 0) {
            val truncatedFramesLineStart = additionalIndent + config.stackFrameIndent + "... " + stackTrace.countTruncatedFrames + " frames truncated"
            if (stackTrace.countSkippedCommonFrames == 0) {
                assertThat(countTruncatedFramesLine).isEqualTo(truncatedFramesLineStart)
            } else {
                assertThat(countTruncatedFramesLine).isEqualTo(truncatedFramesLineStart + " (including " + stackTrace.countSkippedCommonFrames + " common frames)")
            }
        } else if (stackTrace.countSkippedCommonFrames > 0) {
            assertThat(countTruncatedFramesLine).isEqualTo(additionalIndent + config.stackFrameIndent + "... " + stackTrace.countSkippedCommonFrames + " common frames omitted")
        }
    }

}