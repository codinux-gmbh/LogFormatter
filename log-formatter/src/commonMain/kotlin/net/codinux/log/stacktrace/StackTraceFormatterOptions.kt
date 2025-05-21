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
     * By default the last stack frame does not end with a line separator.
     *
     * Set to `true` to add a line separator at the end of the last stack frame.
     */
    val addLineSeparatorAtEnd: Boolean = false,

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
        val Default by lazy { StackTraceFormatterOptions() }
    }

    open class Builder {
        protected var messageLineIndent: String = ""
        protected var stackFrameIndent: String = "    "

        protected var causedByIndent: String = ""
        protected var causedByMessagePrefix: String = "Caused by:"

        protected var ignoreSuppressedExceptions: Boolean = false
        protected var suppressedExceptionIndent: String = "    "
        protected var suppressedExceptionMessagePrefix: String = "Suppressed:"

        protected var rootCauseFirst: Boolean = false
        protected var wrappedByIndent: String = causedByIndent
        protected var wrappedByMessagePrefix: String = "Wrapped by:"

        protected var lineSeparator: String = LineSeparator.System
        protected var addLineSeparatorAtEnd: Boolean = false

        protected var ellipsis: String = "..."
        protected var maxStackTraceStringLength: Int? = null

        fun messageLineIndent(value: String) = apply { messageLineIndent = value }
        fun stackFrameIndent(value: String) = apply { stackFrameIndent = value }

        fun causedByIndent(value: String) = apply { causedByIndent = value }
        fun causedByMessagePrefix(value: String) = apply { causedByMessagePrefix = value }

        fun ignoreSuppressedExceptions(value: Boolean) = apply { ignoreSuppressedExceptions = value }
        fun suppressedExceptionIndent(value: String) = apply { suppressedExceptionIndent = value }
        fun suppressedExceptionMessagePrefix(value: String) = apply { suppressedExceptionMessagePrefix = value }

        fun rootCauseFirst(value: Boolean) = apply { rootCauseFirst = value }
        fun wrappedByIndent(value: String) = apply { wrappedByIndent = value }
        fun wrappedByMessagePrefix(value: String) = apply { wrappedByMessagePrefix = value }

        fun lineSeparator(value: String) = apply { lineSeparator = value }
        fun addLineSeparatorAtEnd(value: Boolean) = apply { addLineSeparatorAtEnd = value }

        fun ellipsis(value: String) = apply { ellipsis = value }
        fun maxStackTraceStringLength(value: Int?) = apply { maxStackTraceStringLength = value }

        fun build() = StackTraceFormatterOptions(
            messageLineIndent = messageLineIndent,
            stackFrameIndent = stackFrameIndent,

            causedByIndent = causedByIndent,
            causedByMessagePrefix = causedByMessagePrefix,

            ignoreSuppressedExceptions = ignoreSuppressedExceptions,
            suppressedExceptionIndent = suppressedExceptionIndent,
            suppressedExceptionMessagePrefix = suppressedExceptionMessagePrefix,

            rootCauseFirst = rootCauseFirst,
            wrappedByIndent = wrappedByIndent,
            wrappedByMessagePrefix = wrappedByMessagePrefix,

            lineSeparator = lineSeparator,
            addLineSeparatorAtEnd = addLineSeparatorAtEnd,

            ellipsis = ellipsis,
            maxStackTraceStringLength = maxStackTraceStringLength
        )
    }

}