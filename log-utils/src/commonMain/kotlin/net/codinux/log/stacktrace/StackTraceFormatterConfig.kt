package net.codinux.log.stacktrace

import net.codinux.kotlin.text.LineSeparator

data class StackTraceFormatterConfig(
    val messageLineIndent: String = "",
    val stackFrameIndent: String = "    ",
    val causedByIndent: String = "",
    val causedByMessagePrefix: String = "Caused by: ",
    val suppressedExceptionIndent: String = "    ",
    val suppressedExceptionMessagePrefix: String = "Suppressed: ",
    val lineSeparator: String = LineSeparator.System,

    /**
     * The ellipsis string to show for skipped common frames or truncated frames when
     * [StackTraceShortenerConfig.maxFramesPerThrowable] is set.
     */
    val ellipsis: String = "...",
) {
    companion object {
        val Default = StackTraceFormatterConfig()
    }
}