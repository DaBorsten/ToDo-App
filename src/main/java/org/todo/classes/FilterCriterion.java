package org.todo.classes;

public class FilterCriterion {
    public enum Criterion {
        DONE, FAVORITE
    }

    private Criterion criterion;
    private boolean negated;

    public FilterCriterion(Criterion criterion, boolean negated) {
        this.criterion = criterion;
        this.negated = negated;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public boolean isNegated() {
        return negated;
    }

    @Override
    public String toString() {
        return (negated ? "!" : "") + criterion.name().toLowerCase();
    }

    public boolean matches(Task task) {
        boolean matches = false;
        switch (criterion) {
            case DONE:
                matches = task.isCompleted();
                break;
            case FAVORITE:
                matches = task.isFavorite();
                break;
        }
        return negated ? !matches : matches;
    }
}
