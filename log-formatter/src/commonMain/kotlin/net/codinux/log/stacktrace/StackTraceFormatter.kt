package net.codinux.log.stacktrace

import kotlin.jvm.JvmOverloads

open class StackTraceFormatter @JvmOverloads constructor(
    protected val options: StackTraceFormatterOptions = StackTraceFormatterOptions.Default,
    protected val stackTraceShortener: StackTraceShortener = StackTraceShortener.Default
) {

    companion object {
        val Default by lazy { StackTraceFormatter() }
    }


    constructor(options: StackTraceFormatterOptions,
                shortenerOptions: StackTraceShortenerOptions = StackTraceShortenerOptions.Default)
            : this(options, StackTraceShortener(shortenerOptions))


    @JvmOverloads
    open fun format(throwable: Throwable, options: StackTraceFormatterOptions = this.options) =
        format(stackTraceShortener.shorten(throwable), options)

    @JvmOverloads
    open fun format(stackTrace: StackTrace, options: StackTraceFormatterOptions = this.options) =
        format(stackTraceShortener.shorten(stackTrace), options)

    @JvmOverloads
    open fun format(stackTrace: ShortenedStackTrace, options: StackTraceFormatterOptions = this.options): String {
        val builder = StringBuilder()

        if (options.rootCauseFirst) {
            appendStackTraceAndChildrenRootCauseFirst(stackTrace, builder, options)
        } else {
            appendStackTraceAndChildrenRootCauseLast(stackTrace, builder, options)
        }

        if (options.addLineSeparatorAtEnd) {
            builder.append(options.lineSeparator)
        }

        if (exceedsMaxLength(builder, options)) {
            cropToMaxLength(builder, options)
        }

        return builder.toString()
    }

    protected open fun appendStackTraceAndChildrenRootCauseLast(stackTrace: ShortenedStackTrace, builder: StringBuilder, options: StackTraceFormatterOptions,
                                                                additionalIndent: String = "", messageLinePrefix: String = "") {
        appendStackTrace(stackTrace, builder, options, additionalIndent, messageLinePrefix)

        if (exceedsMaxLength(builder, options)) {
            return // no need to add even more characters, maximum length already reached
        }

        if (options.ignoreSuppressedExceptions == false) {
            stackTrace.suppressed.forEach { suppressed ->
                builder.append(options.lineSeparator)
                appendStackTraceAndChildrenRootCauseLast(suppressed, builder, options, additionalIndent + options.suppressedExceptionIndent, options.suppressedExceptionMessagePrefix)
            }

            if (exceedsMaxLength(builder, options)) {
                return // no need to add even more characters, maximum length already reached
            }
        }
        appendCountSkippedSuppressedThrowables(stackTrace, builder, options, additionalIndent + options.suppressedExceptionIndent)

        stackTrace.causedBy?.let { causedBy ->
            builder.append(options.lineSeparator)
            appendStackTraceAndChildrenRootCauseLast(causedBy, builder, options, additionalIndent + options.causedByIndent, options.causedByMessagePrefix)
        }

        appendCountSkippedNestedThrowables(stackTrace, builder, options, additionalIndent + options.causedByIndent)
    }

    protected open fun appendStackTraceAndChildrenRootCauseFirst(stackTrace: ShortenedStackTrace, builder: StringBuilder, options: StackTraceFormatterOptions,
                                                   additionalIndent: String = "", messageLinePrefix: String = "") {

        var messageLinePrefixToUse = messageLinePrefix
        stackTrace.causedBy?.let { causedBy ->
            appendStackTraceAndChildrenRootCauseFirst(causedBy, builder, options, additionalIndent + options.wrappedByIndent)
            builder.append(options.lineSeparator)

            if (messageLinePrefixToUse.isBlank()) {
                messageLinePrefixToUse = options.wrappedByMessagePrefix
            }
        }


        appendStackTrace(stackTrace, builder, options, additionalIndent, messageLinePrefixToUse)

        if (exceedsMaxLength(builder, options)) {
            return // no need to add even more characters, maximum length already reached
        }

        if (options.ignoreSuppressedExceptions == false) {
            stackTrace.suppressed.forEach { suppressed ->
                builder.append(options.lineSeparator)
                appendStackTraceAndChildrenRootCauseFirst(suppressed, builder, options, additionalIndent + options.suppressedExceptionIndent, options.suppressedExceptionMessagePrefix)
            }

            if (exceedsMaxLength(builder, options)) {
                return // no need to add even more characters, maximum length already reached
            }
        }
        appendCountSkippedSuppressedThrowables(stackTrace, builder, options, additionalIndent + options.suppressedExceptionIndent)

        appendCountSkippedNestedThrowables(stackTrace, builder, options, additionalIndent + options.wrappedByIndent)
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

        // TODO: may also show ... 1234 characters truncated
        builder.setLength(maxLength - options.ellipsis.length)
        builder.append(options.ellipsis)
    }

    protected open fun exceedsMaxLength(builder: StringBuilder, options: StackTraceFormatterOptions): Boolean =
        (options.maxStackTraceStringLength ?: -1) > 0 && builder.length > options.maxStackTraceStringLength!!

}