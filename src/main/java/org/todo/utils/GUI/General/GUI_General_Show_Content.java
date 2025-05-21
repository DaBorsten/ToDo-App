package org.todo.utils.GUI.General;

import org.todo.classes.FilterCriterion;
import org.todo.classes.SortCriterion;
import org.todo.classes.Task;
import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.Task.GUI_Task_List;

import java.util.List;

public class GUI_General_Show_Content {
    public static void showContent(List<Task> tasks, String searchText, List<SortCriterion> sortCriterions, List<FilterCriterion> filterCriterions, String noContentMessage, String noContentFoundMessage, DatabaseOperations dbOperations) {
        if (!tasks.isEmpty()) {
            GUI_Task_List.displayTaskList(tasks, searchText, sortCriterions, filterCriterions, noContentMessage, noContentFoundMessage, dbOperations);
        } else {
            GUI_General_Messages.displayNoContentMessage(noContentMessage);
        }
    }
}
