package org.todo.utils.GUI.Task;

import org.todo.classes.FilterCriterion;
import org.todo.classes.Task;

import java.util.List;
import java.util.stream.Collectors;

public class GUI_Task_Filter {

    public static List<Task> filterTasks(List<Task> tasks, FilterCriterion filterCriterion) {
        if (filterCriterion == null) return tasks;

        return tasks.stream()
                .filter(filterCriterion::matches)
                .collect(Collectors.toList());
    }
}
