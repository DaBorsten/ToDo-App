package org.todo.components;

import javax.swing.*;

import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.Task.GUI_Task_Edit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class CTaskListItem extends RoundedPanel {
    private final String id;
    private final String title;
    private final String description;
    private final LocalDateTime dueDate;
    private boolean completed;
    private final boolean favorite;
    private final String notes;
    private final String priority;
    private static final int BORDER_RADIUS = 15;
    private final CIconButton cIconButton = new CIconButton();
    private final DatabaseOperations dbOperations;

    public CTaskListItem(String id, String title, String description, LocalDateTime dueDate, boolean completed, boolean favorite, String notes, String priority, DatabaseOperations dbOperations) {
        super(BORDER_RADIUS);
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.favorite = favorite;
        this.notes = notes;
        this.priority = priority;
        this.dbOperations = dbOperations;

        initializeGUI();
    }

    private void initializeGUI() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        setOpaque(false);
        setBackground(new Color(26, 26, 26));

        setBorder(new RoundedBorder(BORDER_RADIUS, new Color(61, 61, 61)));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(50, 50, 50));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!CTaskListItem.this.contains(e.getPoint())) {
                    setBackground(new Color(26, 26, 26));
                    repaint();
                }
            }
        });

        JCheckBox completedCheckBox = new JCheckBox("", completed);
        completedCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        CScaleIcon cscaleIcon = new CScaleIcon();
        ImageIcon uncheckedIcon = cscaleIcon.scaleIcon("icons/png/checkmark-circle-outline.png", 28, 28);
        ImageIcon hoverIcon = cscaleIcon.scaleIcon("icons/png/checkmark-circle-hover.png", 28, 28);
        ImageIcon checkedIcon = cscaleIcon.scaleIcon("icons/png/checkmark-circle-filled.png", 28, 28);
        completedCheckBox.setIcon(uncheckedIcon);
        completedCheckBox.setRolloverIcon(hoverIcon);
        completedCheckBox.setSelectedIcon(checkedIcon);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(16f));
        descriptionLabel.setForeground(new Color(150, 150, 150));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.GERMANY);

        JLabel dueDateLabel = new JLabel(dueDate.format(dateFormatter));
        dueDateLabel.setForeground(new Color(254, 194, 120));
        JLabel separator = new JLabel(" ● ");
        separator.setForeground(new Color(254, 194, 120));
        JLabel dueTimeLabel = new JLabel(dueDate.format(timeFormatter));
        dueTimeLabel.setForeground(new Color(254, 194, 120));

        completedCheckBox.addActionListener(this::updateTaskStatus);

        JButton favoriteButton = createFavoriteButton(favorite);

        JButton editButton = cIconButton.createButton("icons/png/options-outline.png");
        editButton.addActionListener(this::editTask);

        JButton deleteButton = cIconButton.createButton("icons/png/trash-outline.png");
        deleteButton.addActionListener(this::deleteTask);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(completedCheckBox)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(titleLabel)
                                .addComponent(descriptionLabel)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(dueDateLabel)
                                        .addComponent(separator)
                                        .addComponent(dueTimeLabel)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(favoriteButton)
                        .addComponent(editButton)
                        .addComponent(deleteButton));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(completedCheckBox)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel)
                                .addComponent(descriptionLabel)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(dueDateLabel)
                                        .addComponent(separator)
                                        .addComponent(dueTimeLabel)))
                        .addComponent(favoriteButton)
                        .addComponent(editButton)
                        .addComponent(deleteButton));
    }

    private void updateTaskStatus(ActionEvent e) {
        boolean newCompleted = ((JCheckBox) e.getSource()).isSelected();
        this.completed = newCompleted;
        dbOperations.updateTask(id, title, description, dueDate, newCompleted, favorite, notes, priority);
    }

    void editTask(ActionEvent e) {
        GUI_Task_Edit.openEditTaskDialog(id, title, description, dueDate, completed, favorite, notes, priority, dbOperations);
    }

    private void deleteTask(ActionEvent e) {
        dbOperations.removeExistingTagsFromTask(id);
        dbOperations.deleteTask(id);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 100, 100);
    }

    private JButton createFavoriteButton(Boolean isFavorite) {
        CScaleIcon cscaleIcon = new CScaleIcon();
        ImageIcon defaultIcon = cscaleIcon.scaleIcon("icons/png/star-yellow-outline.png", 24, 24);
        ImageIcon clickedIcon = cscaleIcon.scaleIcon("icons/png/star-yellow-filled.png", 24, 24);
        ImageIcon hoverIcon = cscaleIcon.scaleIcon("icons/png/star-yellow-hover.png", 24, 24);

        JButton button = new JButton(isFavorite ? clickedIcon : defaultIcon);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        AtomicBoolean isClicked = new AtomicBoolean(isFavorite);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(isClicked.get() ? clickedIcon : hoverIcon);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(isClicked.get() ? clickedIcon : defaultIcon);
                button.repaint();
            }
        });

        button.setFocusable(true);
        button.setRequestFocusEnabled(true);

        button.addActionListener(e -> {
            isClicked.set(!isClicked.get());
            button.setIcon(isClicked.get() ? clickedIcon : defaultIcon);

            dbOperations.updateTask(id, title, description, dueDate, completed, isClicked.get(), notes, priority);
        });

        return button;
    }
}
