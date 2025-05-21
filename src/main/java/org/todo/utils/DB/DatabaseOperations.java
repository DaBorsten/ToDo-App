package org.todo.utils.DB;

import org.todo.classes.Task;
import org.todo.classes.Tag;
import org.todo.classes.SortCriterion;

import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseOperations {
    void addTask(String id, String title, String description, String notes, 
                LocalDateTime dueDate, boolean completed, boolean favorite, String priority);
    void deleteTask(String taskId);
    List<Task> getAllTasks();
    void updateTask(String taskId, String title, String description, LocalDateTime dueDate, boolean completed, boolean favorite, String notes, String priority);

    void addTag(String id, String title, int color);
    void updateTag(String id, String title, int color);
    void deleteTag(String id);
    List<Tag> getAllTags();
    List<Tag> getTagsForTask(String taskId);
    void updateTagsForTask(String taskId, List<Tag> tags);
    void removeExistingTagsFromTask(String taskId);

    List<SortCriterion> loadSortCriteria();
    void saveSortCriteria(List<SortCriterion> criteria);

    void initializeDatabase();
}