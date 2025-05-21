package org.todo.components;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CButton {
    private static final Color BUTTON_DEFAULT = new Color(54, 54, 54);
    private static final Color BUTTON_HOVER = new Color(64, 64, 64);
    private static final Color BUTTON_PRESSED = new Color(74, 74, 74);

    private final CScaleIcon cscaleIcon = new CScaleIcon();

    public JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setName(text + " Button");
        button.getAccessibleContext().setAccessibleName(text);
        button.getAccessibleContext().setAccessibleDescription("Klicken Sie hier um " + text.toLowerCase());

        if (iconPath != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + iconPath));
            icon.setDescription(text + " Icon");
            button.setIcon(cscaleIcon.scaleIcon(iconPath, 24, 24));
        }

        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(8);
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setFocusPainted(false);
        button.setBackground(BUTTON_DEFAULT);
        button.setOpaque(false);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_DEFAULT);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(BUTTON_PRESSED);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(button.contains(e.getPoint()) ? BUTTON_HOVER : BUTTON_DEFAULT);
            }
        });

        return button;
    }
}
