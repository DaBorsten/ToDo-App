package org.todo.utils.GUI.Task;

import org.todo.classes.Tag;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.todo.components.GuiComponents.frame;

public class GUI_Task_Categories {
    public static void openCategoriesDialog(List<Tag> allTags, List<Tag> selectedTags) {
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        Map<JCheckBox, Tag> checkBoxTagMap = new HashMap<>();

        if (allTags.isEmpty()) {
            JLabel noCategoriesLabel = new JLabel("Keine Kategorien verfügbar.");
            noCategoriesLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
            labelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            labelPanel.add(Box.createVerticalGlue());
            labelPanel.add(noCategoriesLabel);
            labelPanel.add(Box.createVerticalGlue());

            checkBoxPanel.add(labelPanel);
        } else {
            for (Tag tag : allTags) {
                JCheckBox checkBox = new JCheckBox(tag.getTitle());
                checkBoxes.add(checkBox);
                checkBoxTagMap.put(checkBox, tag);
                checkBoxPanel.add(checkBox);

                if (selectedTags.stream().anyMatch(selectedTag -> selectedTag.getId().equals(tag.getId()))) {
                    checkBox.setSelected(true);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        JDialog categoryDialog = new JDialog(frame, "Wähle Kategorien", true);
        categoryDialog.setLayout(new BorderLayout());
        categoryDialog.add(scrollPane, BorderLayout.CENTER);
        categoryDialog.setMinimumSize(new Dimension(200, 200));

        JButton confirmButton = new JButton("Bestätigen");
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        categoryDialog.add(confirmButton, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            List<Tag> selectedCategories = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedCategories.add(checkBoxTagMap.get(checkBox));
                }
            }
            selectedTags.clear();
            selectedTags.addAll(selectedCategories);
            categoryDialog.dispose();
        });

        categoryDialog.pack();
        categoryDialog.setLocationRelativeTo(frame);
        categoryDialog.setVisible(true);
    }
}
