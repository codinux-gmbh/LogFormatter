package net.codinux.log.stacktrace

open class StackTraceInverter {

    companion object {
        val Default: StackTraceInverter = StackTraceInverter()
    }


    open fun rootCauseFirst(stackTrace: StackTrace): StackTrace {
        val causedBy = stackTrace.causedBy

        return if (causedBy == null) {
            stackTrace
        } else {
            val outermostThrowable = StackTrace(stackTrace.messageLine, stackTrace.stackTrace, null,
                stackTrace.suppressed, stackTrace.countSkippedCommonFrames, isRootCauseFirst = true)
            reverseOrder(causedBy, outermostThrowable)
        }
    }

    protected open fun reverseOrder(cause: StackTrace, wrappedBy: StackTrace): StackTrace {
        val reversed = StackTrace(cause.messageLine, cause.stackTrace, wrappedBy, cause.suppressed,
            cause.countSkippedCommonFrames, isRootCauseFirst = true)

        val innerCause = cause.causedBy
        return if (innerCause == null) {
            reversed
        } else {
            reverseOrder(innerCause, reversed)
        }
    }

}