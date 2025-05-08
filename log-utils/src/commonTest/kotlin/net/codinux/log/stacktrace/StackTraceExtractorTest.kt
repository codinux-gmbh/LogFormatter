package net.codinux.log.stacktrace

import assertk.assertThat
import assertk.assertions.*
import org.example.log.stack.StackTraceGenerator
import kotlin.test.Test

class StackTraceExtractorTest {

    private val underTest = StackTraceExtractor()


    @Test
    fun singleThrowable() {
        val throwable = StackTraceGenerator.generateSingle()

        val result = underTest.extractStackTrace(throwable)

        assertIsRootCause(result)
        assertThat(result.countSkippedCommonFrames).isEqualTo(0)
    }


    @Test
    fun oneCausedByThrowable() {
        val throwable = StackTraceGenerator.generateCausedBy()

        val result = underTest.extractStackTrace(throwable)
        assertThat(result.countSkippedCommonFrames).isEqualTo(0)

        assertIsFirstParentException(result)
        assertThat(result.suppressed).isEmpty()

        assertThat(result.causedBy).isNotNull()
        val rootCause = result.causedBy!!
        assertIsRootCause(rootCause)
        assertThat(rootCause.countSkippedCommonFrames).isGreaterThanOrEqualTo(13)
    }

    @Test
    fun twoCausedByThrowables() {
        val throwable = StackTraceGenerator.generateTwoCausedBy()

        val result = underTest.extractStackTrace(throwable)

        assertThat(result.messageLine).isIn("ParentException: Wrapper #2", "org.example.log.stack.ParentException: Wrapper #2")
        assertThat(result.stackTrace.size).isGreaterThanOrEqualTo(8)
        assertThat(result.suppressed).isEmpty()
        assertThat(result.countSkippedCommonFrames).isEqualTo(0)

        assertThat(result.causedBy).isNotNull()
        val intermediateCause = result.causedBy!!
        assertIsFirstParentException(intermediateCause)
        assertThat(intermediateCause.suppressed).isEmpty()
        assertThat(intermediateCause.countSkippedCommonFrames).isGreaterThanOrEqualTo(5)

        assertThat(intermediateCause.causedBy).isNotNull()
        val rootCause = intermediateCause.causedBy!!
        assertIsRootCause(rootCause)
        assertThat(rootCause.suppressed).isEmpty()
        assertThat(rootCause.countSkippedCommonFrames).isGreaterThanOrEqualTo(5)
    }


    @Test
    fun oneSuppressedThrowable() {
        val throwable = StackTraceGenerator.generateSuppressed()

        val result = underTest.extractStackTrace(throwable)

        assertIsRootCause(result, 1)
        assertThat(result.countSkippedCommonFrames).isEqualTo(0)

        assertThat(result.suppressed).hasSize(1)
        val firstSuppressedException = result.suppressed.first()
        assertThat(firstSuppressedException.messageLine).isIn("SuppressedException: Suppressed #1", "org.example.log.stack.SuppressedException: Suppressed #1")
        assertThat(firstSuppressedException.stackTrace.size).isGreaterThanOrEqualTo(1) // on JVM there's really only on frame left
        assertThat(firstSuppressedException.suppressed).isEmpty()
        assertThat(firstSuppressedException.countSkippedCommonFrames).isGreaterThanOrEqualTo(13)
    }


    @Test
    fun isCausedByLine_CausedByLine() {
        val line = "Caused by: org.example.log.stack.RootCauseException: Root cause"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isTrue()
    }

    @Test
    fun isCausedByLine_MessageLine() {
        val line = "org.example.log.stack.RootCauseException: Root cause"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isCausedByLine_StackFrameLine() {
        val line = "\tat org.example.log.stack.StackTraceGenerator.eight(StackTraceGenerator.kt:48)"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isCausedByLine_SecondLevelStackFrameLine() {
        val line = "\t\tat org.example.log.stack.StackTraceGenerator.twoSuppressed(StackTraceGenerator.kt:91)"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isCausedByLine_SuppressedLineStartingWithTab() {
        val line = "\tSuppressed: org.example.log.stack.SuppressedException: Suppressed #1"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isCausedByLine_SkippedCommonFramesJava() {
        val line = "\t... 46 more"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isCausedByLine_SkippedCommonFramesAllOtherPlatforms() {
        val line = "\t\t... and 13 more common stack frames skipped"

        val result = underTest.isCausedByLine(line)

        assertThat(result).isFalse()
    }


    @Test
    fun isSuppressedExceptionLine_SuppressedLineStartingWithTab() {
        val line = "\tSuppressed: org.example.log.stack.SuppressedException: Suppressed #1"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isTrue()
    }

    @Test
    fun isSuppressedExceptionLine_MessageLine() {
        val line = "org.example.log.stack.RootCauseException: Root cause"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isSuppressedExceptionLine_StackFrameLine() {
        val line = "\tat org.example.log.stack.StackTraceGenerator.eight(StackTraceGenerator.kt:48)"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isSuppressedExceptionLine_SecondLevelStackFrameLine() {
        val line = "\t\tat org.example.log.stack.StackTraceGenerator.twoSuppressed(StackTraceGenerator.kt:91)"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isSuppressedExceptionLine_CausedByLine() {
        val line = "Caused by: org.example.log.stack.RootCauseException: Root cause"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isSuppressedExceptionLine_SkippedCommonFramesJava() {
        val line = "\t... 46 more"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isFalse()
    }

    @Test
    fun isSuppressedExceptionLine_SkippedCommonFramesAllOtherPlatforms() {
        val line = "\t\t... and 13 more common stack frames skipped"

        val result = underTest.isSuppressedExceptionLine(line)

        assertThat(result).isFalse()
    }


    @Test
    fun extractSkippedCommonFrames_SkippedCommonFramesJava() {
        val line = "\t... 46 more"

        val result = underTest.extractSkippedCommonFrames(listOf(line))

        assertThat(result).isEqualTo(46)
    }

    @Test
    fun extractSkippedCommonFrames_SkippedCommonFramesAllOtherPlatforms() {
        val line = "\t\t... and 13 more common stack frames skipped"

        val result = underTest.extractSkippedCommonFrames(listOf(line))

        assertThat(result).isEqualTo(13)
    }

    @Test
    fun extractSkippedCommonFrames_MessageLine() {
        val line = "org.example.log.stack.RootCauseException: Root cause"

        val result = underTest.extractSkippedCommonFrames(listOf(line))

        assertThat(result).isNull()
    }

    @Test
    fun extractSkippedCommonFrames_StackFrameLine() {
        val line = "\t\tat org.example.log.stack.StackTraceGenerator.twoSuppressed(StackTraceGenerator.kt:91)"

        val result = underTest.extractSkippedCommonFrames(listOf(line))

        assertThat(result).isNull()
    }

    @Test
    fun extractSkippedCommonFrames_CausedByLine() {
        val line = "Caused by: org.example.log.stack.RootCauseException: Root cause"

        val result = underTest.extractSkippedCommonFrames(listOf(line))

        assertThat(result).isNull()
    }

    @Test
    fun extractSkippedCommonFrames_SuppressedExceptionLine() {
        val line = "\tSuppressed: org.example.log.stack.SuppressedException: Suppressed #1"

        val result = underTest.extractSkippedCommonFrames(listOf(line))

        assertThat(result).isNull()
    }


    private fun assertIsRootCause(stackTrace: StackTrace, countSuppressedExceptions: Int = 0) {
        assertThat(stackTrace.messageLine).isIn("RootCauseException: Root cause", "org.example.log.stack.RootCauseException: Root cause")
        assertThat(stackTrace.stackTrace.size).isGreaterThanOrEqualTo(5)
        assertThat(stackTrace.causedBy).isNull()
        assertThat(stackTrace.suppressed).hasSize(countSuppressedExceptions)
    }

    private fun assertIsFirstParentException(stackTrace: StackTrace) {
        assertThat(stackTrace.messageLine).isIn("ParentException: Wrapper #1", "org.example.log.stack.ParentException: Wrapper #1")
        assertThat(stackTrace.stackTrace.size).isGreaterThanOrEqualTo(4)
        assertThat(stackTrace.causedBy).isNotNull()
        assertThat(stackTrace.suppressed).isEmpty()
    }

}