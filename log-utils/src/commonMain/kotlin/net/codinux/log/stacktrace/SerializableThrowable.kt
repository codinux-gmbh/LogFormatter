package net.codinux.log.stacktrace

data class SerializableThrowable(
    val type: String,
    val message: String? = null,
    val stackTrace: String? = null,
    val cause: SerializableThrowable? = null,
    //@Transient
    val originalException: Throwable? = null,

) {
    constructor(throwable: Throwable) : this(throwable::class.simpleName ?: "<unknown type>", // JavaScript throws an exception on .qualifiedName
        throwable.message, throwable.stackTraceToString(), throwable.cause?.let { SerializableThrowable(it) }, throwable)

    override fun toString() = "$type: $message"
}

