package components;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ImageIconLoader {
    public static ImageIcon getImageIcon(String path, int w) {
        ImageIcon stopIcon = new ImageIcon(Objects.requireNonNull(ImageIconLoader.class.getResource(path)));
        Image image = stopIcon.getImage();
        image = image.getScaledInstance(w, w, Image.SCALE_SMOOTH);
        stopIcon.setImage(image);
        return stopIcon;
    }
}
