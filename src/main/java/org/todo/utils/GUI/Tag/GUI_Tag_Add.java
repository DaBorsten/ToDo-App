package org.todo.utils.GUI.Tag;

import org.todo.classes.Tag;
import org.todo.components.GuiComponents;
import org.todo.screens.CategoriesScreen;
import org.todo.utils.DB.DatabaseOperations;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.UUID;

import static org.todo.components.GuiComponents.frame;

public class GUI_Tag_Add {

    public static void openAddTagDialog(DatabaseOperations dbOperations) {

        JTextField titleField = new JTextField();
        JColorChooser colorField = new JColorChooser();
        colorField.setPreviewPanel(new JPanel());

        AbstractColorChooserPanel[] panels = colorField.getChooserPanels();
        for (AbstractColorChooserPanel panel : panels) {
            if (!panel.getDisplayName().equals("RGB")) {
                colorField.removeChooserPanel(panel);
            }
        }

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 10, 5, 10);
        fieldsPanel.add(new JLabel("<html>Titel: <span style='color: red;'>*</span></html>"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        fieldsPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 10, 5, 10);
        fieldsPanel.add(new JLabel("Farbe:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        fieldsPanel.add(colorField, gbc);

        boolean validTitleInput = false;

        while (!validTitleInput) {

            int result = JOptionPane.showConfirmDialog(GuiComponents.frame, fieldsPanel, "Kategorie hinzufügen",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {

                String titleInput = titleField.getText().trim();

                validTitleInput = !titleInput.isEmpty();

                if (!validTitleInput) {
                    titleField.setBorder(BorderFactory.createLineBorder(new Color(139, 60, 60), 3));
                }

                if (!validTitleInput) {
                    JOptionPane.showMessageDialog(frame, "Ungültige Eingaben.\nBitte überprüfen Sie Ihre Angaben.", "Fehler", JOptionPane.ERROR_MESSAGE);

                } else {
                    String newId = UUID.randomUUID().toString();
                    dbOperations.addTag(newId, titleField.getText(), colorField.getColor().getRGB());
                    List<Tag> tags = dbOperations.getAllTags();

                    System.out.println(tags);

                    GUI_Tag_List.showContent(tags, CategoriesScreen.searchField.getText(), "Keine Kategorien vorhanden", "Keine Kategorien gefunden", dbOperations);
                }
            } else {
                break;
            }
        }
    }
}
