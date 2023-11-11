import javax.swing.*;
import java.awt.*;
import javax.swing.UIManager.*;

/**
 * GUI 프로그램의 메인 프레임
 */
public class GMainFrame extends JFrame {
    public GMainFrame() {
        setTitle("그래프 프로그램");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setSize(new Dimension(800, 600));
        setLayout(new CardLayout());

        setLocationRelativeTo(null); // 화면 중앙에 프로그램을 배치

        // 현재 메인 프레임에 서브 패널들을 추가한다.
        add("Algorithm.Graph Mode", gVisualPanel);
        add("Quiz Mode", gQuizPanel);

    }

    // 그래프를 그리는 서브 패널
    private GVisualPanel gVisualPanel = new GVisualPanel();

    private GQuizPanel gQuizPanel = new GQuizPanel();
}
