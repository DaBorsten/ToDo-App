package org.todo;

import org.todo.screens.SideBar;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.DB.SQLiteDatabaseOperations;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.awt.event.KeyEvent;

public class Main {
    private JTextField searchField;
    private final DatabaseOperations dbOperations;

    public Main() {
        this.dbOperations = new SQLiteDatabaseOperations();
        dbOperations.initializeDatabase();
        initializeGUI();
    }

    private void initializeGUI() {
        FlatMacDarkLaf.setup();
        JFrame frame = new JFrame("ToDo App");
        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(getClass().getClassLoader().getResource("icons/png/ToDo-icon.png")));
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(1280, 720);
        frame.setMinimumSize(new Dimension(854, 480));
        frame.setLayout(new BorderLayout());

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));

        frame.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            @Override
            public Component getFirstComponent(Container focusCycleRoot) {
                return searchField;
            }
        });

        frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                Set.of(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0)));
        frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                Set.of(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK)));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        SideBar sideBar = new SideBar(contentPanel, dbOperations);

        frame.getContentPane().add(sideBar, BorderLayout.WEST);
        frame.getContentPane().add(contentPanel, BorderLayout.CENTER);

        sideBar.initializeHomeScreen();

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
