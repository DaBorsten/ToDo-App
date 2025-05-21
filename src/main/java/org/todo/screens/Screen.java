package org.todo.screens;

import javax.swing.*;
import java.awt.event.*;

public interface Screen {
    JPanel getPanel();

    default void setupCommonKeyBindings(JPanel contentPanel, JTextField searchField) {
        if (searchField == null)
            return;

        Action focusSearchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocusInWindow();
                searchField.selectAll();
            }
        };

        contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusSearch");
        contentPanel.getActionMap().put("focusSearch", focusSearchAction);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    contentPanel.requestFocusInWindow();
                    e.consume();
                }
            }
        });
    }
}
