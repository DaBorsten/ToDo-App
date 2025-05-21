package org.todo.classes;

public class SortCriterion {
    private String criterion;
    private boolean ascending;

    public SortCriterion(String criterion, boolean ascending) {
        this.criterion = criterion;
        this.ascending = ascending;
    }

    public String getCriterion() {
        return criterion;
    }

    public boolean isAscending() {
        return ascending;
    }

    @Override
    public String toString() {
        return (ascending ? "▲ " : "▼ ") + criterion;
    }
}