package org.todo.utils.GUI.Task;

import org.todo.classes.Task;

import java.util.List;
import java.util.stream.Collectors;

public class GUI_Task_Search {

    public static List<Task> searchTasks(List<Task> tasks, String searchText) {
        if (searchText == null || searchText.isEmpty()) return tasks;

        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                        task.getDueDate().toString().contains(searchText))
                .collect(Collectors.toList());
    }

}
