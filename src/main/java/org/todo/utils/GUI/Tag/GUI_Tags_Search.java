package org.todo.utils.GUI.Tag;

import org.todo.classes.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class GUI_Tags_Search {

    public static List<Tag> searchTags(List<Tag> tags, String searchText) {
        if (searchText == null || searchText.isEmpty()) return tags;

        return tags.stream()
                .filter(tag -> tag.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

}

