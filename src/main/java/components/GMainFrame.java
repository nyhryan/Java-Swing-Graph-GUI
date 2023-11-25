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

        GVisualPanelWrapper gVisualPanelWrapper = new GVisualPanelWrapper();
        setJMenuBar(new GMenuBar(gVisualPanelWrapper));

        // 현재 메인 프레임에 서브 패널들을 추가한다.
        add(gVisualPanelWrapper);
    }

   private final GQuizPanel gQuizPanel = new GQuizPanel();
}
