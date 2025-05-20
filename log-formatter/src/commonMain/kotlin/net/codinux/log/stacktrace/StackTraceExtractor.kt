package net.codinux.log.stacktrace

import net.codinux.log.error.ErrorReporter

open class StackTraceExtractor {

    companion object {
        const val CausedByPrefix = "Caused by: "
        const val SuppressedPrefix = "Suppressed: "
        val SuppressedExceptionLineRegex = Regex("^\\s*$SuppressedPrefix.+")

        // Finds lines that state the count of skipped common frames:
        // Java: 	... 46 more
        // all others:     ... and 13 more common stack frames skipped
        val SkippedCommonStackFramesIndicatorRegex = Regex("""^\s*\.\.\. (?:and )?(\d+) more\s*(?:common stack frames skipped)?""")

        val Default by lazy { StackTraceExtractor() }
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
            val linesAfterMessage = stackTraceLines.drop(1)

            val stackFramesEndIndex = linesAfterMessage.indexOfFirst { isCausedByLine(it) || isSuppressedExceptionLine(it) }
            val remainingStackTraceLines = if (stackFramesEndIndex == -1) linesAfterMessage else linesAfterMessage.subList(0, stackFramesEndIndex)
            val (stackFramesLines, countSkippedCommonFrames) = getStackFramesAndCountCommonStackFrames(remainingStackTraceLines)
            val stackFrames = stackFramesLines.map { extractStackFrame(it) }

            if (stackFramesEndIndex == -1) {
                StackTrace(messageLine, stackFrames, countSkippedCommonFrames = countSkippedCommonFrames)
            } else {
                val (causedBy, suppressed) = extractCausedByAndSuppressed(linesAfterMessage.drop(stackFramesEndIndex))

                StackTrace(messageLine, stackFrames, causedBy, suppressed, countSkippedCommonFrames)
            }
        }

    private fun extractStackFrame(stackFrameLine: String): StackFrame {
        val trimmedLine = stackFrameLine.trim() // remove original indent from stack frame line
        val originalIndent = stackFrameLine.substringBefore(trimmedLine, "")

        return StackFrame(trimmedLine, originalIndent, stackFrameLine)
    }

    protected open fun getStackFramesAndCountCommonStackFrames(stackTraceLinesWithoutMessage: List<String>): Pair<List<String>, Int> {
        var lines = stackTraceLinesWithoutMessage

        // remove empty lines from end of stack trace
        while (lines.isNotEmpty() && lines.last().isBlank()) {
            lines = lines.dropLast(1)
        }

        val countSkippedCommonFrames = extractSkippedCommonFrames(lines)
        if (countSkippedCommonFrames != null) {
            return Pair(lines.dropLast(1), countSkippedCommonFrames)
        }

        return Pair(lines, 0)
    }

    // VisibleForTesting
    open fun extractSkippedCommonFrames(stackTraceLines: List<String>): Int? {
        if (stackTraceLines.isNotEmpty()) {
            val isSkippedCommonFramesLine = SkippedCommonStackFramesIndicatorRegex.matchEntire(stackTraceLines.last())

            if (isSkippedCommonFramesLine != null) {
                return isSkippedCommonFramesLine.groupValues[1].toInt()
            }
        }

        return null
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

    // VisibleForTesting
    open fun isCausedByLine(line: String): Boolean =
        line.startsWith(CausedByPrefix)

    // VisibleForTesting
    open fun isSuppressedExceptionLine(line: String): Boolean =
        line.matches(SuppressedExceptionLineRegex)

}