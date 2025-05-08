package net.codinux.log.stacktrace

open class StackTraceFormatter(
    protected val stackTraceShortener: StackTraceShortener = StackTraceShortener.Default
) {

    open fun format(throwable: Throwable, config: StackTraceFormatterConfig = StackTraceFormatterConfig.Default) =
        format(stackTraceShortener.shorten(throwable), config)

    open fun format(stackTrace: StackTrace, config: StackTraceFormatterConfig = StackTraceFormatterConfig.Default) =
        format(stackTraceShortener.shorten(stackTrace), config)

    open fun format(stackTrace: ShortenedStackTrace, config: StackTraceFormatterConfig = StackTraceFormatterConfig.Default): String {
        val builder = StringBuilder()

        appendStackTraceAndChildren(stackTrace, builder, config)

        if (exceedsMaxLength(builder, config)) {
            cropToMaxLength(builder, config)
        }

        return builder.toString()
    }

    protected open fun appendStackTraceAndChildren(stackTrace: ShortenedStackTrace, builder: StringBuilder, config: StackTraceFormatterConfig,
                                        additionalIndent: String = "", messageLinePrefix: String = "") {
        appendStackTrace(stackTrace, builder, config, additionalIndent, messageLinePrefix)

        if (exceedsMaxLength(builder, config)) {
            return // no need to add even more characters, maximum length already reached
        }

        stackTrace.suppressed.forEach { suppressed ->
            builder.append(config.lineSeparator)
            appendStackTrace(suppressed, builder, config, additionalIndent + config.suppressedExceptionIndent, config.suppressedExceptionMessagePrefix)
        }

        if (exceedsMaxLength(builder, config)) {
            return // no need to add even more characters, maximum length already reached
        }

        stackTrace.causedBy?.let { causedBy ->
            builder.append(config.lineSeparator)
            appendStackTraceAndChildren(causedBy, builder, config, additionalIndent + config.causedByIndent, config.causedByMessagePrefix)
        }
    }

    protected open fun appendStackTrace(stackTrace: ShortenedStackTrace, builder: StringBuilder, config: StackTraceFormatterConfig,
                                        additionalIndent: String = "", messageLinePrefix: String = "") {
        builder.append(additionalIndent + config.messageLineIndent + messageLinePrefix + stackTrace.messageLine)

        stackTrace.framesToDisplay.forEach { frame ->
            builder.append(config.lineSeparator + additionalIndent + config.stackFrameIndent + formatFrame(frame))
        }

        if (stackTrace.countTruncatedFrames > 0) {
            builder.append(config.lineSeparator + additionalIndent + config.stackFrameIndent + config.ellipsis + " ${stackTrace.countTruncatedFrames} frames truncated")
            if (stackTrace.countSkippedCommonFrames > 0) {
                builder.append(" (including ${stackTrace.countSkippedCommonFrames} common frames)")
            }
        } else if (stackTrace.countSkippedCommonFrames > 0) {
            // TODO: Kotlin uses "... and 18 more common stack frames skipped", what is better?
            builder.append(config.lineSeparator + additionalIndent + config.stackFrameIndent + config.ellipsis + " ${stackTrace.countSkippedCommonFrames} common frames omitted")
        }
    }

    protected open fun formatFrame(frame: StackFrame): String =
        frame.line


    protected open fun cropToMaxLength(builder: StringBuilder, config: StackTraceFormatterConfig) {
        val maxLength = config.maxStackTraceStringLength ?: return

        // TODO: may also show ... 1234 characters truncated
        builder.setLength(maxLength - config.ellipsis.length - config.lineSeparator.length)
        builder.append(config.ellipsis).append(config.lineSeparator)
    }

    protected open fun exceedsMaxLength(builder: StringBuilder, config: StackTraceFormatterConfig): Boolean =
        (config.maxStackTraceStringLength ?: -1) > 0 && builder.length > config.maxStackTraceStringLength!!

}