package net.codinux.log.stacktrace

import kotlin.jvm.JvmOverloads

open class StackTraceFormatter @JvmOverloads constructor(
    protected val options: StackTraceFormatterOptions = StackTraceFormatterOptions.Default,
    protected val stackTraceShortener: StackTraceShortener = StackTraceShortener.Default
) {

    companion object {
        val Default by lazy { StackTraceFormatter() }
    }


    constructor(options: StackTraceFormatterOptions, shortenerOptions: StackTraceShortenerOptions = StackTraceShortenerOptions.Default)
            : this(options, StackTraceShortener(shortenerOptions))


    @JvmOverloads
    open fun format(throwable: Throwable, options: StackTraceFormatterOptions = this.options,
                    shortenerOptions: StackTraceShortenerOptions = stackTraceShortener.options) =
        format(stackTraceShortener.shorten(throwable, shortenerOptions), options)

    @JvmOverloads
    open fun format(stackTrace: StackTrace, options: StackTraceFormatterOptions = this.options,
                    shortenerOptions: StackTraceShortenerOptions = stackTraceShortener.options) =
        format(stackTraceShortener.shorten(stackTrace, shortenerOptions), options)

    @JvmOverloads
    open fun format(stackTrace: ShortenedStackTrace, options: StackTraceFormatterOptions = this.options): String {
        val builder = StringBuilder()

        appendStackTraceAndChildren(stackTrace, builder, options)

        if (options.addLineSeparatorAtEnd) {
            builder.append(options.lineSeparator)
        }

        if (exceedsMaxLength(builder, options)) {
            cropToMaxLength(builder, options)
        }

        return builder.toString()
    }

    protected open fun appendStackTraceAndChildren(stackTrace: ShortenedStackTrace, builder: StringBuilder, options: StackTraceFormatterOptions,
                                                   additionalIndent: String = "", messageLinePrefix: String = "") {
        appendStackTrace(stackTrace, builder, options, additionalIndent, messageLinePrefix)

        if (exceedsMaxLength(builder, options)) {
            return // no need to add even more characters, maximum length already reached
        }

        if (options.ignoreSuppressedExceptions == false) {
            stackTrace.suppressed.forEach { suppressed ->
                builder.append(options.lineSeparator)
                appendStackTraceAndChildren(suppressed, builder, options, additionalIndent + options.suppressedExceptionIndent, options.suppressedExceptionMessagePrefix)
            }

            if (exceedsMaxLength(builder, options)) {
                return // no need to add even more characters, maximum length already reached
            }
        }
        appendCountSkippedSuppressedThrowables(stackTrace, builder, options, additionalIndent + options.suppressedExceptionIndent)

        val (nestedThrowableIndent, nestedThrowablePrefix) = if (stackTrace.isRootCauseFirst) options.wrappedByIndent to options.wrappedByMessagePrefix
                                                            else options.causedByIndent to options.causedByMessagePrefix
        stackTrace.causedBy?.let { causedBy ->
            builder.append(options.lineSeparator)
            appendStackTraceAndChildren(causedBy, builder, options, additionalIndent + nestedThrowableIndent, nestedThrowablePrefix)
        }

        appendCountSkippedNestedThrowables(stackTrace, builder, options, additionalIndent + nestedThrowableIndent)
    }

    protected open fun appendStackTrace(stackTrace: ShortenedStackTrace, builder: StringBuilder, options: StackTraceFormatterOptions,
                                        additionalIndent: String = "", messageLinePrefix: String = "") {
        builder.append(additionalIndent + options.messageLineIndent + messageLinePrefix + stackTrace.messageLine)

        stackTrace.framesToDisplay.forEach { frame ->
            builder.append(options.lineSeparator + additionalIndent + options.stackFrameIndent + formatFrame(frame))
        }

        if (stackTrace.countTruncatedFrames > 0) {
            builder.append(options.lineSeparator + additionalIndent + options.stackFrameIndent + options.ellipsis + " ${stackTrace.countTruncatedFrames} frames truncated")
            if (stackTrace.countSkippedCommonFrames > 0) {
                builder.append(" (including ${stackTrace.countSkippedCommonFrames} common frames)")
            }
        } else if (stackTrace.countSkippedCommonFrames > 0) {
            // TODO: Kotlin uses "... and 18 more common stack frames skipped", what is better?
            builder.append(options.lineSeparator + additionalIndent + options.stackFrameIndent + options.ellipsis + " ${stackTrace.countSkippedCommonFrames} common frames omitted")
        }
    }

    protected open fun appendCountSkippedNestedThrowables(stackTrace: ShortenedStackTrace, builder: StringBuilder, options: StackTraceFormatterOptions, additionalIndent: String) {
        if (stackTrace.countSkippedNestedThrowables > 0) {
            builder.append(options.lineSeparator + additionalIndent + options.ellipsis + " ${stackTrace.countSkippedNestedThrowables} nested cause(s) omitted")
        }
    }

    protected open fun appendCountSkippedSuppressedThrowables(stackTrace: ShortenedStackTrace, builder: StringBuilder, options: StackTraceFormatterOptions, additionalIndent: String) {
        if (stackTrace.countSkippedSuppressedThrowables > 0) {
            builder.append(options.lineSeparator + additionalIndent + options.ellipsis + " ${stackTrace.countSkippedSuppressedThrowables} suppressed exception(s) omitted")
        }
    }

    protected open fun formatFrame(frame: StackFrame): String =
        frame.line


    protected open fun cropToMaxLength(builder: StringBuilder, options: StackTraceFormatterOptions) {
        val maxLength = options.maxStackTraceStringLength ?: return

        if (maxLength <= 0) {
            // don't do anything then
        } else if (maxLength > options.ellipsis.length) {
            // TODO: may also show ... 1234 characters truncated
            builder.setLength(maxLength - options.ellipsis.length)
            builder.append(options.ellipsis)
        } else { // maxLength shorter than ellipsis string
            builder.setLength(0)
            builder.append(options.ellipsis.take(maxLength))
        }
    }

    protected open fun exceedsMaxLength(builder: StringBuilder, options: StackTraceFormatterOptions): Boolean {
        val maxLength = options.maxStackTraceStringLength ?: return false

        return maxLength > 0 && builder.length > maxLength
    }

}