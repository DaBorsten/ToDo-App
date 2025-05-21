package org.todo.utils.GUI.Task;

import org.todo.classes.Tag;
import org.todo.utils.DB.DatabaseOperations;
import raven.datetime.DatePicker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static org.todo.components.GuiComponents.frame;

public class GUI_Task_Edit {

    public static void openEditTaskDialog(String id, String title, String description, LocalDateTime dueDate,
                                          boolean completed, boolean favorite, String notes, String priority, DatabaseOperations dbOperations) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextArea notesField = new JTextArea(5, 30);
        JCheckBox completedCheckBox = new JCheckBox("Abgeschlossen", completed);
        notesField.setAutoscrolls(true);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesField);


        titleField.setText(title);
        descriptionField.setText(description);
        completedCheckBox.setSelected(completed);
        notesField.setText(notes);

        JComboBox<String> priorityField = new JComboBox<>();
        priorityField.addItem("Niedrig");
        priorityField.addItem("Mittel");
        priorityField.addItem("Hoch");
        priorityField.setSelectedItem(priority);


        JFormattedTextField dateField = new JFormattedTextField();
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(Date.from(dueDate.atZone(ZoneId.systemDefault()).toInstant()));


        JButton categoriesButton = new JButton("Kategorien auswählen");

        List<Tag> allTags = dbOperations.getAllTags();
        List<Tag> selectedTags = dbOperations.getTagsForTask(id);

        categoriesButton.addActionListener(e -> GUI_Task_Categories.openCategoriesDialog(allTags, selectedTags));


        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        titleField.getDocument().addDocumentListener(new DocumentListener() {
            private boolean hadContent = false;

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBorder();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBorder();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateBorder();
            }

            private void updateBorder() {
                boolean hasContent = !titleField.getText().isEmpty();

                if (!hasContent && hadContent) {
                    titleField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                } else {
                    titleField.setBorder(UIManager.getBorder("TextField.border"));
                }

                hadContent = hasContent;
            }
        });

        dateField.getDocument().addDocumentListener(new DocumentListener() {
            private boolean wasValid = false;

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBorder();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBorder();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateBorder();
            }

            private void updateBorder() {
                boolean isValid = dateField.isEditValid() && dateField.isValid();

                if (!isValid && wasValid) {
                    dateField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                } else {
                    dateField.setBorder(UIManager.getBorder("TextField.border"));
                }

                wasValid = isValid;
            }
        });


        fieldsPanel.add(new JLabel("<html>Titel: <span style='color: red;'>*</span></html>"));
        fieldsPanel.add(titleField);

        fieldsPanel.add(new JLabel("Beschreibung:"));
        fieldsPanel.add(descriptionField);

        fieldsPanel.add(new JLabel("<html>Fälligkeitsdatum: <span style='color: red;'>*</span></html>"));
        fieldsPanel.add(dateField);

        fieldsPanel.add(new JLabel("<html>Uhrzeit: <span style='color: red;'>*</span></html>"));
        fieldsPanel.add(timeSpinner);

        fieldsPanel.add(new JLabel("Priorität:"));
        fieldsPanel.add(priorityField);

        fieldsPanel.add(new JLabel("Kategorien:"));
        fieldsPanel.add(categoriesButton);

        fieldsPanel.add(new JLabel("Abgeschlossen:"));
        fieldsPanel.add(completedCheckBox);


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
        datePicker.setSelectedDate(dueDate.toLocalDate());

        boolean validTextInput = false;
        boolean validDateInput = false;

        while (!validTextInput || !validDateInput) {

            int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Aufgabe bearbeiten",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {

                String titleInput = titleField.getText().trim();

                validTextInput = !titleInput.isEmpty();
                validDateInput = dateField.isEditValid();

                if (!validTextInput) {
                    titleField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                }
                if (!validDateInput) {
                    dateField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                }

                if (!validTextInput || !validDateInput) {
                    JOptionPane.showMessageDialog(frame, "Ungültige Eingaben.\nBitte überprüfen Sie Ihre Angaben.", "Fehler", JOptionPane.ERROR_MESSAGE);
                } else {
                    LocalDateTime newDueDate;
                    try {
                        String newDateText = dateField.getText().trim();
                        String newTimeText = timeEditor.getTextField().getText().trim();
                        LocalDate newDate = LocalDate.parse(newDateText, dateFormatter);
                        LocalTime newTime = LocalTime.parse(newTimeText, timeFormatter);
                        newDueDate = LocalDateTime.of(newDate, newTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    String newTitle = titleField.getText().trim();
                    String newDescription = descriptionField.getText().trim();
                    boolean newCompleted = completedCheckBox.isSelected();
                    String newNotes = notesField.getText().trim();
                    String newPriority = Objects.requireNonNull(priorityField.getSelectedItem()).toString();

                    dbOperations.updateTask(id, newTitle, newDescription, newDueDate, newCompleted, favorite, newNotes, newPriority);
                    dbOperations.updateTagsForTask(id, selectedTags);

                }
            } else {
                break;
            }
        }
    }
}
