package net.codinux.log.stacktrace

data class StackTraceFormatterConfig(
    val messageLineIndent: String = "",
    val stackFrameIndent: String = "    ",
    val causedByIndent: String = "",
    val causedByMessagePrefix: String = "Caused by: ",
    val suppressedExceptionIndent: String = "    ",
    val suppressedExceptionMessagePrefix: String = "Suppressed: ",
    val lineSeparator: String = "\n",
) {
    companion object {
        val Default = StackTraceFormatterConfig()
    }
}