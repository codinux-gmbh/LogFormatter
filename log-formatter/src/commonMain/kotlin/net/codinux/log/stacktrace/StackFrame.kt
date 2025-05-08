package net.codinux.log.stacktrace

/**
 * Represents a function/method call instance on the call stack.
 */
data class StackFrame(
    val line: String,
    val originalIndent: String = "",
    val originalLine: String = line
) {
    override fun toString() = line
}