package net.codinux.log.formatter.quarkus

import net.codinux.log.stacktrace.StackTraceFormatter
import org.jboss.logmanager.ExtLogRecord
import org.jboss.logmanager.formatters.FormatStep
import java.lang.StringBuilder

open class ExceptionFormatStep(
    protected val formatter: StackTraceFormatter = StackTraceFormatter(),
    protected val lineSeparator: String = System.lineSeparator(), // should be set to the same value as StackTraceFormatter.options.lineSeparator
) : FormatStep {

    override fun render(builder: StringBuilder, record: ExtLogRecord) {
        record.thrown?.let { thrown ->
            builder.append(lineSeparator)

            builder.append(formatter.format(thrown))

            builder.append(lineSeparator)
        }
    }

    override fun estimateLength(): Int = 500 // don't know what to return; original code returns 32 which is way more inaccurate

}