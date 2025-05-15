package net.codinux.log.formatter.quarkus;

import net.codinux.log.stacktrace.StackTraceFormatter;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.formatters.FormatStep;

public class ExceptionFormatStep implements FormatStep {

    protected final StackTraceFormatter formatter;
    protected final String lineSeparator; // should be set to the same value as StackTraceFormatter.options.lineSeparator

    public ExceptionFormatStep() {
        this(new StackTraceFormatter(), System.lineSeparator());
    }

    public ExceptionFormatStep(StackTraceFormatter formatter, String lineSeparator) {
        this.formatter = formatter;
        this.lineSeparator = lineSeparator;
    }

    @Override
    public void render(StringBuilder builder, ExtLogRecord record) {
        Throwable thrown = record.getThrown();
        if (thrown != null) {
            builder.append(lineSeparator);
            builder.append(formatter.format(thrown));
            builder.append(lineSeparator);
        }
    }

    @Override
    public int estimateLength() {
        return 500; // don't know what to return; original code returns 32 which is way more inaccurate
    }
}
