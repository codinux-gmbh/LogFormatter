package net.codinux.log.error

object ErrorReporter {

    fun reportError(message: String, e: Throwable? = null) {
        println("Error: $message")
        e?.printStackTrace()
    }

}