package org.todo.screens;

import org.todo.classes.Task;
import org.todo.utils.GUI.General.GUI_General_Chart;
import org.todo.utils.GUI.General.GUI_General_Messages;
import org.todo.utils.DB.events.DatabaseEvent;
import org.todo.utils.DB.DatabaseObserver;
import org.todo.utils.DB.DatabaseOperations;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatisticsScreen implements Screen {
    private List<Task> tasks;
    private JPanel contentPanel;

    private final DatabaseOperations dbOperations;

    public StatisticsScreen(DatabaseOperations dbOperations) {
        this.dbOperations = dbOperations;
        initializeGUI();
        setupDatabaseListener();
    }

    private void setupDatabaseListener() {
        DatabaseObserver.getInstance().addListener(event -> {
            if (event.getTable() == DatabaseEvent.Table.TASKS ||
                event.getTable() == DatabaseEvent.Table.TASK_TAGS) {
                SwingUtilities.invokeLater(() -> {
                    tasks = dbOperations.getAllTasks();
                    showContent(tasks);
                });
            }
        });
    }

    @Override
    public JPanel getPanel() {
        return contentPanel;
    }

    private void initializeGUI() {
        tasks = dbOperations.getAllTasks();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();

        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toolBar.setBackground(Color.decode("#282828"));

        JLabel heading = new JLabel("Statistik - Status von ToDo's");
        heading.setFont(heading.getFont().deriveFont(18f));
        heading.setPreferredSize(new Dimension(300, 42));

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(heading);
        toolBar.add(Box.createHorizontalGlue());

        JPanel taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(320, 400));

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(toolBar, BorderLayout.NORTH);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        GUI_General_Chart.contentPanel = taskListPanel;

        showContent(tasks);
    }

    private void showContent(List<Task> todoTasks) {
        if (!todoTasks.isEmpty()) {
            GUI_General_Chart.displayPieChart(todoTasks);
        } else {
            GUI_General_Messages.displayNoContentMessage("Keine ToDo's vorhanden");
        }
    }
}
