package net.codinux.log.formatter.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.log.console")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface QuarkusLogFormatterConfig {

    /**
     * Options to configure the stacktrace format.
     */
    @WithName("stacktrace")
    StackTraceFormatConfig stackTraceFormat();

}