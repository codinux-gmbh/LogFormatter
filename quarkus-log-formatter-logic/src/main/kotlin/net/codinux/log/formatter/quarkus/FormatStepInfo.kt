package net.codinux.log.formatter.quarkus

import org.jboss.logmanager.formatters.FormatStep
import java.lang.reflect.Field

internal data class FormatStepInfo(
    val step: FormatStep,
    val type: FormatStep.ItemType?,
    val index: Int,
    val wrappedStep: FormatStep? = null,
    val delegateField: Field? = null
) {
    override fun toString() = "${type ?: "unknown"}${if (wrappedStep != null) " Wrapped in" else ""} $step"
}