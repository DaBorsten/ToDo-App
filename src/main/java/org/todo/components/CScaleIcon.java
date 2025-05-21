package org.todo.components;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.Objects;

public class CScaleIcon {

    public ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
