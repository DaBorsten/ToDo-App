package org.todo.screens;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.todo.classes.Tag;
import org.todo.components.CButton;
import org.todo.utils.DB.events.DatabaseEvent;
import org.todo.utils.DB.DatabaseObserver;
import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.General.GUI_General_Chart;
import org.todo.utils.GUI.General.GUI_General_Messages;
import org.todo.utils.GUI.Tag.GUI_Tag_Add;
import org.todo.utils.GUI.Tag.GUI_Tag_List;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CategoriesScreen implements Screen {

    private JPanel contentPanel;
    private JPanel tagListPanel;
    private final CButton cButton = new CButton();

    private List<Tag> tags;
    public static JTextField searchField;

    private final DatabaseOperations dbOperations;

    public CategoriesScreen(DatabaseOperations dbOperations) {
        this.dbOperations = dbOperations;
        initializeGUI();
        setupDatabaseListener();
        setupCommonKeyBindings(contentPanel, searchField);
    }

    private void setupDatabaseListener() {
        DatabaseObserver.getInstance().addListener(event -> {
            if (event.getTable() == DatabaseEvent.Table.TAGS ||
                event.getTable() == DatabaseEvent.Table.TASK_TAGS) {
                SwingUtilities.invokeLater(this::updateTags);
            }
        });
    }

    @Override
    public JPanel getPanel() {
        return contentPanel;
    }

    private void initializeGUI() {
        FlatMacDarkLaf.setup();

        tags = dbOperations.getAllTags();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toolBar.setBackground(Color.decode("#282828"));

        JButton addButton = cButton.createButton("Kategorie hinzufügen",
                "icons/png/add-circle-outline.png");

        JLabel searchLabel = new JLabel("Suchen:");
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(10);
        searchField.setPreferredSize(new Dimension(150, 42));
        searchField.setMaximumSize(new Dimension(300, 42));
        searchField.setToolTipText("Suche nach Kategorien (Strg + F)");
        searchField.setColumns(50);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTags();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTags();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTags();
            }
        });

        addButton.addActionListener(e -> {
            GUI_Tag_Add.openAddTagDialog(dbOperations);
        });

        toolBar.add(searchLabel);
        toolBar.add(Box.createHorizontalStrut(8));
        toolBar.add(searchField);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(Box.createHorizontalStrut(20));
        toolBar.add(addButton);

        contentPanel.add(toolBar, BorderLayout.NORTH);

        tagListPanel = new JPanel();
        tagListPanel.setLayout(new BoxLayout(tagListPanel, BoxLayout.Y_AXIS));
        tagListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(tagListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(320, 400));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        GUI_General_Chart.contentPanel = tagListPanel;
        GUI_Tag_List.contentPanel = tagListPanel;
        GUI_General_Messages.contentPanel = tagListPanel;

        renderContent(tags);
    }

    private void renderContent(List<Tag> tags) {
        GUI_Tag_List.showContent(tags, searchField.getText(), "Keine Kategorien vorhanden", "Keine Kategorien gefunden", dbOperations);
    }

    public void updateTags() {
        this.tags = dbOperations.getAllTags();
        renderContent(tags);
    }
}
