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

    /**
     * If `true`, prints the innermost exception (the root cause) at the beginning of the output,
     * reversing the traditional order of the exception cause chain.
     *
     * This allows you to see the actual underlying error more quickly, instead of having to scroll
     * to the end of a long stack trace to find it. The root cause is typically the most relevant
     * and informative part of an exception, especially in deeply nested failure scenarios.
     *
     * Be aware, this only affects the order of the printed exceptions. But currently it does not
     * change how common stack frames are handled: shared frames between nested exceptions are still
     * printed only for the outermost exception. Inner exceptions will continue to include a summary
     * line such as `... N common frames omitted`.
     *
     * If `false`, stack traces are printed in the conventional order — starting with the outermost
     * exception and following the cause chain to the root.
     */
    val rootCauseFirst: Boolean = false,
    val wrappedByIndent: String = causedByIndent,
    val wrappedByMessagePrefix: String = "Wrapped by: ",

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