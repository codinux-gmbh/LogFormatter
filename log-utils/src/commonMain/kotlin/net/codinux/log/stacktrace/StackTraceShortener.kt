package net.codinux.log.stacktrace

open class StackTraceShortener(
    protected val stackTraceExtractor: StackTraceExtractor = StackTraceExtractor.Default
) {
    companion object {
        val Default = StackTraceShortener()
    }


    open fun shorten(throwable: Throwable, maxFramesPerThrowable: Int?) =
        shorten(extractStackTrace(throwable), maxFramesPerThrowable)

    open fun shorten(throwable: Throwable, config: StackTraceShortenerConfig = StackTraceShortenerConfig.Default) =
        shorten(extractStackTrace(throwable), config)

    open fun shorten(stackTrace: StackTrace, maxFramesPerThrowable: Int?) =
        shorten(stackTrace, StackTraceShortenerConfig(maxFramesPerThrowable))

    open fun shorten(stackTrace: StackTrace, config: StackTraceShortenerConfig = StackTraceShortenerConfig.Default): ShortenedStackTrace {
        val shortened = if (config.maxNestedThrowables == null || config.maxNestedThrowables < 0) {
            ShortenedStackTrace(stackTrace)
        } else {
            shortenedStackTraceWithMaxDepth(stackTrace, config.maxNestedThrowables)
        }

        if (config.maxFramesPerThrowable != null && config.maxFramesPerThrowable >= 0) {
            truncateToMaxFramesPerThrowable(shortened, config.maxFramesPerThrowable)
        }

        return shortened
    }


    protected open fun shortenedStackTraceWithMaxDepth(stackTrace: StackTrace, maxNestedThrowables: Int) =
        shortenedStackTraceWithMaxDepth(stackTrace, maxNestedThrowables, 0)

    protected open fun shortenedStackTraceWithMaxDepth(stackTrace: StackTrace, maxNestedThrowables: Int, countAddedNestedThrowables: Int): ShortenedStackTrace =
        ShortenedStackTrace(stackTrace,
            if (countAddedNestedThrowables < maxNestedThrowables && stackTrace.causedBy != null) {
                shortenedStackTraceWithMaxDepth(stackTrace.causedBy, maxNestedThrowables, countAddedNestedThrowables + 1)
            } else {
                null
            }
        )


    protected open fun truncateToMaxFramesPerThrowable(shortened: ShortenedStackTrace, maxFramesPerThrowable: Int) {
        if (shortened.framesToDisplay.size > maxFramesPerThrowable) {
            shortened.framesToDisplay = shortened.framesToDisplay.subList(0, maxFramesPerThrowable)
            shortened.countTruncatedFrames = shortened.originalFrames.size - maxFramesPerThrowable
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

}