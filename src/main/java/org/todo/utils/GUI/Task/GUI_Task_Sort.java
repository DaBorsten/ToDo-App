package org.todo.utils.GUI.Task;

import org.todo.classes.SortCriterion;
import org.todo.classes.Task;

import java.util.*;
import java.util.stream.Collectors;

public class GUI_Task_Sort {

    public static List<Task> sortTasks(List<Task> tasks, List<SortCriterion> sortCriteria) {
        if (sortCriteria == null || sortCriteria.isEmpty()) return tasks;

        return tasks.stream()
            .sorted((task1, task2) -> {
                for (SortCriterion criterion : sortCriteria) {
                    int comparison = compareBycriterion(task1, task2, criterion);
                    if (comparison != 0) {
                        return criterion.isAscending() ? comparison : -comparison;
                    }
                }
                return 0;
            })
            .collect(Collectors.toList());
    }

    private static int compareBycriterion(Task task1, Task task2, SortCriterion criterion) {
        return switch (criterion.getCriterion().toLowerCase()) {
            case "titel" -> task1.getTitle().compareToIgnoreCase(task2.getTitle());
            case "datum" -> task1.getDueDate().compareTo(task2.getDueDate());
            case "priorität" -> task1.getPriority().compareTo(task2.getPriority());
            default -> task1.getTitle().compareToIgnoreCase(task2.getTitle());
        };
    }
}
