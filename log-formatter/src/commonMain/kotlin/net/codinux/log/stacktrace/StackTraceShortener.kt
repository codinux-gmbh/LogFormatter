package net.codinux.log.stacktrace

import kotlin.jvm.JvmOverloads

/**
 * Note: Common stack frames that reoccur in nested [Throwable] causes are omitted there and
 * indicated by [ShortenedStackTrace.countSkippedCommonFrames].
 * This is due to the behavior of [Throwable.stackTraceToString()], which we have to rely on,
 * as the Kotlin standard library does not expose raw stack trace data (except on the JVM).
 */
open class StackTraceShortener @JvmOverloads constructor(
    val options: StackTraceShortenerOptions = StackTraceShortenerOptions.Default,
    protected val stackTraceExtractor: StackTraceExtractor = StackTraceExtractor.Default,
    protected val inverter: StackTraceInverter = StackTraceInverter.Default
) {
    companion object {
        val Default by lazy { StackTraceShortener() }
    }


    open fun shorten(throwable: Throwable, maxFramesPerThrowable: Int?) =
        shorten(extractStackTrace(throwable), maxFramesPerThrowable)

    open fun shorten(throwable: Throwable, options: StackTraceShortenerOptions = this.options) =
        shorten(extractStackTrace(throwable), options)

    open fun shorten(stackTrace: StackTrace, maxFramesPerThrowable: Int?) =
        shorten(stackTrace, options.copy(maxFramesPerThrowable = maxFramesPerThrowable))

    open fun shorten(stackTrace: StackTrace, options: StackTraceShortenerOptions = this.options): ShortenedStackTrace {
        val stackTraceToShorten = if (options.rootCauseFirst) rootCauseFirst(stackTrace) else stackTrace

        val shortened = if (options.maxNestedThrowables == null || options.maxNestedThrowables < 0) {
            createShortenedStackTrace(stackTraceToShorten, options)
        } else {
            shortenedStackTraceWithMaxDepth(stackTraceToShorten, options, options.maxNestedThrowables)
        }

        if (options.maxFramesPerThrowable != null && options.maxFramesPerThrowable >= 0) {
            truncateToMaxFramesPerThrowable(shortened, options.maxFramesPerThrowable)
        }

        return shortened
    }


    protected open fun shortenedStackTraceWithMaxDepth(stackTrace: StackTrace, options: StackTraceShortenerOptions, maxNestedThrowables: Int) =
        shortenedStackTraceWithMaxDepth(stackTrace, options, maxNestedThrowables, 0)

    protected open fun shortenedStackTraceWithMaxDepth(stackTrace: StackTrace, options: StackTraceShortenerOptions, maxNestedThrowables: Int,
                                                       countAddedNestedThrowables: Int, countSkippedNestedThrowables: Int = 0): ShortenedStackTrace =
        createShortenedStackTrace(stackTrace, options,
            if (countAddedNestedThrowables < maxNestedThrowables && stackTrace.causedBy != null) {
                shortenedStackTraceWithMaxDepth(stackTrace.causedBy, options, maxNestedThrowables, countAddedNestedThrowables + 1)
            } else {
                null
            },
            if (countAddedNestedThrowables == maxNestedThrowables) countSkippedNestedThrowables(stackTrace.causedBy) else 0
        )

    protected open fun countSkippedNestedThrowables(causedBy: StackTrace?, visitedCauses: MutableSet<StackTrace> = mutableSetOf()): Int =
        if (causedBy == null) 0
        else if (visitedCauses.contains(causedBy)) 0 // circle detected
        else {
            visitedCauses.add(causedBy)
            1 + countSkippedNestedThrowables(causedBy.causedBy, visitedCauses)
        }

    protected open fun createShortenedStackTrace(stackTrace: StackTrace, options: StackTraceShortenerOptions,
                                                 causedBy: ShortenedStackTrace? = stackTrace.causedBy?.let { createShortenedStackTrace(it, options) },
                                                 countSkippedNestedThrowables: Int = 0): ShortenedStackTrace {
        val suppressed = mapSuppressedExceptions(stackTrace, options)

        return ShortenedStackTrace(stackTrace, causedBy, suppressed, countSkippedNestedThrowables,
            countSkippedSuppressedThrowables = stackTrace.suppressed.size - suppressed.size, isRootCauseFirst = options.rootCauseFirst)
    }

    protected open fun mapSuppressedExceptions(stackTrace: StackTrace, options: StackTraceShortenerOptions): List<ShortenedStackTrace> {
        val maxSuppressedThrowables = options.maxSuppressedThrowables

        val suppressed = if (maxSuppressedThrowables == null || maxSuppressedThrowables < 0) {
            stackTrace.suppressed
        } else {
            stackTrace.suppressed.take(maxSuppressedThrowables)
        }

        return suppressed.map { createShortenedStackTrace(it, options) } // TODO: doesn't limit the causedBy exceptions of suppressedExceptions
    }


    protected open fun truncateToMaxFramesPerThrowable(shortened: ShortenedStackTrace, maxFramesPerThrowable: Int) {
        if (shortened.framesToDisplay.size > maxFramesPerThrowable) {
            shortened.framesToDisplay = shortened.framesToDisplay.subList(0, maxFramesPerThrowable)
            shortened.countTruncatedFrames = shortened.originalStackTrace.size - maxFramesPerThrowable
        }

        shortened.suppressed.forEach {
            truncateToMaxFramesPerThrowable(it, maxFramesPerThrowable)
        }

        shortened.causedBy?.let {
            truncateToMaxFramesPerThrowable(it, maxFramesPerThrowable)
        }
    }


    protected open fun extractStackTrace(throwable: Throwable) =
        stackTraceExtractor.extractStackTrace(throwable)

    protected open fun rootCauseFirst(stackTrace: StackTrace): StackTrace =
        inverter.rootCauseFirst(stackTrace)

}