@file:Suppress("UNUSED_VARIABLE")

package net.codinux.log.examples

import net.codinux.kotlin.text.LineSeparator
import net.codinux.log.classname.ClassNameResolver
import net.codinux.log.stacktrace.*
import net.codinux.log.test.TestClasses

class ShowExamples {

    fun getClassNameComponents() {
        // get package name (not available on JavaScript and WASM), class name and enclosing class name
        // (in case of a Companion class, inner class, local class, anonymous function, lambda, ...)
        val classNameComponents = ClassNameResolver.getClassNameComponents(TestClasses.OuterClass.InnerClass::class)
    }

    fun extractStackTrace() {
        val throwable = Throwable("Something went wrong", Throwable("Root cause"))
            .apply { addSuppressed(Throwable("Suppressed exception")) }

        val stackTrace = StackTraceExtractor().extractStackTrace(throwable)

        printStackTrace(stackTrace)
    }

    private fun printStackTrace(stackTrace: StackTrace) {
        println("Message line: ${stackTrace.messageLine}")

        println("Stack frames:")
        stackTrace.frames.forEach { println(it.line) }

        println("Count skipped common frames: ${stackTrace.countSkippedCommonFrames}")

        stackTrace.suppressed.forEach { suppressed ->
            println("Suppressed:")
            printStackTrace(suppressed)
        }

        if (stackTrace.causedBy != null) {
            println("Caused by:")
            printStackTrace(stackTrace.causedBy!!)
        }
    }

    fun shortenStackTrace() {
        val throwable = Throwable("Something went wrong", Throwable("Root cause"))
            .apply { addSuppressed(Throwable("Suppressed exception")) }

        val shortener = StackTraceShortener()

        // keeps at maximum 3 frames per Throwable making it a quite compact and good readable stack trace
        val max3FramesPerThrowable = shortener.shorten(throwable, maxFramesPerThrowable = 3)
    }

    fun formatStackTrace() {
        val throwable = Throwable("Something went wrong")

        val formatter = StackTraceFormatter()

        println(formatter.format(throwable))

        // shows how much better readable stack trace then is
        println(formatter.format(StackTraceShortener().shorten(throwable, maxFramesPerThrowable = 3)))

        // a lot of config options to format stack trace
        println(formatter.format(throwable, StackTraceFormatterOptions(
            messageLineIndent = "",
            stackFrameIndent = "    ",
            causedByIndent = "",
            causedByMessagePrefix = "Caused by: ",
            suppressedExceptionIndent = "    ",
            suppressedExceptionMessagePrefix = "Suppressed: ",
            lineSeparator = LineSeparator.System // or Unix or Windows ...
        )))
    }
}