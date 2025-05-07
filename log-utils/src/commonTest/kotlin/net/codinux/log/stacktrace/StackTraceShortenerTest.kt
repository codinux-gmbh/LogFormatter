package net.codinux.log.stacktrace

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotNull
import org.example.log.stack.StackTraceGenerator
import kotlin.test.Test

class StackTraceShortenerTest {

    private val underTest = StackTraceShortener()


    @Test
    fun maxFramesPerThrowable_2_TwoCausedBy() {
        val maxFramesPerThrowable = 2
        val throwable = StackTraceGenerator.generateTwoCausedBy()

        val result = underTest.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)

        assertMaxFramesPerThrowable(result, maxFramesPerThrowable)

        assertThat(result.causedBy).isNotNull()
        val intermediateCause = result.causedBy!!
        assertMaxFramesPerThrowable(intermediateCause, maxFramesPerThrowable, 5)

        assertThat(intermediateCause.causedBy).isNotNull()
        val rootCause = intermediateCause.causedBy!!
        assertMaxFramesPerThrowable(rootCause, maxFramesPerThrowable, 5)
    }


    @Test
    fun maxFramesPerThrowable_2_SuppressedExceptions() {
        val maxFramesPerThrowable = 2
        val throwable = StackTraceGenerator.generateSuppressed()

        val result = underTest.shorten(throwable, maxFramesPerThrowable = maxFramesPerThrowable)

        assertMaxFramesPerThrowable(result, maxFramesPerThrowable)

        assertThat(result.suppressed).hasSize(1)
        val suppressedException = result.suppressed.first()
        assertMaxFramesPerThrowable(suppressedException, maxFramesPerThrowable, 5)
    }


    private fun assertMaxFramesPerThrowable(stackTrace: ShortenedStackTrace, maxFramesPerThrowable: Int, countMinSkippedCommonFrames: Int = 0) {
        assertThat(stackTrace.framesToDisplay).hasSize(maxFramesPerThrowable)
        assertThat(stackTrace.originalFrames).hasSize(maxFramesPerThrowable + stackTrace.countTruncatedFrames)
        assertThat(stackTrace.countTruncatedFrames).isEqualTo(stackTrace.originalFrames.size - maxFramesPerThrowable)

        assertThat(stackTrace.countSkippedCommonFrames).isGreaterThanOrEqualTo(countMinSkippedCommonFrames)
    }

}