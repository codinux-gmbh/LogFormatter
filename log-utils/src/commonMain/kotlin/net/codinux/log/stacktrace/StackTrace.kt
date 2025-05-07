package net.codinux.log.stacktrace

data class StackTrace(
    /**
     * Contains exception type and message, e.g. "java.lang.RuntimeException: Something went wrong".
     */
    val messageLine: String,
    val frames: List<StackFrame>,
    val causedBy: StackTrace? = null,
    val suppressed: List<StackTrace> = emptyList(),
    /**
     * Count frames common with parent stack trace, that therefore have been omitted here.
     */
    val countSkippedCommonFrames: Int = 0,
) {
    override fun toString() = "$messageLine, ${frames.size} stack frames"
}