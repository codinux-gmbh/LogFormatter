package net.codinux.log.formatter.quarkus;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;
import io.quarkus.runtime.RuntimeValue;
import net.codinux.log.formatter.quarkus.config.QuarkusLogFormatterConfig;

import java.util.Optional;
import java.util.logging.Handler;

public class QuarkusLogFormatterSteps {

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    // LoggingSetupBuildItem is required so that this logic is run after log handlers have been fully set up
    public void setUpLogAppender(QuarkusLogFormatterRecorder recorder, QuarkusLogFormatterConfig config, LoggingSetupBuildItem loggingSetup) {
        RuntimeValue<Optional<Handler>> modifiedHandler = recorder.initializeLogFormatter(config);
    }


    @BuildStep
    ReflectiveClassBuildItem lokiLoggerClasses() { // register classes QuarkusLogFormatterInitializer introspects via reflection
        return ReflectiveClassBuildItem.builder(
                "org.jboss.logmanager.formatters.FormatStep",
                QuarkusLogFormatterInitializer.ExtFormatterDelegatingClassName
        )
        .fields(true)
        .methods(true)
        .build();
    }

}