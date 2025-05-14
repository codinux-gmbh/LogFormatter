package net.codinux.log.formatter.quarkus;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.codinux.log.formatter.quarkus.config.LogFormatterConfig;
import net.codinux.log.formatter.quarkus.config.QuarkusLogFormatterConfig;
import net.codinux.log.formatter.quarkus.config.StackTraceFormatConfig;

import java.util.Optional;
import java.util.logging.Handler;

@Recorder
public class QuarkusLogFormatterRecorder {

    public RuntimeValue<Optional<Handler>> initializeLogFormatter(QuarkusLogFormatterConfig config) {
        StackTraceFormatConfig stackTrace = config.stackTraceFormat();
        LogFormatterConfig mappedConfig = new LogFormatterConfig(stackTrace.rootCauseFirst(),
                stackTrace.maxFramesPerThrowable().orElse(null), stackTrace.maxNestedThrowables().orElse(null),
                stackTrace.maxStackTraceStringLength().orElse(null));

        Handler modifiedHandler = new QuarkusLogFormatterInitializer().initQuarkusLogFormatter(mappedConfig);
        return new RuntimeValue(Optional.ofNullable(modifiedHandler));
    }

}