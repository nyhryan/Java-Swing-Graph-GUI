import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import components.GMainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class MainProgram {
    public static void main(String[] args) {
        // 스윙 전용 스레드(EDT)에서 그래프 프로그램을 시작
        SwingUtilities.invokeLater(() -> {
            try {
                // UI 테마 설정
                UIManager.setLookAndFeel(new FlatCyanLightIJTheme());

                // UI 폰트 설정
                InputStream is = MainProgram.class.getResourceAsStream("/NotoSansKR-Regular.ttf");
                if (is != null) {
                    Font notoSansKR = Font.createFont(Font.TRUETYPE_FONT, is);
                    UIManager.put("defaultFont", notoSansKR.deriveFont(12f));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            UIManager.put("Component.arrowType", "chevron");
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 12);


            // 메인 프레임 생성
            new GMainFrame().setVisible(true);
        });
    }
}
