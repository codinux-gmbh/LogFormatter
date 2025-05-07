package org.example.log.stack

import kotlin.test.fail

/**
 * Copied from logstash-logback-encoder under Apache License 2.0.
 *
 * Moved it to org.example.log.stack for nicer and more common package names in stack traces.
 */
object StackTraceGenerator {

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