package net.codinux.log.formatter.quarkus

import io.quarkus.bootstrap.logging.QuarkusDelayedHandler
import net.codinux.log.formatter.quarkus.config.LogFormatterConfig
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

    @JvmOverloads
    fun initQuarkusLogFormatter(config: LogFormatterConfig = LogFormatterConfig()): Handler? {
        if (config.isDefault) {
            return null // no need to adjust ConsoleHandler, no values are set
        }

        try {
            val rootLogger = LogManager.getLogManager().getLogger("")

            // the first struggle is to find the ConsoleHandler which is wrapped in a QuarkusDelayedHandler and
            // in dev and test mode in another anonymous Handler and may in an AsyncHandler if
            // quarkus.log.console.async is set to true
            val consoleHandler = findConsoleHandler(rootLogger)

            // the next struggle is to find the PatternFormatter, which may again is wrapped, e.g. if banner is activated
            val formatter = consoleHandler?.let { findPatternFormatter(it) }

            // and the last one is to find the field formatters like ExceptionFormatter - which again are anonymous classes
            formatter?.let { patchFormatters(it, config) }

            return consoleHandler
        } catch (e: Throwable) {
            logError("Could not modify ConsoleHandler to format console log output", e)
            return null
        }
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

                    try {
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
                    } catch (e: Throwable) {
                        logError("Could not find ConsoleHandler delegate created in LoggingSetupRecorder", e)
                    }
                }
            } else {
                findConsoleHandler(handler)?.let {
                    return it
                }
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
        try {
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
        } catch (e: Throwable) {
            logError("Could not find PatternFormatter in ConsoleHandler", e)
        }

        return null
    }


    private fun patchFormatters(formatter: PatternFormatter, config: LogFormatterConfig) {
        // the step may again is wrapped, e.g. in a ColorPatternFormatter.ColorStep
        val steps = formatter.steps.mapIndexed { index, step -> unwrapStep(step, index) }

        steps.forEach { stepInfo ->
            try {
                patchStepWith(stepInfo, config)?.let { toPatchWith ->
                    if (stepInfo.wrappedStep != null && stepInfo.delegateField != null) { // then replace the wrapped FormatStep
                        stepInfo.delegateField.set(stepInfo.step, toPatchWith)
                    } else {
                        formatter.steps = formatter.steps.apply {
                            this[stepInfo.index] = toPatchWith
                        }
                    }
                }
            } catch (e: Throwable) {
                logError("Could not patch step ${stepInfo.type} ${stepInfo.step} at index ${stepInfo.index}", e)
            }
        }
    }

    private fun patchStepWith(stepInfo: FormatStepInfo, config: LogFormatterConfig): FormatStep? =
        if (stepInfo.type == ItemType.EXCEPTION_TRACE) {
            val options = StackTraceFormatterOptions(rootCauseFirst = config.rootCauseFirst, maxStackTraceStringLength = config.maxStackTraceStringLength)
            val shortener = StackTraceShortener(StackTraceShortenerOptions(config.maxFramesPerThrowable, config.maxNestedThrowables))
            ExceptionFormatStep(StackTraceFormatter(options, shortener), options.lineSeparator)
        } else {
            null
        }

    private fun unwrapStep(step: FormatStep, index: Int): FormatStepInfo {
        try {
            val delegateField = step.javaClass.declaredFields.firstOrNull { it.name == "delegate" }
            if (delegateField != null) {
                if (delegateField.trySetAccessible()) {
                    val delegate = delegateField.get(step)
                    if (delegate is FormatStep) {
                        return FormatStepInfo(step, getStepType(delegate), index, delegate, delegateField)
                    }
                }
            }
        } catch (e: Throwable) {
            logError("Could not unwrap delegate of step $step at index $index", e)
        }

        return FormatStepInfo(step, getStepType(step), index, null, null)
    }

    private fun getStepType(step: FormatStep): ItemType? {
        try {
            val stepClass = step.javaClass

            val getItemTypeMethod = stepClass.declaredMethods.firstOrNull { it.name == "getItemType" && it.parameterTypes.isEmpty() && it.returnType == ItemType::class.java }
            if (getItemTypeMethod != null) {
                getItemTypeMethod.trySetAccessible()
                return getItemTypeMethod.invoke(step) as? ItemType
            }

            val enclosingMethod = stepClass.enclosingMethod
            if (enclosingMethod != null && enclosingMethod.declaringClass == Formatters::class.java) {
                if (enclosingMethod.name == "exceptionFormatStep") {
                    return ItemType.EXCEPTION_TRACE
                }
            }
        } catch (e: Throwable) {
            logError("Could not get step type of $step", e)
        }

        return null
    }


    private fun logError(message: String, error: Throwable?) {
        Logger.getLogger(QuarkusLogFormatterInitializer::class.java.name).log(Level.SEVERE, message, error)
    }

}