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
        setSize(new Dimension(800, 600));

        setLocationRelativeTo(null); // 화면 중앙에 프로그램을 배치

        // 현재 메인 프레임에 서브 패널들을 추가한다.
        setLayout(new CardLayout());
        add("Graph Mode", new GVisualPanelWrapper());
//        add("Quiz Mode", gQuizPanel);
    }

   private GQuizPanel gQuizPanel = new GQuizPanel();
}
