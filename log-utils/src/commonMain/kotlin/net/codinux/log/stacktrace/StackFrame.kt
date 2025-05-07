package net.codinux.log.stacktrace

data class StackFrame(
    val line: String
) {
    override fun toString() = line
}