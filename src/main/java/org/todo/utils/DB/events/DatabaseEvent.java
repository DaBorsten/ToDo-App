package org.todo.utils.DB.events;

public class DatabaseEvent {
    public enum Operation {
        INSERT,
        UPDATE,
        DELETE
    }

    public enum Table {
        TASKS,
        TAGS,
        TASK_TAGS,
        SORT_CRITERIA
    }

    private final Operation operation;
    private final Table table;
    private final String entityId;

    public DatabaseEvent(Operation operation, Table table, String entityId) {
        this.operation = operation;
        this.table = table;
        this.entityId = entityId;
    }

    public Operation getOperation() {
        return operation;
    }

    public Table getTable() {
        return table;
    }

    public String getEntityId() {
        return entityId;
    }
}