package net.codinux.log.formatter.quarkus

import io.quarkus.bootstrap.logging.QuarkusDelayedHandler
import net.codinux.log.stacktrace.StackTraceFormatter
import net.codinux.log.stacktrace.StackTraceFormatterOptions
import net.codinux.log.stacktrace.StackTraceShortener
import net.codinux.log.stacktrace.StackTraceShortenerOptions
import org.jboss.logmanager.ExtFormatter
import org.jboss.logmanager.LogManager
import org.jboss.logmanager.formatters.FormatStep
import org.jboss.logmanager.formatters.FormatStep.ItemType
import org.jboss.logmanager.formatters.Formatters
import org.jboss.logmanager.formatters.PatternFormatter
import org.jboss.logmanager.handlers.AsyncHandler
import org.jboss.logmanager.handlers.ConsoleHandler
import java.util.logging.*

class QuarkusLogFormatterInitializer {

    fun initQuarkusLogFormatter(): Handler? {
        val rootLogger = LogManager.getLogManager().getLogger("")

        // the first struggle is to find the ConsoleHandler which is wrapped in a QuarkusDelayedHandler and
        // in dev and test mode in another anonymous Handler and may in an AsyncHandler if
        // quarkus.log.console.async is set to true
        val consoleHandler = findConsoleHandler(rootLogger)

        // the next struggle is to find the PatternFormatter, which may again is wrapped, e.g. if banner is activated
        val formatter = consoleHandler?.let { findPatternFormatter(it) }

        // and the last one is to find the field formatters like ExceptionFormatter - which again are anonymous classes
        formatter?.let { patchFormatters(it) }

        return consoleHandler
    }


    private fun findConsoleHandler(rootLogger: Logger): ConsoleHandler? {
        rootLogger.handlers.forEach { handler ->
            val handlerClass = handler.javaClass

            if (handler is QuarkusDelayedHandler) {
                val wrappedHandlers = handler.handlers
                wrappedHandlers.forEach { wrappedHandler ->
                    findConsoleHandler(wrappedHandler)?.let {
                        return it
                    }

                    val wrappedHandlerClass = wrappedHandler.javaClass

                    // Quarkus creates ConsoleHandler in LoggingSetupRecorder.configureConsoleHandler() method and
                    // in dev and test mode wraps it in an anonymous Handler
                    val enclosingMethod = wrappedHandlerClass.enclosingMethod
                    if (enclosingMethod != null && enclosingMethod.name == "configureConsoleHandler" &&
                        enclosingMethod.declaringClass.name == "io.quarkus.runtime.logging.LoggingSetupRecorder") {
                        val delegateField = wrappedHandlerClass.declaredFields.firstOrNull { it.name.contains("delegate") }
                        if (delegateField != null) {
                            if (delegateField.trySetAccessible()) {
                                val delegate = delegateField.get(wrappedHandler)
                                if (delegate is ConsoleHandler) {
                                    return delegate
                                }
                            }
                        }
                    }
                }
            } else {
                return findConsoleHandler(handler)
            }
        }

        return null
    }

    private fun findConsoleHandler(handler: Handler): ConsoleHandler? =
        if (handler is ConsoleHandler) {
            handler
        } else if (handler is AsyncHandler) {
            handler.handlers.firstNotNullOfOrNull { findConsoleHandler(it) }
        } else {
            null
        }


    private fun findPatternFormatter(handler: ConsoleHandler): PatternFormatter? {
        val formatter = handler.formatter
        if (formatter is PatternFormatter) {
            return formatter
        } else if (formatter is ExtFormatter.Delegating) { // e.g. TextBannerFormatter, which is used in case of activated banner, is derived from ExtFormatter.Delegating
            val delegatingFormatterClass = ExtFormatter.Delegating::class.java
            val delegateField = delegatingFormatterClass.declaredFields.firstOrNull { it.name == "delegate" }
            if (delegateField != null) {
                if (delegateField.trySetAccessible()) {
                    val delegate = delegateField.get(formatter) as? Formatter
                    if (delegate is PatternFormatter) {
                        return delegate
                    }
                }
            }
        }

        return null
    }


    private fun patchFormatters(formatter: PatternFormatter) {
        // the step may again is wrapped, e.g. in a ColorPatternFormatter.ColorStep
        val steps = formatter.steps.map { unwrapStep(it) }

        val stepsByType = getStepTypes(steps)

        stepsByType.forEach { (originalStep, wrappedStep, itemType) ->
            patchStepWith(itemType, wrappedStep ?: originalStep)?.let { toPatchWith ->
                val index = formatter.steps.indexOf(originalStep)
                if (index > -1) {
                    if (wrappedStep != null) { // then replace the wrapped FormatStep
                        val delegateField = originalStep.javaClass.declaredFields.firstOrNull { it.name == "delegate" }
                        if (delegateField != null) {
                            if (delegateField.trySetAccessible()) {
                                delegateField.set(originalStep, toPatchWith)
                            }
                        }
                    } else {
                        formatter.steps = formatter.steps.apply {
                            this[index] = toPatchWith
                        }
                    }
                }
            }
        }
    }

    private fun patchStepWith(type: ItemType?, step: FormatStep): FormatStep? =
        if (type == ItemType.EXCEPTION_TRACE) {
            val options = StackTraceFormatterOptions(rootCauseFirst = true, maxStackTraceStringLength = 500)
            val shortener = StackTraceShortener(StackTraceShortenerOptions(maxFramesPerThrowable = 4, maxNestedThrowables = 0))
            ExceptionFormatStep(StackTraceFormatter(options, shortener), options.lineSeparator)
        } else {
            null
        }

    private fun unwrapStep(step: FormatStep): Pair<FormatStep, FormatStep?> {
        val delegateField = step.javaClass.declaredFields.firstOrNull { it.name == "delegate" }
        if (delegateField != null) {
            if (delegateField.trySetAccessible()) {
                val delegate = delegateField.get(step)
                if (delegate is FormatStep) {
                    return step to delegate
                }
            }
        }

        return step to null
    }

    private fun getStepTypes(steps: List<Pair<FormatStep, FormatStep?>>): List<Triple<FormatStep, FormatStep?, ItemType?>> =
        steps.map { (originalStep, wrappedStep) ->
            Triple(originalStep, wrappedStep, wrappedStep?.let { getStepType(it) } ?: getStepType(originalStep))
        }

    private fun getStepType(step: FormatStep): ItemType? {
        val stepClass = step.javaClass

        val itemTypeField = stepClass.declaredMethods.firstOrNull { it.name == "getItemType" && it.parameterTypes.isEmpty() && it.returnType == ItemType::class.java }
        if (itemTypeField != null) {
            itemTypeField.trySetAccessible()
            return itemTypeField.invoke(step) as ItemType?
        }

        val fields = stepClass.declaredFields

        val enclosingMethod = stepClass.enclosingMethod
        if (enclosingMethod != null && enclosingMethod.declaringClass == Formatters::class.java) {
            if (enclosingMethod.name == "exceptionFormatStep") {
                return ItemType.EXCEPTION_TRACE
            }
        }

        return null
    }

}