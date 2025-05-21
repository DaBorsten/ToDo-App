package org.todo.utils.GUI.Task;

import javax.swing.*;

import org.todo.classes.FilterCriterion;
import org.todo.classes.SortCriterion;
import org.todo.classes.Task;
import org.todo.components.*;
import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.General.GUI_General_Messages;

import java.awt.*;
import java.util.List;

public class GUI_Task_List {
    public static JPanel contentPanel = new JPanel();


    public static void displayTaskList(List<Task> tasks, String searchText, List<SortCriterion> sortCriteria, List<FilterCriterion> filterCriteria, String noContentMessage, String noContentFoundMessage, DatabaseOperations dbOperations) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        List<Task> processedTasks = tasks;
        for (FilterCriterion filter : filterCriteria) {
            processedTasks = GUI_Task_Filter.filterTasks(processedTasks, filter);
        }

        if (processedTasks.isEmpty()) {
            GUI_General_Messages.displayNoContentMessage(noContentMessage);
            return;
        }

        processedTasks = GUI_Task_Search.searchTasks(processedTasks, searchText);

        if (processedTasks.isEmpty()) {
            GUI_General_Messages.displayNoContentFoundMessage(noContentFoundMessage);
            return;
        }

        processedTasks = GUI_Task_Sort.sortTasks(processedTasks, sortCriteria);

        for (Task task : processedTasks) {
            CTaskListItem item = new CTaskListItem(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate(),
                    task.isCompleted(),
                    task.isFavorite(),
                    task.getNotes(),
                    task.getPriority(),
                    dbOperations
            );
            contentPanel.add(item);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
