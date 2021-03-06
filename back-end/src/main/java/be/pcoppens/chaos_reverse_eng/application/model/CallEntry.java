package be.pcoppens.chaos_reverse_eng.application.model;

import java.util.Objects;

/**
 * Represent a Call entry {source -> target}
 */
public class CallEntry {
    private EndPointEntry source;
    private EndPointEntry target;

    public CallEntry(EndPointEntry source, EndPointEntry target) {
        this.source = source;
        this.target = target;
    }

    public EndPointEntry getSource() {
        return source;
    }

    public EndPointEntry getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallEntry callEntry = (CallEntry) o;
        return Objects.equals(getSource(), callEntry.getSource()) &&
                Objects.equals(getTarget(), callEntry.getTarget());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getSource(), getTarget());
    }

    @Override
    public String toString() {
        return "CallEntry{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }
}
