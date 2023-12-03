package components;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ImageIconLoader {
    public static ImageIcon getImageIcon(String path) {
        ImageIcon stopIcon = new ImageIcon(Objects.requireNonNull(ImageIconLoader.class.getResource(path)));
        Image image = stopIcon.getImage();
        image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        stopIcon.setImage(image);
        return stopIcon;
    }
}
