package net.codinux.log.formatter.fields

import net.codinux.log.LogEvent
import net.codinux.log.classname.ClassNameAbbreviator
import net.codinux.log.classname.ClassNameAbbreviatorOptions

open class LoggerNameFormatter(
    format: FieldFormat? = null,
    options: String? = null,
    protected open val abbreviator: ClassNameAbbreviator = ClassNameAbbreviator.Default,
    protected open val abbreviatorOptions: ClassNameAbbreviatorOptions = ClassNameAbbreviatorOptions.Logback
) : FieldFormatter(format, options) {

    override fun getField(event: LogEvent): String {
        val length = firstOptionAsInt

        return if (length == null || length < 0) {
            event.loggerName
        } else {
            abbreviator.abbreviate(event.loggerName, length, abbreviatorOptions)
        }
    }

}