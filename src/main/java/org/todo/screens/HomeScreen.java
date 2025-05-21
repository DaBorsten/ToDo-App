package org.todo.screens;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.todo.classes.*;
import org.todo.components.*;
import org.todo.utils.DB.*;
import org.todo.utils.DB.events.DatabaseEvent;
import org.todo.utils.GUI.General.GUI_General_Chart;
import org.todo.utils.GUI.General.GUI_General_Messages;
import org.todo.utils.GUI.General.GUI_General_Show_Content;
import org.todo.utils.GUI.Task.GUI_Task_Add;
import org.todo.utils.GUI.Task.GUI_Task_List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.Vector;

import static org.todo.components.GuiComponents.frame;

public class HomeScreen implements Screen {

    private JPanel contentPanel;
    private JPanel taskListPanel;
    private final CButton cButton = new CButton();
    private final CIconButton cIconButton = new CIconButton();

    private List<Task> tasks;
    protected JTextField searchField;
    protected List<SortCriterion> sortCriterion;

    private final DatabaseOperations dbOperations;

    public HomeScreen(DatabaseOperations dbOperations) {
        this.dbOperations = dbOperations;
        initializeGUI();
        setupDatabaseListener();
        setupTaskKeyBinding();
        setupCommonKeyBindings(contentPanel, searchField);
    }

    private void setupTaskKeyBinding() {
        Action addTaskAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI_Task_Add.openAddTaskDialog(dbOperations);
                List<Task> updatedTasks = dbOperations.getAllTasks();
                renderContent(updatedTasks);
            }
        };

        contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "addTask");
        contentPanel.getActionMap().put("addTask", addTaskAction);
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
        FlatMacDarkLaf.setup();

        sortCriterion = dbOperations.loadSortCriteria();

        tasks = dbOperations.getAllTasks();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFocusable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toolBar.setBackground(Color.decode("#282828"));

        JButton sortButton = cIconButton.createButton("icons/png/swap-vertical-outline.png");
        sortButton.setFocusable(true);
        sortButton.setFocusCycleRoot(false);
        sortButton.getAccessibleContext().setAccessibleName("Sortierung");
        sortButton.getAccessibleContext().setAccessibleDescription("Öffnet Dialog zum Ändern der Sortierung");

        JButton addButton = cButton.createButton("Aufgabe hinzufügen",
                "icons/png/add-circle-outline.png");
        addButton.setFocusable(true);
        addButton.setFocusCycleRoot(false);
        addButton.getAccessibleContext().setAccessibleDescription("Aufgabe hinzufügen");
        addButton.setToolTipText("Aufgabe hinzufügen");

        JLabel searchLabel = new JLabel("Suchen:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setLabelFor(searchField);

        searchField = new JTextField(10);
        searchField.setFocusable(true);
        searchField.setFocusCycleRoot(false);
        searchField.setPreferredSize(new Dimension(150, 42));
        searchField.setMaximumSize(new Dimension(300, 42));
        searchField.setToolTipText("Suche nach Titel oder Beschreibung (Strg + F)");
        searchField.setColumns(50);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                renderContent(tasks);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                renderContent(tasks);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                renderContent(tasks);
            }
        });

        sortButton.addActionListener(e -> {
            CSortDialog sortDialog = new CSortDialog(frame, dbOperations);
            sortDialog.showSortDialog();
        });

        addButton.addActionListener(e -> {
            GUI_Task_Add.openAddTaskDialog(dbOperations);
            List<Task> updatedTasks = dbOperations.getAllTasks();
            renderContent(updatedTasks);
        });

        toolBar.add(searchLabel);
        toolBar.add(Box.createHorizontalStrut(8));
        toolBar.add(searchField);

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(Box.createHorizontalStrut(20));

        toolBar.add(sortButton);
        toolBar.add(addButton);

        Vector<Component> order = new Vector<>();
        order.add(searchField);
        order.add(sortButton);
        order.add(addButton);

        FocusTraversalPolicy focusPolicy = new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container container, Component component) {
                int idx = order.indexOf(component);
                return order.get((idx + 1) % order.size());
            }

            @Override
            public Component getComponentBefore(Container container, Component component) {
                int idx = order.indexOf(component);
                return order.get((idx - 1 + order.size()) % order.size());
            }

            @Override
            public Component getFirstComponent(Container container) {
                return order.get(0);
            }

            @Override
            public Component getLastComponent(Container container) {
                return order.get(order.size() - 1);
            }

            @Override
            public Component getDefaultComponent(Container container) {
                return order.get(0);
            }
        };

        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BorderLayout());
        toolbarPanel.add(toolBar, BorderLayout.CENTER);
        toolbarPanel.setFocusTraversalPolicy(focusPolicy);
        toolbarPanel.setFocusCycleRoot(true);
        contentPanel.setFocusTraversalPolicy(focusPolicy);
        contentPanel.setFocusCycleRoot(true);

        contentPanel.add(toolbarPanel, BorderLayout.NORTH);

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

        Vector<Component> contentPanelOrder = new Vector<>();
        contentPanelOrder.add(searchField);
        contentPanelOrder.add(sortButton);

        contentPanel.setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container container, Component component) {
                int idx = contentPanelOrder.indexOf(component);
                return contentPanelOrder.get((idx + 1) % contentPanelOrder.size());
            }

            @Override
            public Component getComponentBefore(Container container, Component component) {
                int idx = contentPanelOrder.indexOf(component);
                return contentPanelOrder.get((idx - 1 + contentPanelOrder.size()) % contentPanelOrder.size());
            }

            @Override
            public Component getFirstComponent(Container container) {
                return contentPanelOrder.get(0);
            }

            @Override
            public Component getLastComponent(Container container) {
                return contentPanelOrder.get(contentPanelOrder.size() - 1);
            }

            @Override
            public Component getDefaultComponent(Container container) {
                return contentPanelOrder.get(0);
            }
        });
    }

    private void renderContent(List<Task> tasks) {
        GUI_General_Show_Content.showContent(tasks, searchField.getText(), sortCriterion,
                List.of(new FilterCriterion(FilterCriterion.Criterion.DONE, true)), "Keine Aufgaben vorhanden",
                "Keine Aufgaben gefunden", dbOperations);
    }

    public void updateTasks() {
        this.tasks = dbOperations.getAllTasks();
        this.sortCriterion = dbOperations.loadSortCriteria();
        renderContent(tasks);
    }
}
