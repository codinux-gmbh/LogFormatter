package net.codinux.log.stacktrace

data class ShortenedStackTrace(
    val messageLine: String,
    val originalStackTrace: List<StackFrame>,
    val causedBy: ShortenedStackTrace? = null,
    val suppressed: List<ShortenedStackTrace> = emptyList(),

    val countSkippedCommonFrames: Int = 0,
) {
    var framesToDisplay: List<StackFrame> = originalStackTrace
        internal set

    var countTruncatedFrames: Int = 0
        internal set


    constructor(stackTrace: StackTrace) : this(stackTrace, stackTrace.causedBy?.let { ShortenedStackTrace(it) })

    constructor(stackTrace: StackTrace, causedBy: ShortenedStackTrace?) : this(
        stackTrace.messageLine,
        stackTrace.stackTrace,
        causedBy,
        stackTrace.suppressed.map { ShortenedStackTrace(it) },
        stackTrace.countSkippedCommonFrames
    )

    override fun toString() = "$messageLine, ${framesToDisplay.size} of ${originalStackTrace.size} stack frames displayed"
}