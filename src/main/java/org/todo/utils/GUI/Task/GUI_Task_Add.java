package org.todo.utils.GUI.Task;

import org.todo.classes.Tag;
import org.todo.utils.DB.DatabaseOperations;
import raven.datetime.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

import static org.todo.components.GuiComponents.frame;

public class GUI_Task_Add {
    public static void openAddTaskDialog(DatabaseOperations dbOperations) {
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextArea notesField = new JTextArea(5, 30);
        notesField.setAutoscrolls(true);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);
        Set<KeyStroke> forwardKeys = new HashSet<>(Arrays.asList(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0)));
        Set<KeyStroke> backwardKeys = new HashSet<>(
                Arrays.asList(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK)));
        notesField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        notesField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
        JScrollPane notesScrollPane = new JScrollPane(notesField);

        JFormattedTextField dateField = new JFormattedTextField();
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(timeEditor);

        JComboBox<String> priorityField = new JComboBox<>();
        priorityField.addItem("Niedrig");
        priorityField.addItem("Mittel");
        priorityField.addItem("Hoch");
        priorityField.setSelectedIndex(0);

        JButton categoriesButton = new JButton("Kategorien auswählen");

        List<Tag> allTags = dbOperations.getAllTags();
        List<Tag> selectedTags = new ArrayList<>();

        categoriesButton.addActionListener(e -> GUI_Task_Categories.openCategoriesDialog(allTags, selectedTags));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel titleLabel = new JLabel("<html>Titel: <span style='color: red;'>*</span></html>");
        titleLabel.setLabelFor(titleField);
        titleField.getAccessibleContext().setAccessibleName("Titel (Pflichtfeld)");

        JLabel descriptionLabel = new JLabel("Beschreibung:");
        descriptionLabel.setLabelFor(descriptionField);
        descriptionField.getAccessibleContext().setAccessibleName("Beschreibung");

        JLabel dateLabel = new JLabel("<html>Fälligkeitsdatum: <span style='color: red;'>*</span></html>");
        dateLabel.setLabelFor(dateField);
        dateField.getAccessibleContext().setAccessibleName("Fälligkeitsdatum (Pflichtfeld)");

        JLabel timeLabel = new JLabel("<html>Uhrzeit: <span style='color: red;'>*</span></html>");
        timeLabel.setLabelFor(timeSpinner);
        timeSpinner.getAccessibleContext().setAccessibleName("Uhrzeit (Pflichtfeld)");

        JLabel priorityLabel = new JLabel("Priorität:");
        priorityLabel.setLabelFor(priorityField);
        priorityField.getAccessibleContext().setAccessibleName("Priorität");

        JLabel notesLabel = new JLabel("Notizen:");
        notesLabel.setLabelFor(notesField);
        notesField.getAccessibleContext().setAccessibleName("Notizen");

        fieldsPanel.add(titleLabel);
        fieldsPanel.add(titleField);
        fieldsPanel.add(descriptionLabel);
        fieldsPanel.add(descriptionField);
        fieldsPanel.add(dateLabel);
        fieldsPanel.add(dateField);
        fieldsPanel.add(timeLabel);
        fieldsPanel.add(timeSpinner);
        fieldsPanel.add(priorityLabel);
        fieldsPanel.add(priorityField);
        fieldsPanel.add(new JLabel("Kategorien:"));
        fieldsPanel.add(categoriesButton);

        inputPanel.add(fieldsPanel, BorderLayout.NORTH);

        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(new JLabel("Notizen:"), BorderLayout.NORTH);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 4));
        notesPanel.add(spacerPanel, BorderLayout.CENTER);

        notesPanel.add(notesScrollPane, BorderLayout.SOUTH);

        inputPanel.add(notesPanel, BorderLayout.CENTER);

        DatePicker datePicker = new DatePicker();
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
        datePicker.setUsePanelOption(true);
        datePicker.setEditor(dateField);
        datePicker.setLocale(Locale.GERMANY);
        datePicker.setDateFormat("dd.MM.yyyy");

        boolean validTitleInput = false;
        boolean validDateInput = false;

        while (!validTitleInput || !validDateInput) {

            int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Aufgabe hinzufügen",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {

                String titleInput = titleField.getText().trim();

                validTitleInput = !titleInput.isEmpty();
                validDateInput = dateField.isEditValid();

                if (!validTitleInput) {
                    titleField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                    titleField.getAccessibleContext().setAccessibleDescription("Titel-Eingabefeld ist leer");
                }
                if (!validDateInput) {
                    dateField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                    dateField.getAccessibleContext().setAccessibleDescription("Datum-Eingabefeld ist ungültig");
                }

                if (!validTitleInput || !validDateInput) {
                    StringBuilder errorMessage = new StringBuilder("Ungültige Eingaben:\n");
                    if (!validTitleInput)
                        errorMessage.append("- Titel ist leer\n");
                    if (!validDateInput)
                        errorMessage.append("- Datum ist ungültig\n");
                    JOptionPane.showMessageDialog(frame, errorMessage.toString(), "Fehler", JOptionPane.ERROR_MESSAGE);
                } else {
                    LocalDate selectedDate = datePicker.getSelectedDate();
                    Date selectedTime = (Date) timeSpinner.getValue();

                    LocalTime selectedLocalTime = LocalTime.ofInstant(selectedTime.toInstant(), ZoneId.systemDefault());
                    LocalDateTime newDueDate = LocalDateTime.of(selectedDate, selectedLocalTime);

                    String priority = Objects.requireNonNull(priorityField.getSelectedItem()).toString();

                    String id = UUID.randomUUID().toString();
                    dbOperations.addTask(id, titleField.getText().trim(), descriptionField.getText().trim(), notesField.getText().trim(), newDueDate, false, false, priority);
                    dbOperations.updateTagsForTask(id, selectedTags);

                }
            } else {
                break;
            }

        }
    }
}
