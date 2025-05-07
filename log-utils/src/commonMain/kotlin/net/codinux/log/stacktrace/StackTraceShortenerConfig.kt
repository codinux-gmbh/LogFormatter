package net.codinux.log.stacktrace

data class StackTraceShortenerConfig(
    /**
     * If set to a value greater 0, adds only the first `maxFramesPerThrowable` stack frames
     * per `Throwable` to stack trace and a line indicating how many frames were omitted.
     */
    val maxFramesPerThrowable: Int? = null,
) {
    companion object {
        val Default = StackTraceShortenerConfig()
    }
}