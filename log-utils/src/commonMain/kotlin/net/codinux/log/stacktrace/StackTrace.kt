package net.codinux.log.stacktrace

data class StackTrace(
    /**
     * Contains exception type and message, e.g. "java.lang.RuntimeException: Something went wrong".
     */
    val messageLine: String,

    /**
     * Snapshot of the call stack at the time the exception occurred.
     *
     * Each stack frame represents a function/method call on the call stack, ordered bottom-top.
     *
     * The topmost stack frame represents the method that was executing when the exception
     * occurred and the bottommost stack frame representing the first method call in the call stack.
     */
    val stackTrace: List<StackFrame>,
    val causedBy: StackTrace? = null,
    val suppressed: List<StackTrace> = emptyList(),
    /**
     * Count frames common with parent stack trace, that therefore have been omitted here.
     */
    val countSkippedCommonFrames: Int = 0,
) {
    override fun toString() = "$messageLine, ${stackTrace.size} stack frames"
}