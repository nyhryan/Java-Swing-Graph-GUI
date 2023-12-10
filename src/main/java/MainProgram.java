import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import components.GMainFrame;

import javax.swing.*;
import java.awt.*;

public class MainProgram {
    public static void main(String[] args) {
        // 스윙 전용 스레드(EDT)에서 그래프 프로그램을 시작
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatCyanLightIJTheme());
                var is = MainProgram.class.getResourceAsStream("/NotoSansKR-Regular.ttf");
                assert is != null;
                Font notoSansKR = Font.createFont(Font.TRUETYPE_FONT, is);
                UIManager.put("defaultFont", notoSansKR.deriveFont(12f));
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GMainFrame().setVisible(true);
        });
    }
}
