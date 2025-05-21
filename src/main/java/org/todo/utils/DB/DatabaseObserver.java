package org.todo.utils.DB;

import org.todo.utils.DB.events.DatabaseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseObserver {
    private static final DatabaseObserver instance = new DatabaseObserver();
    private final List<Consumer<DatabaseEvent>> listeners = new ArrayList<>();

    private DatabaseObserver() {}

    public static DatabaseObserver getInstance() {
        return instance;
    }

    public void addListener(Consumer<DatabaseEvent> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<DatabaseEvent> listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(DatabaseEvent event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}