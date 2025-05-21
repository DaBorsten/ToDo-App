package org.todo.components;

import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.Tag.GUI_Tag_Edit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CTagListItem extends RoundedPanel {
    private final String id;
    private final String title;
    private final int color;
    private static final int BORDER_RADIUS = 15;
    private final CIconButton cIconButton = new CIconButton();
    private final DatabaseOperations dbOperations;

    public CTagListItem(String id, String title, int color, DatabaseOperations dbOperations) {
        super(BORDER_RADIUS);
        this.id = id;
        this.title = title;
        this.color = color;
        this.dbOperations = dbOperations;

        initializeGUI();
    }

    private void initializeGUI() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        setOpaque(false);
        setBackground(new Color(24, 24, 24));

        setBorder(new RoundedBorder(BORDER_RADIUS, new Color(61, 61, 61)));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(50, 50, 50));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!CTagListItem.this.contains(e.getPoint())) {
                    setBackground(new Color(24, 24, 24));
                    repaint();
                }
            }
        });

        JLabel titleLabel = new JLabel("Kategorie: " + title);

        JButton editButton = cIconButton.createButton("icons/png/options-outline.png");
        editButton.addActionListener(this::editTag);

        JButton deleteButton = cIconButton.createButton("icons/png/trash-outline.png");
        deleteButton.addActionListener(this::deleteTag);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(titleLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(editButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(deleteButton)));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(editButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(deleteButton)));
    }

    void editTag(ActionEvent e) {
        GUI_Tag_Edit.openEditTagDialog(id, title, color, dbOperations);
    }

    private void deleteTag(ActionEvent e) {
        dbOperations.deleteTag(id);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);

        int stripWidth = BORDER_RADIUS;

        g.setColor(new Color(color));
        g.fillRoundRect(0, 0, stripWidth, getHeight(), BORDER_RADIUS, BORDER_RADIUS);

        int cutoffX = stripWidth / 2;

        g.setColor(getBackground());
        g.fillRect(cutoffX, 0, stripWidth, getHeight());
    }
}
