package org.todo.utils.GUI.Tag;

import org.todo.classes.Tag;
import org.todo.components.CTagListItem;
import org.todo.utils.DB.DatabaseOperations;
import org.todo.utils.GUI.General.GUI_General_Messages;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI_Tag_List {

    public static JPanel contentPanel = new JPanel();

    public static void showContent(List<Tag> tags, String searchText, String noContentMessage, String noContentFoundMessage, DatabaseOperations dbOperations) {
        if (!tags.isEmpty()) {
            displayTagsList(tags, searchText, noContentMessage, noContentFoundMessage, dbOperations);
        } else {
            GUI_General_Messages.displayNoContentMessage(noContentMessage);
        }
    }

    public static void displayTagsList(List<Tag> tags, String searchText, String noContentMessage, String noContentFoundMessage, DatabaseOperations dbOperations) {
        contentPanel.removeAll();

        List<Tag> finalTags = tags;

        finalTags = GUI_Tags_Search.searchTags(finalTags, searchText);

        if (finalTags.isEmpty()) {
            GUI_General_Messages.displayNoContentFoundMessage(noContentFoundMessage);
        } else {
            for (Tag tag : finalTags) {
                String id = tag.getId();
                String title = tag.getTitle();
                int color = tag.getColor();

                CTagListItem item = new CTagListItem(id, title, color, dbOperations);
                contentPanel.add(item);

                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
