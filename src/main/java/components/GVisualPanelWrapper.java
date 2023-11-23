package components;

import javax.swing.*;
import java.awt.*;

public class GVisualPanelWrapper extends JPanel {
    public GVisualPanelWrapper() {
        // 이 둘 순서 바뀌면 프로그램이 정상 작동하지 않음. 주의!
        // gInfoPanel은 gVisualPanel의 정보에 의존하기 떄문에 순서를 지켜야한다.
        gVisualPanel = new GVisualPanel(this);
        gInfoPanel = new GInfoPanel(this);

        setLayout(new BorderLayout());

        // 화면 상단에 툴바를 추가한다.
        JToolBar toolBar = new GVisualPanelToolBar(gVisualPanel);
        add(toolBar, BorderLayout.NORTH);

        // 그래프를 그리는 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(gVisualPanel);
        gVisualPanel.setPreferredSize(new Dimension(2000, 2000));

        // 스플릿 패널에 그래프와 그래프 정보를 그리는 패널을 추가한다.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, gInfoPanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.7);
        splitPane.setOneTouchExpandable(true);

        // splitPane의 divider가 움직이면 발생하는 이벤트 리스너 추가
        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
            gVisualPanel.repaint();
            gInfoPanel.repaint();
        });

        // 스플릿 패널을 현재 패널에 추가한다.
        add(splitPane, BorderLayout.CENTER);

        // 화면을 더블클릭한 위치에 새로운 노드 추가
        gVisualPanel.addMouseListener(new GVisualPanelMouseAdapter(this));
        gVisualPanel.addMouseMotionListener(new GVisualPanelMouseAdapter(this));

    }

    public GVisualPanel getgVisualPanel() {
        return gVisualPanel;
    }

    public GInfoPanel getgInfoPanel() {
        return gInfoPanel;
    }

    // 그래프를 그리는 패널
    private final GVisualPanel gVisualPanel;
    private final GInfoPanel gInfoPanel;
}
