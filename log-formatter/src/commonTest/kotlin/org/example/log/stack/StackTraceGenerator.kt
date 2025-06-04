package org.example.log.stack

import kotlin.test.fail

/**
 * Copied from logstash-logback-encoder under Apache License 2.0.
 *
 * Moved it to org.example.log.stack for nicer and more common package names in stack traces.
 */
object StackTraceGenerator {

    const val ExceptionsNamespace = "org.example.log.stack."

    const val RootCauseMessageLineUnqualified = "RootCauseException: Root cause"

    const val FirstCausedByLineUnqualified = "ParentException: Wrapper #1"

    const val SecondCausedByLineUnqualified = "ParentException: Wrapper #2"

    const val ThirdCausedByLineUnqualified = "ParentException: Wrapper #3"

    const val FirstSuppressedExceptionLineUnqualified = "SuppressedException: Suppressed #1"


    fun generateSingle(): Throwable = try {
        oneSingle()
        fail("Will never come to here")
    } catch (e: Throwable) {
        e
    }

    fun oneSingle() {
        twoSingle()
    }

    private fun twoSingle() {
        threeSingle()
    }

    private fun threeSingle() {
        four()
    }

    private fun four() {
        five()
    }

    private fun five() {
        six()
    }

    private fun six() {
        seven()
    }

    private fun seven() {
        eight()
    }

    private fun eight() {
        throw RootCauseException("Root cause")
    }

    fun generateCausedBy(): Throwable = try {
        oneCausedBy()
        fail("Will never come to here")
    } catch (e: Throwable) {
        e
    }

    fun generateTwoCausedBy(): Throwable = try {
        throw generateCausedBy()
    } catch (e: Throwable) {
        ParentException("Wrapper #2", e)
    }

    fun generateThreeCausedBy(): Throwable = try {
        throw generateTwoCausedBy()
    } catch (e: Throwable) {
        ParentException("Wrapper #3", e)
    }

    private fun oneCausedBy() {
        twoCausedBy()
    }

    private fun twoCausedBy() {
        try {
            threeSingle()
        } catch (e: Throwable) {
            throw ParentException("Wrapper #1", e)
        }
    }

    fun generateSuppressed(): Throwable = try {
        oneSuppressed()
        fail("Will never come to here")
    } catch (e: Throwable) {
        e
    }

    fun generateTwoSuppressed(): Throwable = RootCauseException("Root cause").apply {
        addSuppressed(SuppressedException("Suppressed #1"))
        addSuppressed(SuppressedException("Suppressed #2"))
    }

    private fun oneSuppressed() {
        twoSuppressed()
    }

    private fun twoSuppressed() {
        try {
            threeSingle()
        } catch (e: Throwable) {
            e.addSuppressed(SuppressedException("Suppressed #1"))
            throw e
        }
    }

}