package components;

import javax.swing.*;
import java.awt.*;

/**
 * GUI 프로그램의 메인 프레임
 */
public class GMainFrame extends JFrame {
    public GMainFrame() {
        setTitle("그래프 프로그램");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setSize(new Dimension(1280, 960));
        setLocationRelativeTo(null); // 화면 중앙에 프로그램을 배치

        var appIcon = ImageIconLoader.getImageIcon("/icon.png", 16);
        setIconImage(appIcon.getImage());

        GVisualPanelWrapper gVisualPanelWrapper = new GVisualPanelWrapper();
        // 메뉴바를 추가한다.
        setJMenuBar(new GMenuBar(gVisualPanelWrapper));

        // gVisualPanelWrapper <-> 퀴즈패널은 각 패널 안에서 이동가능함.
        add(gVisualPanelWrapper);
    }
}
