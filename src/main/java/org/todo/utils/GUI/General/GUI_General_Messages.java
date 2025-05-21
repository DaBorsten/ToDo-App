package org.todo.utils.GUI.General;

import org.todo.components.CScaleIcon;

import javax.swing.*;
import java.awt.*;

public class GUI_General_Messages {

    public static JPanel contentPanel = new JPanel();

    public static void displayNoContentMessage() {
        displayNoContentMessage(null);
    }

    public static void displayNoContentMessage(String errorMessage) {
        contentPanel.removeAll();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(Box.createVerticalGlue());

        CScaleIcon cscaleIcon = new CScaleIcon();
        ImageIcon noTasksIcon = cscaleIcon.scaleIcon("icons/png/chatbox-ellipses-outline.png", 128, 128);
        JLabel iconLabel = new JLabel(noTasksIcon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLabel);

        contentPanel.add(Box.createVerticalStrut(20));

        JLabel noTasksLabel = new JLabel(errorMessage != null && !errorMessage.isEmpty() ? errorMessage : "Ressource nicht vorhanden");
        noTasksLabel.setFont(noTasksLabel.getFont().deriveFont(18f));
        noTasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(noTasksLabel);

        contentPanel.add(Box.createVerticalGlue());

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void displayNoContentFoundMessage() {
        displayNoContentMessage(null);
    }

    public static void displayNoContentFoundMessage(String customMessage) {
        contentPanel.removeAll();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(Box.createVerticalGlue());

        CScaleIcon cscaleIcon = new CScaleIcon();
        ImageIcon noTasksIcon = cscaleIcon.scaleIcon("icons/png/search-outline.png", 128, 128);
        JLabel iconLabel = new JLabel(noTasksIcon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLabel);

        contentPanel.add(Box.createVerticalStrut(20));

        JLabel noTasksLabel = new JLabel(customMessage != null && !customMessage.isEmpty() ? customMessage : "Ressource nicht gefunden");
        noTasksLabel.setFont(noTasksLabel.getFont().deriveFont(18f));
        noTasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(noTasksLabel);

        contentPanel.add(Box.createVerticalGlue());

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
