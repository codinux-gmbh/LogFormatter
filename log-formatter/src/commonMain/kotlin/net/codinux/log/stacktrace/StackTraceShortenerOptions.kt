package net.codinux.log.stacktrace

import kotlin.jvm.JvmOverloads

data class StackTraceShortenerOptions @JvmOverloads constructor(
    /**
     * If set to a value greater 0, adds only the first `maxFramesPerThrowable` stack frames
     * per `Throwable` to stack trace and a line indicating how many frames were omitted.
     */
    val maxFramesPerThrowable: Int? = null,

    /**
     * How many nested caused by Throwables of the Throwable hierarchy should be returned.
     *
     * Setting value to `0` means: Return only the first Throwable, no caused by Throwables.
     *
     * Setting the value to `null` (the default) or a value `less then 0` means:
     * Return the full hierarchy with all nested caused by Throwables.
     */
    val maxNestedThrowables: Int? = null,

    val maxSuppressedThrowables: Int? = null,

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
) {
    companion object {
        val Default by lazy { StackTraceShortenerOptions() }
    }
}