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

    val rootCauseFirst: Boolean = false,
) {
    companion object {
        val Default by lazy { StackTraceShortenerOptions() }
    }
}