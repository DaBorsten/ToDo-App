package org.todo.classes;

import java.time.LocalDateTime;

public class Task {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private boolean favorite;
    private String notes;
    private String priority;

    public Task(String id, String title, String description, LocalDateTime due_Date, boolean completed, boolean favorite, String notes, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = due_Date;
        this.completed = completed;
        this.favorite = favorite;
        this.notes = notes;
        this.priority = priority;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPriority() {
        return priority;
    }

    public void setNPriority(String priority) {
        this.priority = priority;
    }


    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    public void toggleFavorite() {
        this.favorite = !this.favorite;
    }

    public boolean isOverdue() {
        return !completed && LocalDateTime.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", completed=" + completed +
                ", favorite=" + favorite +
                '}';
    }
}


