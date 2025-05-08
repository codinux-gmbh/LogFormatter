package net.codinux.log.stacktrace

open class StackTraceFormatter(
    protected val options: StackTraceFormatterOptions = StackTraceFormatterOptions.Default,
    protected val stackTraceShortener: StackTraceShortener = StackTraceShortener.Default
) {

    open fun format(throwable: Throwable, options: StackTraceFormatterOptions = this.options) =
        format(stackTraceShortener.shorten(throwable), options)

    open fun format(stackTrace: StackTrace, options: StackTraceFormatterOptions = this.options) =
        format(stackTraceShortener.shorten(stackTrace), options)

    open fun format(stackTrace: ShortenedStackTrace, options: StackTraceFormatterOptions = this.options): String {
        val builder = StringBuilder()

        appendStackTraceAndChildren(stackTrace, builder, options)

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

        stackTrace.suppressed.forEach { suppressed ->
            builder.append(options.lineSeparator)
            appendStackTraceAndChildren(suppressed, builder, options, additionalIndent + options.suppressedExceptionIndent, options.suppressedExceptionMessagePrefix)
        }

        if (exceedsMaxLength(builder, options)) {
            return // no need to add even more characters, maximum length already reached
        }

        stackTrace.causedBy?.let { causedBy ->
            builder.append(options.lineSeparator)
            appendStackTraceAndChildren(causedBy, builder, options, additionalIndent + options.causedByIndent, options.causedByMessagePrefix)
        }
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

    protected open fun formatFrame(frame: StackFrame): String =
        frame.line


    protected open fun cropToMaxLength(builder: StringBuilder, options: StackTraceFormatterOptions) {
        val maxLength = options.maxStackTraceStringLength ?: return

        // TODO: may also show ... 1234 characters truncated
        builder.setLength(maxLength - options.ellipsis.length - options.lineSeparator.length)
        builder.append(options.ellipsis).append(options.lineSeparator)
    }

    protected open fun exceedsMaxLength(builder: StringBuilder, options: StackTraceFormatterOptions): Boolean =
        (options.maxStackTraceStringLength ?: -1) > 0 && builder.length > options.maxStackTraceStringLength!!

}