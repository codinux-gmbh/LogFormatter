package net.codinux.log.stacktrace

data class ShortenedStackTrace(
    val messageLine: String,
    val originalFrames: List<StackFrame>,
    val causedBy: ShortenedStackTrace? = null,
    val suppressed: List<ShortenedStackTrace> = emptyList(),

    val countSkippedCommonFrames: Int = 0,
) {
    var framesToDisplay: List<StackFrame> = originalFrames
        internal set

    var countTruncatedFrames: Int = 0
        internal set


    constructor(stackTrace: StackTrace) : this(
        stackTrace.messageLine,
        stackTrace.frames,
        stackTrace.causedBy?.let { ShortenedStackTrace(it) },
        stackTrace.suppressed.map { ShortenedStackTrace(it) },
        stackTrace.countSkippedCommonFrames
    )

    override fun toString() = "$messageLine, ${framesToDisplay.size} of ${originalFrames.size} stack frames displayed"
}