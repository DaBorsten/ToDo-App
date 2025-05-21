package org.todo.components;

import org.todo.classes.SortCriterion;
import org.todo.utils.DB.DatabaseObserver;
import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.DB.events.DatabaseEvent;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;

public class CSortDialog {

    private DefaultListModel<String> sortModel;
    private AccessibleSortList sortList;
    private JComboBox<String> sortOptions;
    private JDialog dialog;
    private List<SortCriterion> sortCriteria = new ArrayList<>();
    private final JFrame frame;

    private final DatabaseOperations dbOperations;

    public CSortDialog(JFrame frame, DatabaseOperations dbOperations) {
        this.frame = frame;
        this.dbOperations = dbOperations;
    }

    private class AccessibleSortList extends JList<String> {
        public AccessibleSortList(ListModel<String> model) {
            super(model);
        }

        @Override
        public AccessibleContext getAccessibleContext() {
            if (accessibleContext == null) {
                accessibleContext = new AccessibleSortJList();
            }
            return accessibleContext;
        }

        protected class AccessibleSortJList extends JList<String>.AccessibleJList {
            @Override
            public String getAccessibleName() {
                int index = getSelectedIndex();
                if (index != -1) {
                    String visualText = getModel().getElementAt(index);
                    String criterion = visualText.substring(2);
                    boolean isAscending = visualText.startsWith("▲");
                    return criterion + (isAscending ? " aufsteigend" : " absteigend");
                }
                return super.getAccessibleName();
            }
        }
    }

    public void showSortDialog() {
        dialog = new JDialog(frame, "Sortieren", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        String[] sortChoices = { "Titel", "Datum", "Priorität" };
        sortOptions = new JComboBox<>(sortChoices);

        sortModel = new DefaultListModel<>();
        sortList = new AccessibleSortList(sortModel);
        sortList.getAccessibleContext().setAccessibleDescription(
                "Liste der Sortieroptionen. Doppelklicken oder Leertaste drücken, um die Sortierrichtung zu ändern.");
        JScrollPane scrollPane = new JScrollPane(sortList);

        loadSortCriteria();

        sortList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleSortDirection();
                }
            }
        });

        sortList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    toggleSortDirection();
                }
            }
        });

        JButton addSortButton = new JButton("➕ Hinzufügen");
        addSortButton.setToolTipText("Hinzufügen");
        addSortButton.getAccessibleContext().setAccessibleName("Hinzufügen");
        addSortButton.getAccessibleContext().setAccessibleDescription(
                "Ausgewählte Sortieroption zur Liste hinzufügen. Man kann die gleiche Sortieroption nicht mehrmals hinzufügen.");
        JButton moveUpButton = new JButton("▲ Hoch");
        moveUpButton.setToolTipText("Hoch");
        moveUpButton.getAccessibleContext().setAccessibleName("Hoch");
        moveUpButton.getAccessibleContext()
                .setAccessibleDescription("In der Liste die Sortieroption nach oben verschieben falls möglich.");
        JButton moveDownButton = new JButton("▼ Runter");
        moveDownButton.setToolTipText("Runter");
        moveDownButton.getAccessibleContext().setAccessibleName("Runter");
        moveDownButton.getAccessibleContext()
                .setAccessibleDescription("In der Liste die Sortieroption nach unten verschieben falls möglich.");
        JButton removeSortButton = new JButton("❌ Entfernen");
        removeSortButton.setToolTipText("Entfernen");
        removeSortButton.getAccessibleContext().setAccessibleName("Entfernen");
        removeSortButton.getAccessibleContext().setAccessibleDescription("Sortieroption in der Liste entfernen.");
        JButton applyButton = new JButton("✔️ Anwenden");
        applyButton.setToolTipText("Anwenden");
        applyButton.getAccessibleContext().setAccessibleName("Anwenden");
        applyButton.getAccessibleContext().setAccessibleDescription("Sortieroptionen anwenden.");

        JPanel topPanel = new JPanel();
        topPanel.add(sortOptions);
        topPanel.add(addSortButton);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);
        buttonPanel.add(removeSortButton);
        buttonPanel.add(applyButton);

        addSortButton.addActionListener(e -> addSortOption());
        moveUpButton.addActionListener(e -> moveSortUp());
        moveDownButton.addActionListener(e -> moveSortDown());
        removeSortButton.addActionListener(e -> removeSortOption());
        applyButton.addActionListener(e -> applySorting());

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.EAST);

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void loadSortCriteria() {
        sortCriteria = dbOperations.loadSortCriteria();
        sortModel.clear();

        for (SortCriterion sc : sortCriteria) {
            String symbol = sc.isAscending() ? "▲ " : "▼ ";
            String visualText = symbol + sc.getCriterion();
            sortModel.addElement(visualText);
        }

        sortList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                String text = value.toString();

                AttributedString attributedString = new AttributedString(text);
                attributedString.addAttribute(new AttributedCharacterIterator.Attribute("hidden") {
                }, true, 0, 2);

                label.setText(text);

                String criterion = text.substring(2);
                boolean isAscending = text.startsWith("▲");
                String accessibleText = criterion + (isAscending ? " aufsteigend" : " absteigend");
                label.getAccessibleContext().setAccessibleName(accessibleText);

                label.setToolTipText("Doppelklick oder Leertaste drücken um die Sortierrichtung zu ändern");

                return label;
            }
        });
    }

    private void toggleSortDirection() {
        int index = sortList.getSelectedIndex();
        if (index == -1)
            return;

        String currentItem = sortModel.get(index);
        boolean isAscending = currentItem.startsWith("▲");
        String criterion = currentItem.substring(2);

        boolean newAscending = !isAscending;
        String visualText = (newAscending ? "▲ " : "▼ ") + criterion;

        sortModel.set(index, visualText);
        sortCriteria.set(index, new SortCriterion(sortCriteria.get(index).getCriterion(), newAscending));

        String accessibleText = criterion + (newAscending ? " aufsteigend" : " absteigend");

        sortList.clearSelection();
        sortList.setSelectedIndex(index);

        sortList.getAccessibleContext().setAccessibleName(accessibleText);
        sortList.getAccessibleContext().firePropertyChange(
                AccessibleContext.ACCESSIBLE_NAME_PROPERTY,
                null,
                accessibleText);

        if (sortList.getCellRenderer() != null) {
            Component c = sortList.getCellRenderer().getListCellRendererComponent(
                    sortList, visualText, index, true, true);
            if (c instanceof JLabel) {
                ((JLabel) c).getAccessibleContext().setAccessibleName(accessibleText);
            }
        }
    }

    private void addSortOption() {
        String selectedOption = (String) sortOptions.getSelectedItem();
        if (selectedOption == null)
            return;

        for (int i = 0; i < sortModel.size(); i++) {
            String existingItem = sortModel.get(i);
            String existingOption = existingItem.substring(2);
            if (existingOption.equals(selectedOption)) {
                AccessibleContext context = dialog.getAccessibleContext();
                if (context != null) {
                    context.firePropertyChange(
                            AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY,
                            null,
                            "Die Sortieroption " + selectedOption + " ist bereits in der Liste vorhanden");
                }
                JOptionPane.showMessageDialog(
                        dialog,
                        "Die Sortieroption ist bereits vorhanden",
                        "Doppelter Eintrag",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String criterion = switch (selectedOption) {
            case "Titel" -> "titel";
            case "Datum" -> "datum";
            case "Priorität" -> "priorität";
            default -> "titel";
        };

        String visualText = "▲ " + selectedOption;
        String screenReaderText = selectedOption + " aufsteigend";

        sortModel.addElement(visualText);
        sortList.putClientProperty("AccessibleName." + (sortModel.size() - 1), screenReaderText);
        sortCriteria.add(new SortCriterion(criterion, true));
    }

    private void moveSortUp() {

        int index = sortList.getSelectedIndex();

        if (index <= 0)
            return;

        String element = sortModel.getElementAt(index);

        sortModel.remove(index);
        sortModel.add(index - 1, element);
        sortList.setSelectedIndex(index - 1);

    }

    private void moveSortDown() {

        int index = sortList.getSelectedIndex();

        if (index >= sortModel.size() - 1)
            return;

        String element = sortModel.getElementAt(index);
        sortModel.remove(index);
        sortModel.add(index + 1, element);
        sortList.setSelectedIndex(index + 1);

    }

    private void removeSortOption() {

        int index = sortList.getSelectedIndex();

        if (index == -1)
            return;

        sortModel.remove(index);

        if (sortModel.isEmpty())
            return;

        if (index < sortModel.size()) {
            sortList.setSelectedIndex(index);
        } else {
            sortList.setSelectedIndex(index - 1);
        }
    }

    private void applySorting() {
        sortCriteria.clear();

        for (int i = 0; i < sortModel.size(); i++) {
            String item = sortModel.get(i);
            boolean ascending = item.startsWith("▲");
            String criterion = item.substring(2);
            sortCriteria.add(new SortCriterion(criterion, ascending));
        }

        dbOperations.saveSortCriteria(sortCriteria);
        
        DatabaseObserver.getInstance().notifyListeners(
                new DatabaseEvent(DatabaseEvent.Operation.UPDATE, DatabaseEvent.Table.SORT_CRITERIA, ""));

        dialog.dispose();
    }

}
