package net.codinux.log.stacktrace

import net.codinux.log.error.ErrorReporter

open class StackTraceExtractor {

    companion object {
        const val CausedByPrefix = "Caused by: "
        const val SuppressedPrefix = "Suppressed: "
        val SuppressedExceptionLineRegex = Regex("^\\s*$SuppressedPrefix.+")
    }

    open fun extractStackTrace(throwable: Throwable): StackTrace =
        extractStackTrace(throwable.stackTraceToString())

    open fun extractStackTrace(stackTraceString: String): StackTrace =
        extractStackTrace(stackTraceString.lines())

    protected open fun extractStackTrace(stackTraceLines: List<String>): StackTrace =
        if (stackTraceLines.isEmpty()) {
            StackTrace("Empty string, not a Stack Trace", emptyList())
        } else {
            val messageLine = stackTraceLines.first()
            val linesAfterMessage = stackTraceLines.drop(0)

            val stackFramesEndIndex = linesAfterMessage.indexOfFirst { isCausedByLine(it) || isSuppressedExceptionLine(it) }
            val stackFramesLines = if (stackFramesEndIndex == -1) linesAfterMessage else linesAfterMessage.subList(0, stackFramesEndIndex)
            val stackFrames = stackFramesLines.map { StackFrame(it) }

            if (stackFramesEndIndex == -1) {
                StackTrace(messageLine, stackFrames)
            } else {
                val (causedBy, suppressed) = extractCausedByAndSuppressed(linesAfterMessage.drop(stackFramesEndIndex))

                StackTrace(messageLine, stackFrames, causedBy, suppressed)
            }
        }

    protected open fun extractCausedByAndSuppressed(stackTraceLines: List<String>): Pair<StackTrace?, List<StackTrace>> {
        if (stackTraceLines.isEmpty()) { // should never happen at this point, just to be on the safe side
            return Pair(null, emptyList())
        }

        var messageLine = stackTraceLines.first()
        return if (isCausedByLine(messageLine)) {
            messageLine = messageLine.substringAfter(CausedByPrefix)

            val remainingLines = listOf(messageLine) + stackTraceLines.drop(1)
            val causedBy = extractStackTrace(remainingLines)

            Pair(causedBy, emptyList())
        } else if (isSuppressedExceptionLine(messageLine)) {
            messageLine = messageLine.substringAfter(SuppressedPrefix)

            val remainingLines = listOf(messageLine) + stackTraceLines.drop(1)
            val suppressed = extractStackTrace(remainingLines)

            Pair(suppressed.causedBy, listOf(suppressed) + suppressed.suppressed)
        } else {
            ErrorReporter.reportError("Cannot extract causedBy or suppressed Stack Trace. " +
                    "Unexpected stack trace line '${stackTraceLines.first()}' in stack trace, expected to start with '$CausedByPrefix' or '$SuppressedPrefix'")
            Pair(null, emptyList())
        }
    }

    open fun isCausedByLine(line: String): Boolean =
        line.startsWith(CausedByPrefix)

    open fun isSuppressedExceptionLine(line: String): Boolean =
        line.matches(SuppressedExceptionLineRegex)

}