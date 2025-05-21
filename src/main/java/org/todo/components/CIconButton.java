package org.todo.components;

import javax.swing.JButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CIconButton {
    private static final Color BUTTON_DEFAULT = new Color(54, 54, 54);
    private static final Color BUTTON_HOVER = new Color(64, 64, 64);
    private static final Color BUTTON_PRESSED = new Color(74, 74, 74);

    private final CScaleIcon cscaleIcon = new CScaleIcon();

    public JButton createButton(String iconPath) {
        JButton button = new JButton();
        button.setIcon(cscaleIcon.scaleIcon(iconPath, 24, 24));
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(8);
        button.setMargin(new Insets(8, 8, 8, 8));
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
