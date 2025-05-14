package net.codinux.log.formatter.quarkus;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.codinux.log.formatter.quarkus.config.QuarkusLogFormatterConfig;

import java.util.Optional;
import java.util.logging.Handler;

@Recorder
public class QuarkusLogFormatterRecorder {

    public RuntimeValue<Optional<Handler>> initializeLogFormatter(QuarkusLogFormatterConfig config) {
        return new RuntimeValue(Optional.empty());
    }

}