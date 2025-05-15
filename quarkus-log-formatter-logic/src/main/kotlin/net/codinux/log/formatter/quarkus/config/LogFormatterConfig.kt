package net.codinux.log.formatter.quarkus.config

data class LogFormatterConfig(
    val rootCauseFirst: Boolean = false,

    val maxFramesPerThrowable: Int? = null,
    val maxNestedThrowables: Int? = null,

    val maxStackTraceStringLength: Int? = null,
) {
    val isDefault by lazy { rootCauseFirst == false && maxFramesPerThrowable == null &&
            maxNestedThrowables == null && maxStackTraceStringLength == null }
}