package net.codinux.log.stacktrace

data class StackFrame(
    val line: String,
    val originalIndent: String = "",
    val originalLine: String = line
) {
    override fun toString() = line
}