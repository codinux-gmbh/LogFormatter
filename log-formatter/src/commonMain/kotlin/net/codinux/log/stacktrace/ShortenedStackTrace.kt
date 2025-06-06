package net.codinux.log.stacktrace

data class ShortenedStackTrace(
    val messageLine: String,
    val originalStackTrace: List<StackFrame>,
    val causedBy: ShortenedStackTrace? = null,
    val suppressed: List<ShortenedStackTrace> = emptyList(),

    val countSkippedCommonFrames: Int = 0,

    val countSkippedNestedThrowables: Int = 0,
    val countSkippedSuppressedThrowables: Int = 0,

    val isRootCauseFirst: Boolean = false,
) {
    var framesToDisplay: List<StackFrame> = originalStackTrace
        internal set

    var countTruncatedFrames: Int = 0
        internal set


    constructor(stackTrace: StackTrace) : this(stackTrace, stackTrace.causedBy?.let { ShortenedStackTrace(it) })

    constructor(stackTrace: StackTrace, causedBy: ShortenedStackTrace?,
                suppressed: List<ShortenedStackTrace> = stackTrace.suppressed.map { ShortenedStackTrace(it) },
                countSkippedNestedThrowables: Int = 0, countSkippedSuppressedThrowables: Int = 0, isRootCauseFirst: Boolean = false) : this(
        stackTrace.messageLine,
        stackTrace.stackTrace,
        causedBy,
        suppressed,
        stackTrace.countSkippedCommonFrames,
        countSkippedNestedThrowables,
        countSkippedSuppressedThrowables,
        isRootCauseFirst
    )

    override fun toString() = "$messageLine, ${framesToDisplay.size} of ${originalStackTrace.size} stack frames displayed"
}