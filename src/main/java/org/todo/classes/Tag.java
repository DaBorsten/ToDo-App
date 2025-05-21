package org.todo.classes;

public class Tag {
    private String id;
    private String title;
    private int color;

    public Tag(String id, String title, int color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + title + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}


