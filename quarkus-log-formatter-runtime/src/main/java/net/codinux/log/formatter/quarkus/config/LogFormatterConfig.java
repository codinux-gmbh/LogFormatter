package net.codinux.log.formatter.quarkus.config;

import java.util.Objects;

public class LogFormatterConfig {

    private final boolean rootCauseFirst;

    private final Integer maxFramesPerThrowable;
    private final Integer maxNestedThrowables;

    private final Integer maxStackTraceStringLength;


    public LogFormatterConfig() {
        this(false, null, null, null);
    }

    public LogFormatterConfig(
            boolean rootCauseFirst,
            Integer maxFramesPerThrowable,
            Integer maxNestedThrowables,
            Integer maxStackTraceStringLength
    ) {
        this.rootCauseFirst = rootCauseFirst;
        this.maxFramesPerThrowable = maxFramesPerThrowable;
        this.maxNestedThrowables = maxNestedThrowables;
        this.maxStackTraceStringLength = maxStackTraceStringLength;
    }


    public boolean isRootCauseFirst() {
        return rootCauseFirst;
    }

    public Integer getMaxFramesPerThrowable() {
        return maxFramesPerThrowable;
    }

    public Integer getMaxNestedThrowables() {
        return maxNestedThrowables;
    }

    public Integer getMaxStackTraceStringLength() {
        return maxStackTraceStringLength;
    }

    public boolean isDefault() {
        return rootCauseFirst == false
                && maxFramesPerThrowable == null
                && maxNestedThrowables == null
                && maxStackTraceStringLength == null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogFormatterConfig)) return false;
        LogFormatterConfig that = (LogFormatterConfig) o;
        return rootCauseFirst == that.rootCauseFirst &&
                Objects.equals(maxFramesPerThrowable, that.maxFramesPerThrowable) &&
                Objects.equals(maxNestedThrowables, that.maxNestedThrowables) &&
                Objects.equals(maxStackTraceStringLength, that.maxStackTraceStringLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootCauseFirst, maxFramesPerThrowable, maxNestedThrowables, maxStackTraceStringLength);
    }

    @Override
    public String toString() {
        return "LogFormatterConfig{" +
                "rootCauseFirst=" + rootCauseFirst +
                ", maxFramesPerThrowable=" + maxFramesPerThrowable +
                ", maxNestedThrowables=" + maxNestedThrowables +
                ", maxStackTraceStringLength=" + maxStackTraceStringLength +
                '}';
    }

}