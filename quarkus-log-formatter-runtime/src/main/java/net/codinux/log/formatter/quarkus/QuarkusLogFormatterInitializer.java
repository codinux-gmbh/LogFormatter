package net.codinux.log.formatter.quarkus;

import net.codinux.log.formatter.quarkus.config.LogFormatterConfig;
import net.codinux.log.stacktrace.StackTraceFormatter;
import net.codinux.log.stacktrace.StackTraceFormatterOptions;
import net.codinux.log.stacktrace.StackTraceShortener;
import net.codinux.log.stacktrace.StackTraceShortenerOptions;
import io.quarkus.bootstrap.logging.QuarkusDelayedHandler;
import org.jboss.logmanager.ExtFormatter;
import org.jboss.logmanager.LogManager;
import org.jboss.logmanager.formatters.FormatStep;
import org.jboss.logmanager.formatters.FormatStep.ItemType;
import org.jboss.logmanager.formatters.Formatters;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.AsyncHandler;
import org.jboss.logmanager.handlers.ConsoleHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuarkusLogFormatterInitializer {

    public Handler initQuarkusLogFormatter() {
        return initQuarkusLogFormatter(new LogFormatterConfig());
    }

    public Handler initQuarkusLogFormatter(LogFormatterConfig config) {
        if (config.isDefault()) {
            return null; // no need to adjust ConsoleHandler, no values are set
        }

        try {
            Logger rootLogger = LogManager.getLogManager().getLogger("");

            // the first struggle is to find the ConsoleHandler which is wrapped in a QuarkusDelayedHandler and
            // in dev and test mode in another anonymous Handler and may in an AsyncHandler if
            // quarkus.log.console.async is set to true
            ConsoleHandler consoleHandler = findConsoleHandler(rootLogger);

            // the next struggle is to find the PatternFormatter, which may again is wrapped, e.g. if banner is activated
            PatternFormatter formatter = consoleHandler != null ? findPatternFormatter(consoleHandler) : null;

            if (formatter != null) {
                // and the last one is to find the field formatters like ExceptionFormatter - which again are anonymous classes
                patchFormatters(formatter, config);
            }

            return consoleHandler;
        } catch (Throwable e) {
            logError("Could not modify ConsoleHandler to format console log output", e);
            return null;
        }
    }

    private ConsoleHandler findConsoleHandler(Logger rootLogger) {
        for (Handler handler : rootLogger.getHandlers()) {
            if (handler instanceof QuarkusDelayedHandler) {
                for (Handler wrappedHandler : ((QuarkusDelayedHandler) handler).getHandlers()) {
                    ConsoleHandler found = findConsoleHandler(wrappedHandler);
                    if (found != null) {
                        return found;
                    }

                    try {
                        Class<?> wrappedHandlerClass = wrappedHandler.getClass();

                        // Quarkus creates ConsoleHandler in LoggingSetupRecorder.configureConsoleHandler() method and
                        // in dev and test mode wraps it in an anonymous Handler
                        Method enclosingMethod = wrappedHandlerClass.getEnclosingMethod();
                        if (enclosingMethod != null && enclosingMethod.getName().equals("configureConsoleHandler") &&
                                enclosingMethod.getDeclaringClass().getName().equals("io.quarkus.runtime.logging.LoggingSetupRecorder")) {
                            Field delegateField = getFirstFieldContainingOrNull(wrappedHandlerClass, "delegate");
                            if (delegateField != null) {
                                if (delegateField.trySetAccessible()) {
                                    Object delegate = delegateField.get(wrappedHandler);
                                    if (delegate instanceof ConsoleHandler) {
                                        return (ConsoleHandler) delegate;
                                    }
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logError("Could not find ConsoleHandler delegate created in LoggingSetupRecorder", e);
                    }
                }
            } else {
                ConsoleHandler found = findConsoleHandler(handler);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private ConsoleHandler findConsoleHandler(Handler handler) {
        if (handler instanceof ConsoleHandler) {
            return (ConsoleHandler) handler;
        } else if (handler instanceof AsyncHandler) {
            for (Handler inner : ((AsyncHandler) handler).getHandlers()) {
                ConsoleHandler found = findConsoleHandler(inner);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    private PatternFormatter findPatternFormatter(ConsoleHandler handler) {
        try {
            Formatter formatter = handler.getFormatter();
            if (formatter instanceof PatternFormatter) {
                return (PatternFormatter) formatter;
            } else if (formatter instanceof ExtFormatter.Delegating) { // e.g. TextBannerFormatter, which is used in case of activated banner, is derived from ExtFormatter.Delegating
                Field delegateField = getDeclaredFieldOrNull(ExtFormatter.Delegating.class, "delegate");

                if (delegateField != null) {
                    if (delegateField.trySetAccessible()) {
                        Object delegate = delegateField.get(formatter);
                        if (delegate instanceof PatternFormatter) {
                            return (PatternFormatter) delegate;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logError("Could not find PatternFormatter in ConsoleHandler", e);
        }
        return null;
    }

    private void patchFormatters(PatternFormatter formatter, LogFormatterConfig config) {
        // the step may again is wrapped, e.g. in a ColorPatternFormatter.ColorStep
        FormatStep[] originalSteps = formatter.getSteps();
        List<FormatStepInfo> stepInfos = IntStream.range(0, originalSteps.length)
                .mapToObj(i -> unwrapStep(originalSteps[i], i))
                .collect(Collectors.toList());

        for (FormatStepInfo stepInfo : stepInfos) {
            try {
                FormatStep toPatchWith = patchStepWith(stepInfo, config);
                if (toPatchWith != null) {
                    if (stepInfo.getWrappedStep() != null && stepInfo.getDelegateField() != null) {
                        stepInfo.getDelegateField().set(stepInfo.getStep(), toPatchWith);
                    } else {
                        originalSteps[stepInfo.getIndex()] = toPatchWith;
                        formatter.setSteps(originalSteps); // TODO: does it really work?
                    }
                }
            } catch (Throwable e) {
                logError("Could not patch step " + stepInfo.getType() + " " + stepInfo.getStep() + " at index " + stepInfo.getIndex(), e);
            }
        }
    }

    private FormatStep patchStepWith(FormatStepInfo stepInfo, LogFormatterConfig config) {
        if (stepInfo.getType() == ItemType.EXCEPTION_TRACE) {
            StackTraceFormatterOptions options = new StackTraceFormatterOptions.Builder()
                    .rootCauseFirst(config.isRootCauseFirst())
                    .maxStackTraceStringLength(config.getMaxStackTraceStringLength())
                    .build();
            StackTraceShortener shortener = new StackTraceShortener(new StackTraceShortenerOptions(
                    config.getMaxFramesPerThrowable(), config.getMaxNestedThrowables()));

            return new ExceptionFormatStep(new StackTraceFormatter(options, shortener), options.getLineSeparator());
        }

        return null;
    }

    private FormatStepInfo unwrapStep(FormatStep step, int index) {
        try {
            Field delegateField = getDeclaredFieldOrNull(step.getClass(), "delegate");
            if (delegateField != null) {
                if (delegateField.trySetAccessible()) {
                    Object delegate = delegateField.get(step);
                    if (delegate instanceof FormatStep) {
                        return new FormatStepInfo(step, getStepType((FormatStep) delegate), index, (FormatStep) delegate, delegateField);
                    }
                }
            }
        } catch (Throwable e) {
            logError("Could not unwrap delegate of step " + step + " at index " + index, e);
        }

        return new FormatStepInfo(step, getStepType(step), index, null, null);
    }

    private ItemType getStepType(FormatStep step) {
        try {
            Class stepClass = step.getClass();

            Method getItemTypeMethod = getDeclaredMethodOrNull(stepClass, "getItemType");
            if (getItemTypeMethod != null && getItemTypeMethod.getReturnType() == ItemType.class && getItemTypeMethod.getParameterCount() == 0) {
                getItemTypeMethod.trySetAccessible();
                return (ItemType) getItemTypeMethod.invoke(step);
            }

            Method enclosing = stepClass.getEnclosingMethod();
            if (enclosing != null && enclosing.getDeclaringClass() == Formatters.class) {
                if (enclosing.getName().equals("exceptionFormatStep")) {
                    return ItemType.EXCEPTION_TRACE;
                }
            }
        } catch (Throwable e) {
            logError("Could not get step type of " + step, e);
        }
        return null;
    }


    private void logError(String message, Throwable error) {
        Logger.getLogger(QuarkusLogFormatterInitializer.class.getName()).log(Level.SEVERE, message, error);
    }


    private Method getDeclaredMethodOrNull(Class<?> clazz, String name) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                return method;
            }
        }

        return null;
    }

    private Field getDeclaredFieldOrNull(Class<?> clazz, String name) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    private Field getFirstFieldContainingOrNull(Class<?> clazz, String substring) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().contains(substring)) {
                return field;
            }
        }

        return null;
    }

}