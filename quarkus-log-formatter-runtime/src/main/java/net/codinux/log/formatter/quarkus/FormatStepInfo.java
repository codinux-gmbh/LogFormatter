package net.codinux.log.formatter.quarkus;

import org.jboss.logmanager.formatters.FormatStep;

import java.lang.reflect.Field;
import java.util.Objects;

class FormatStepInfo {

    private final FormatStep step;
    private final FormatStep.ItemType type;
    private final int index;
    private final FormatStep wrappedStep;
    private final Field delegateField;

    public FormatStepInfo(FormatStep step, FormatStep.ItemType type, int index,
                          FormatStep wrappedStep, Field delegateField) {
        this.step = step;
        this.type = type;
        this.index = index;
        this.wrappedStep = wrappedStep;
        this.delegateField = delegateField;
    }

    public FormatStep getStep() {
        return step;
    }

    public FormatStep.ItemType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public FormatStep getWrappedStep() {
        return wrappedStep;
    }

    public Field getDelegateField() {
        return delegateField;
    }

    @Override
    public String toString() {
        return (type != null ? type : "unknown") +
                (wrappedStep != null ? " Wrapped in" : "") +
                " " + step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormatStepInfo)) return false;
        FormatStepInfo that = (FormatStepInfo) o;
        return index == that.index &&
                Objects.equals(step, that.step) &&
                type == that.type &&
                Objects.equals(wrappedStep, that.wrappedStep) &&
                Objects.equals(delegateField, that.delegateField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(step, type, index, wrappedStep, delegateField);
    }
}
