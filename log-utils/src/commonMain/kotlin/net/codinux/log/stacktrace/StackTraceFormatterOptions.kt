package net.codinux.log.stacktrace

import net.codinux.kotlin.text.LineSeparator

data class StackTraceFormatterOptions(
    val messageLineIndent: String = "",
    val stackFrameIndent: String = "    ",

    val causedByIndent: String = "",
    val causedByMessagePrefix: String = "Caused by: ",

    /**
     * If suppressed exceptions should be ignored and not included in stack trace string.
     *
     * Defaults to `false`.
     */
    val ignoreSuppressedExceptions: Boolean = false,
    val suppressedExceptionIndent: String = "    ",
    val suppressedExceptionMessagePrefix: String = "Suppressed: ",
    val lineSeparator: String = LineSeparator.System,

    /**
     * The ellipsis string to show for skipped common frames, truncated frames when
     * [StackTraceShortenerOptions.maxFramesPerThrowable] is set or if stack trace
     * string exceeds [maxStackTraceStringLength].
     */
    val ellipsis: String = "...",

    /**
     * The maximum amount of characters the resulting stack trace string may have.
     *
     * Set to `null` or value less than zero to disable stack trace string truncation.
     *
     * Be aware, count characters used may not equal count bytes used, which may is the
     * more important value when sending log to a log storage like Loki or Elasticsearch,
     * if stack trace contains non-UTF-8 characters.
     */
    val maxStackTraceStringLength: Int? = null,
) {
    companion object {
        val Default = StackTraceFormatterOptions()
    }
}