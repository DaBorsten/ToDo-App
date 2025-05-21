package org.todo.utils.DB;

import org.todo.classes.Task;
import org.todo.classes.Tag;
import org.todo.classes.SortCriterion;
import org.todo.config.Config;
import org.todo.utils.DB.events.DatabaseEvent;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseOperations implements DatabaseOperations {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void addTask(String id, String title, String description, String notes,
                        LocalDateTime dueDate, boolean completed, boolean favorite, String priority) {
        String formattedDueDate = dueDate.format(formatter);

        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO tasks (id, title, description, notes, due_date, completed, favorite, priority) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, notes);
            pstmt.setString(5, formattedDueDate);
            pstmt.setBoolean(6, completed);
            pstmt.setBoolean(7, favorite);
            pstmt.setString(8, priority);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.INSERT, DatabaseEvent.Table.TASKS, id)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTask(String taskId) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
            pstmt.setString(1, taskId);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.DELETE, DatabaseEvent.Table.TASKS, taskId)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String dueDate = rs.getString("due_date");
                boolean completed = rs.getBoolean("completed");
                boolean favorite = rs.getBoolean("favorite");
                String notes = rs.getString("notes");
                String priority = rs.getString("priority");

                LocalDateTime newDueDate = LocalDateTime.parse(dueDate, formatter);
                Task task = new Task(id, title, description, newDueDate, completed, favorite, notes, priority);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public void updateTask(String taskId, String title, String description, LocalDateTime dueDate, boolean completed, boolean favorite, String notes, String priority) {
        String formattedDueDate = dueDate.format(formatter);

        String completedAt = completed ? LocalDateTime.now().format(formatter) : null;

        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE tasks SET title = ?, description = ?, due_date = ?, completed = ?, favorite = ?, completed_at = ?, notes = ?, priority = ? WHERE id = ?")) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, formattedDueDate);
            pstmt.setBoolean(4, completed);
            pstmt.setBoolean(5, favorite);
            pstmt.setString(6, completedAt);
            pstmt.setString(7, notes);
            pstmt.setString(8, priority);
            pstmt.setString(9, taskId);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.UPDATE, DatabaseEvent.Table.TASKS, taskId)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTag(String id, String title, int color) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tags (id, title, color) VALUES (?, ?, ?)")) {
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setInt(3, color);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.INSERT, DatabaseEvent.Table.TAGS, id)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTag(String id, String title, int color) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE tags SET title = ?, color = ? WHERE id = ?")) {
            pstmt.setString(1, title);
            pstmt.setInt(2, color);
            pstmt.setString(3, id);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.UPDATE, DatabaseEvent.Table.TAGS, id)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTag(String id) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tags WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.DELETE, DatabaseEvent.Table.TAGS, id)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tags")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                int color = rs.getInt("color");
                tags.add(new Tag(id, title, color));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    @Override
    public List<Tag> getTagsForTask(String taskId) {
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.* FROM tags t " +
                             "JOIN task_tags tt ON t.id = tt.tag_id " +
                             "WHERE tt.task_id = ?")) {

            pstmt.setString(1, taskId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                int color = rs.getInt("color");
                tags.add(new Tag(id, title, color));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    @Override
    public void updateTagsForTask(String taskId, List<Tag> tags) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL)) {
            removeExistingTagsFromTask(taskId);

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO task_tags (task_id, tag_id) VALUES (?, ?)")) {
                for (Tag tag : tags) {
                    pstmt.setString(1, taskId);
                    pstmt.setString(2, tag.getId());
                    pstmt.executeUpdate();
                }
            }

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(
                            DatabaseEvent.Operation.UPDATE,
                            DatabaseEvent.Table.TASK_TAGS,
                            taskId
                    )
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeExistingTagsFromTask(String taskId) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM task_tags WHERE task_id = ?")) {

            pstmt.setString(1, taskId);
            pstmt.executeUpdate();

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(
                            DatabaseEvent.Operation.DELETE,
                            DatabaseEvent.Table.TASK_TAGS,
                            taskId
                    )
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SortCriterion> loadSortCriteria() {
        List<SortCriterion> criteria = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM SortCriteria ORDER BY id")) {

            while (rs.next()) {
                String criterion = rs.getString("criterion");
                boolean ascending = rs.getInt("ascending") == 1;
                criteria.add(new SortCriterion(criterion, ascending));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return criteria;
    }

    @Override
    public void saveSortCriteria(List<SortCriterion> criteria) {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM SortCriteria");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO SortCriteria (criterion, ascending) VALUES (?, ?)")) {
                for (SortCriterion sc : criteria) {
                    pstmt.setString(1, sc.getCriterion());
                    pstmt.setInt(2, sc.isAscending() ? 1 : 0);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            DatabaseObserver.getInstance().notifyListeners(
                    new DatabaseEvent(DatabaseEvent.Operation.UPDATE, DatabaseEvent.Table.SORT_CRITERIA, "")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                    id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    description TEXT,
                    due_date TEXT NOT NULL,
                    completed BOOLEAN DEFAULT 0,
                    favorite BOOLEAN DEFAULT 0,
                    notes TEXT,
                    priority TEXT CHECK(priority IN ('Niedrig', 'Mittel', 'Hoch')),
                    created_at TEXT DEFAULT (REPLACE(DATETIME('now', 'localtime'), ' ', 'T')),
                    completed_at TEXT
                    )""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tags (
                        id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        color INT NOT NULL
                    )""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS task_tags (
                        task_id TEXT,
                        tag_id TEXT,
                        FOREIGN KEY (task_id) REFERENCES tasks(id),
                        FOREIGN KEY (tag_id) REFERENCES tags(id),
                        PRIMARY KEY (task_id, tag_id)
                    )""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS SortCriteria (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        criterion TEXT NOT NULL,
                        ascending INTEGER NOT NULL
                    )""");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}