package net.codinux.log.stacktrace

import assertk.assertThat
import assertk.assertions.*
import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.isJvmOrAndroid
import org.example.log.stack.StackTraceGenerator
import kotlin.test.Test

class StackTraceShortenerTest {

    private val underTest = StackTraceShortener()


    @Test
    fun maxFramesPerThrowable_2_TwoCausedBy() {
        val maxFramesPerThrowable = 2
        val throwable = StackTraceGenerator.generateTwoCausedBy()

        val result = underTest.shorten(throwable, options(maxFramesPerThrowable = maxFramesPerThrowable))

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
        val maxFramesPerThrowable = if (Platform.isJvmOrAndroid) 1 else 2 // on JVM suppressed exception is already truncated to one not-common frame
        val throwable = StackTraceGenerator.generateSuppressed()

        val result = underTest.shorten(throwable, options(maxFramesPerThrowable = maxFramesPerThrowable))

        assertMaxFramesPerThrowable(result, maxFramesPerThrowable)

        assertThat(result.suppressed).hasSize(1)
        val suppressedException = result.suppressed.first()
        assertMaxFramesPerThrowable(suppressedException, maxFramesPerThrowable, 5)
    }


    @Test
    fun maxNestedThrowables_0() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = 0))

        assertThat(result.causedBy).isNull()
        assertThat(throwable.cause).isNotNull()
    }

    @Test
    fun maxNestedThrowables_1() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = 1))

        assertThat(result.causedBy).isNotNull()

        assertThat(result.causedBy!!.causedBy).isNull()
    }

    @Test
    fun maxNestedThrowables_2() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = 2))

        assertThat(result.causedBy).isNotNull()

        assertThat(result.causedBy!!.causedBy).isNotNull()

        assertThat(result.causedBy!!.causedBy!!.causedBy).isNull()
    }

    @Test
    fun maxNestedThrowables_3() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = 3))

        assertMaxNumberOfCausesIsReturned(result)
    }

    @Test
    fun maxNestedThrowables_MoreThanNumberOfCauses() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = 4))

        assertMaxNumberOfCausesIsReturned(result)
    }

    @Test
    fun maxNestedThrowables_Null_MaxNumberOfCausesIsReturned() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = null))

        assertMaxNumberOfCausesIsReturned(result)
    }

    @Test
    fun maxNestedThrowables_LessThanZero_MaxNumberOfCausesIsReturned() {
        val throwable = StackTraceGenerator.generateThreeCausedBy()

        val result = underTest.shorten(throwable, options(maxNestedThrowables = -1))

        assertMaxNumberOfCausesIsReturned(result)
    }


    @Test
    fun maxSuppressedThrowables_0() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()

        val result = underTest.shorten(throwable, options(maxSuppressedThrowables = 0))

        assertThat(result.suppressed).hasSize(0)
        assertThat(throwable.suppressedExceptions).hasSize(2)
    }

    @Test
    fun maxSuppressedThrowables_1() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()

        val result = underTest.shorten(throwable, options(maxSuppressedThrowables = 1))

        assertThat(result.suppressed).hasSize(1)
        assertThat(throwable.suppressedExceptions).hasSize(2)
    }

    @Test
    fun maxSuppressedThrowables_2() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()

        val result = underTest.shorten(throwable, options(maxSuppressedThrowables = 2))

        assertThat(result.suppressed).hasSize(2)
        assertThat(throwable.suppressedExceptions).hasSize(2)
    }

    @Test
    fun maxSuppressedThrowables_MoreThanNumberOfSuppressedExceptions() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()

        val result = underTest.shorten(throwable, options(maxSuppressedThrowables = 3))

        assertThat(result.suppressed).hasSize(2)
        assertThat(throwable.suppressedExceptions).hasSize(2)
    }

    @Test
    fun maxSuppressedThrowables_Null_MaxNumberOfSuppressedExceptionsIsReturned() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()

        val result = underTest.shorten(throwable, options(maxSuppressedThrowables = null))

        assertThat(result.suppressed).hasSize(2)
        assertThat(throwable.suppressedExceptions).hasSize(2)
    }

    @Test
    fun maxSuppressedThrowables_LessThanZero_MaxNumberOfSuppressedExceptionsIsReturned() {
        val throwable = StackTraceGenerator.generateTwoSuppressed()

        val result = underTest.shorten(throwable, options(maxSuppressedThrowables = -1))

        assertThat(result.suppressed).hasSize(2)
        assertThat(throwable.suppressedExceptions).hasSize(2)
    }


    private fun assertMaxFramesPerThrowable(stackTrace: ShortenedStackTrace, maxFramesPerThrowable: Int, countMinSkippedCommonFrames: Int = 0) {
        assertThat(stackTrace.framesToDisplay).hasSize(maxFramesPerThrowable)
        assertThat(stackTrace.originalStackTrace).hasSize(maxFramesPerThrowable + stackTrace.countTruncatedFrames)
        assertThat(stackTrace.countTruncatedFrames).isEqualTo(stackTrace.originalStackTrace.size - maxFramesPerThrowable)

        assertThat(stackTrace.countSkippedCommonFrames).isGreaterThanOrEqualTo(countMinSkippedCommonFrames)
    }


    private fun assertMaxNumberOfCausesIsReturned(result: ShortenedStackTrace) {
        assertThat(result.causedBy).isNotNull()

        assertThat(result.causedBy!!.causedBy).isNotNull()

        assertThat(result.causedBy!!.causedBy!!.causedBy).isNotNull()

        assertThat(result.causedBy!!.causedBy!!.causedBy!!.causedBy).isNull()
    }


    private fun options(maxFramesPerThrowable: Int? = null, maxNestedThrowables: Int? = null, maxSuppressedThrowables: Int? = null) =
        StackTraceShortenerOptions(maxFramesPerThrowable, maxNestedThrowables, maxSuppressedThrowables)

}