package components;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ImageIconLoader {
    public static ImageIcon getImageIcon(String path, int w) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(ImageIconLoader.class.getResource(path)));
        Image image = icon.getImage();
        image = image.getScaledInstance(w, w, Image.SCALE_SMOOTH);
        icon.setImage(image);
        return icon;
    }
}
