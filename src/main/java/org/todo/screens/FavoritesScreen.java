package org.todo.screens;

import org.todo.classes.FilterCriterion;
import org.todo.classes.SortCriterion;
import org.todo.classes.Task;
import org.todo.components.CIconButton;
import org.todo.components.CSortDialog;
import org.todo.utils.DB.events.DatabaseEvent;
import org.todo.utils.DB.DatabaseObserver;
import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.General.GUI_General_Chart;
import org.todo.utils.GUI.General.GUI_General_Messages;
import org.todo.utils.GUI.General.GUI_General_Show_Content;
import org.todo.utils.GUI.Task.GUI_Task_List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

import static org.todo.components.GuiComponents.frame;

public class FavoritesScreen implements Screen {

    private JPanel contentPanel;
    private JPanel taskListPanel;
    private final CIconButton cIconButton = new CIconButton();

    private List<Task> tasks;
    protected List<SortCriterion> sortCriterion;
    protected JTextField searchField;

    private final DatabaseOperations dbOperations;

    public FavoritesScreen(DatabaseOperations dbOperations) {
        this.dbOperations = dbOperations;
        initializeGUI();
        setupDatabaseListener();
        setupCommonKeyBindings(contentPanel, searchField);
    }

    private void setupDatabaseListener() {
        DatabaseObserver.getInstance().addListener(event -> {
            if (event.getTable() == DatabaseEvent.Table.TASKS ||
                event.getTable() == DatabaseEvent.Table.TASK_TAGS ||
                event.getTable() == DatabaseEvent.Table.SORT_CRITERIA) {
                SwingUtilities.invokeLater(this::updateTasks);
            }
        });
    }

    @Override
    public JPanel getPanel() {
        return contentPanel;
    }

    private void initializeGUI() {

        sortCriterion = dbOperations.loadSortCriteria();

        tasks = dbOperations.getAllTasks();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();

        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toolBar.setBackground(Color.decode("#282828"));

        JButton sortButton = cIconButton.createButton("icons/png/swap-vertical-outline.png");

        JLabel searchLabel = new JLabel("Suchen:");
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(10);
        searchField.setPreferredSize(new Dimension(150, 42));
        searchField.setMaximumSize(new Dimension(300, 42));
        searchField.setToolTipText("Suche nach Titel oder Beschreibung (Strg + F)");
        searchField.setColumns(50);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTasks();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTasks();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTasks();
            }
        });

        sortButton.addActionListener(e -> {
            CSortDialog sortDialog = new CSortDialog(frame, dbOperations);
            sortDialog.showSortDialog();
        });

        toolBar.add(searchLabel);
        toolBar.add(Box.createHorizontalStrut(8));
        toolBar.add(searchField);

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(Box.createHorizontalStrut(20));

        toolBar.add(sortButton);

        contentPanel.add(toolBar, BorderLayout.NORTH);

        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(320, 400));

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        GUI_General_Chart.contentPanel = taskListPanel;
        GUI_Task_List.contentPanel = taskListPanel;
        GUI_General_Messages.contentPanel = taskListPanel;

        renderContent(tasks);
    }

    private void renderContent(List<Task> tasks) {
        GUI_General_Show_Content.showContent(tasks, searchField.getText(), sortCriterion, List.of(new FilterCriterion(FilterCriterion.Criterion.FAVORITE, false)), "Keine Favoriten vorhanden", "Keine Favoriten gefunden", dbOperations);
    }

    public void updateTasks() {
        this.tasks = dbOperations.getAllTasks();
        this.sortCriterion = dbOperations.loadSortCriteria();
        renderContent(tasks);
    }
}
