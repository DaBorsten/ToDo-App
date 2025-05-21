package org.todo.screens;

import javax.swing.*;

import org.todo.components.CScaleIcon;
import org.todo.utils.DB.DatabaseOperations;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SideBar extends JPanel {
    private static final Color BUTTON_DEFAULT = new Color(40, 40, 40);
    private static final Color BUTTON_HOVER = new Color(50, 50, 50);
    private static final Color BUTTON_ACTIVE = new Color(69, 69, 69);
    private static final Color BUTTON_ACTIVE_HOVER = new Color(79, 79, 79);

    private final JPanel contentPanel;
    private final Map<String, Supplier<JPanel>> screenSuppliers = new HashMap<>();
    private JButton activeButton;
    private final Map<String, IconPair> buttonIcons = new HashMap<>();

    private record IconPair(String defaultIcon, String activeIcon) {}

    public SideBar(JPanel contentPanel, DatabaseOperations dbOperations) {
        this.contentPanel = contentPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, getHeight()));
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonIcons.put("Home", new IconPair("icons/png/home-outline.png", "icons/png/home-filled.png"));
        buttonIcons.put("Favoriten", new IconPair("icons/png/star-outline.png", "icons/png/star-filled.png"));
        buttonIcons.put("Abgeschlossen", new IconPair("icons/png/checkbox-outline.png", "icons/png/checkbox-filled.png"));
        buttonIcons.put("Statistiken", new IconPair("icons/png/pie-chart-outline.png", "icons/png/pie-chart-filled.png"));
        buttonIcons.put("Kategorien", new IconPair("icons/png/pricetags-outline.png", "icons/png/pricetags-filled.png"));

        screenSuppliers.put("Home", () -> new HomeScreen(dbOperations).getPanel());
        screenSuppliers.put("Favoriten", () -> new FavoritesScreen(dbOperations).getPanel());
        screenSuppliers.put("Abgeschlossen", () -> new DoneScreen(dbOperations).getPanel());
        screenSuppliers.put("Statistiken", () -> new StatisticsScreen(dbOperations).getPanel());
        screenSuppliers.put("Kategorien", () -> new CategoriesScreen(dbOperations).getPanel());


        add(createCustomButton("Home", buttonIcons.get("Home").defaultIcon()));
        add(createCustomButton("Favoriten", buttonIcons.get("Favoriten").defaultIcon()));
        add(createCustomButton("Abgeschlossen", buttonIcons.get("Abgeschlossen").defaultIcon()));
        add(createCustomButton("Statistiken", buttonIcons.get("Statistiken").defaultIcon()));
        add(createCustomButton("Kategorien", buttonIcons.get("Kategorien").defaultIcon()));
    }

    private JButton createCustomButton(String text, String iconPath) {
        CScaleIcon cscaleIcon = new CScaleIcon();
        ImageIcon icon = cscaleIcon.scaleIcon(iconPath, 24, 24);

        JButton button = new JButton(icon);
        button.setText(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(8);
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setMaximumSize(new Dimension(500, 60));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setFocusPainted(false);
        button.setOpaque(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(button.equals(activeButton) ? BUTTON_ACTIVE_HOVER : BUTTON_HOVER);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setOpaque(button.equals(activeButton));
                button.setBackground(button.equals(activeButton) ? BUTTON_ACTIVE : BUTTON_DEFAULT);
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(BUTTON_ACTIVE_HOVER);
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!button.equals(activeButton)) {
                    button.setOpaque(false);
                    button.setBackground(BUTTON_DEFAULT);
                } else {
                    button.setBackground(BUTTON_ACTIVE);
                }
                button.repaint();
            }
        });

        button.setFocusable(true);
        button.setRequestFocusEnabled(true);

        button.setMnemonic(text.charAt(0));
        button.setFocusable(true);
        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    button.doClick();
                }
            }
        });

        button.addActionListener(e -> {
            setActivePage(text, button);
        });

        return button;
    }

    private void setActivePage(String page, JButton newActiveButton) {
        this.activeButton = newActiveButton;

        Supplier<JPanel> panelSupplier = screenSuppliers.get(page);
        if (panelSupplier != null) {
            JPanel newPanel = panelSupplier.get();
            contentPanel.removeAll();
            contentPanel.add(newPanel);

            CScaleIcon cscaleIcon = new CScaleIcon();
            
            for (Component comp : getComponents()) {
                if (comp instanceof JButton btn) {
                    boolean isActive = btn.equals(activeButton);
                    btn.setOpaque(isActive);
                    btn.setBackground(isActive ? BUTTON_ACTIVE : BUTTON_DEFAULT);
                    
                    IconPair icons = buttonIcons.get(btn.getText());
                    if (icons != null) {
                        String iconPath = isActive ? icons.activeIcon() : icons.defaultIcon();
                        btn.setIcon(cscaleIcon.scaleIcon(iconPath, 24, 24));
                    }
                }
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            System.err.println("Keine Ansicht für Seite: " + page);
        }
    }

    public void initializeHomeScreen() {
        for (Component comp : getComponents()) {
            if (comp instanceof JButton btn && btn.getText().contains("Home")) {
                setActivePage("Home", btn);
                break;
            }
        }
    }
}
