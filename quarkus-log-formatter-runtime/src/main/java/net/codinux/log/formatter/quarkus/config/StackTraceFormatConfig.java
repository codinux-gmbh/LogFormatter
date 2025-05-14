package net.codinux.log.formatter.quarkus.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import java.util.Optional;

@ConfigGroup
public interface StackTraceFormatConfig {

    /**
     * If root cause - the most inner cause of a Throwable, in most cases the one you
     * actually want to see - should be printed first and Throwable hierachy therefore inverted.
     */
    @WithDefault("false")
    boolean rootCauseFirst();

    /**
     * How many stack frames per Throwable should be printed.
     */
    Optional<Integer> maxFramesPerThrowable();

    /**
     * How many nested Throwables should be printed. {@code null} or a value less than
     * 0 mean: print all Throwables.
     */
    Optional<Integer> maxNestedThrowables();

    /**
     * If stack trace should be cropped at a maximum string length.
     */
    @WithName("max-string-length")
    Optional<Integer> maxStackTraceStringLength();

}