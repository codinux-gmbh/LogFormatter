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
    }


    @Test
    fun oneCausedByThrowable() {
        val throwable = StackTraceGenerator.generateCausedBy()

        val result = underTest.extractStackTrace(throwable)

        assertIsFirstParentException(result)
        assertThat(result.suppressed).isEmpty()

        assertThat(result.causedBy).isNotNull()
        val rootCause = result.causedBy!!
        assertIsRootCause(rootCause)
    }

    @Test
    fun twoCausedByThrowables() {
        val throwable = StackTraceGenerator.generateTwoCausedBy()

        val result = underTest.extractStackTrace(throwable)

        assertThat(result.messageLine).isIn("ParentException: Wrapper #2", "org.example.log.stack.ParentException: Wrapper #2")
        assertThat(result.frames.size).isGreaterThanOrEqualTo(8)
        assertThat(result.suppressed).isEmpty()

        assertThat(result.causedBy).isNotNull()
        val intermediateCause = result.causedBy!!
        assertIsFirstParentException(intermediateCause)
        assertThat(intermediateCause.suppressed).isEmpty()

        assertThat(intermediateCause.causedBy).isNotNull()
        val rootCause = intermediateCause.causedBy!!
        assertIsRootCause(rootCause)
        assertThat(rootCause.suppressed).isEmpty()
    }


    @Test
    fun oneSuppressedThrowable() {
        val throwable = StackTraceGenerator.generateSuppressed()

        val result = underTest.extractStackTrace(throwable)

        assertIsRootCause(result, 1)

        assertThat(result.suppressed).hasSize(1)
        val firstSuppressedException = result.suppressed.first()
        assertThat(firstSuppressedException.messageLine).isIn("SuppressedException: Suppressed #1", "org.example.log.stack.SuppressedException: Suppressed #1")
        assertThat(firstSuppressedException.frames.size).isGreaterThanOrEqualTo(3)
        assertThat(firstSuppressedException.suppressed).isEmpty()
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


    private fun assertIsRootCause(stackTrace: StackTrace, countSuppressedExceptions: Int = 0) {
        assertThat(stackTrace.messageLine).isIn("RootCauseException: Root cause", "org.example.log.stack.RootCauseException: Root cause")
        assertThat(stackTrace.frames.size).isGreaterThanOrEqualTo(5)
        assertThat(stackTrace.causedBy).isNull()
        assertThat(stackTrace.suppressed).hasSize(countSuppressedExceptions)
    }

    private fun assertIsFirstParentException(stackTrace: StackTrace) {
        assertThat(stackTrace.messageLine).isIn("ParentException: Wrapper #1", "org.example.log.stack.ParentException: Wrapper #1")
        assertThat(stackTrace.frames.size).isGreaterThanOrEqualTo(5)
        assertThat(stackTrace.causedBy).isNotNull()
        assertThat(stackTrace.suppressed).isEmpty()
    }

}