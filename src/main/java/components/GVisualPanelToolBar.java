package components;

import tools.ImageIconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class GVisualPanelToolBar extends JToolBar {
    public GVisualPanelToolBar(GVisualPanelWrapper gVisualPanelWrapper) {
        gVisualPanel = gVisualPanelWrapper.getgVisualPanel();

        setFloatable(false);
        setMargin(new Insets(1, 1, 1, 1));

        JLabel currentModeLabel = new JLabel("현재 모드: 선택");

        ImageIcon addNodeIcon = ImageIconLoader.getImageIcon("/node.png", 16);
        JButton addNodeButton = addToolBarButton("노드 추가/제거", "좌 더블클릭으로 노드 추가, 우클릭으로 노드 제거", e -> {
            gVisualPanel.setMode(GVisualPanel.Mode.NODE_MODE);
            currentModeLabel.setText("모드: 노드 추가/제거");
        });
        addNodeButton.setIcon(addNodeIcon);
        add(addNodeButton);

        ImageIcon addEdgeIcon = ImageIconLoader.getImageIcon("/edge.png", 16);
        JButton addEdgeButton = addToolBarButton("간선 추가/제거", "한 노드를 클릭하고 다른 노드로 드래그해서 간선 추가. 가중치로 0을 입력하면 간선 제거",
                e -> {
                    gVisualPanel.setMode(GVisualPanel.Mode.EDGE_MODE);
                    currentModeLabel.setText("모드: 간선 추가/제거");
                });
        addEdgeButton.setIcon(addEdgeIcon);
        add(addEdgeButton);

        ImageIcon moveIcon = ImageIconLoader.getImageIcon("/move.png", 16);
        JButton moveBtn = addToolBarButton("이동", "노드를 클릭해서 드래그해서 이동", e -> {
            gVisualPanel.setMode(GVisualPanel.Mode.MOVE);
            currentModeLabel.setText("모드: 이동");
        });
        moveBtn.setIcon(moveIcon);
        add(moveBtn);

        // 현재 선택된 모드 라벨
        addSeparator();
        add(currentModeLabel);

        addSeparator();
        ImageIcon resetIcon = ImageIconLoader.getImageIcon("/reset.png", 16);
        JButton resetBtn = addToolBarButton("그래프 초기화", "그래프를 초기화합니다.", e -> {
            gVisualPanel.setMode(GVisualPanel.Mode.DEFAULT);
            gVisualPanel.resetGraph();
        });
        resetBtn.setIcon(resetIcon);
        add(resetBtn);

        ImageIcon emptyIcon = ImageIconLoader.getImageIcon("/empty.png", 16);
        JButton emptyBtn = addToolBarButton("화면 지우기", "화면을 지웁니다.", e -> {
            gVisualPanel.setMode(GVisualPanel.Mode.DEFAULT);
            gVisualPanel.setEmptyGraph();
        });
        emptyBtn.setIcon(emptyIcon);
        add(emptyBtn);

        // 퀴즈 모드로 이동 버튼
        addSeparator();
        JButton quizModeBtn = addToolBarButton("Quiz Panel로 이동", "Quiz Panel로 이동", e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gVisualPanel);
            frame.setJMenuBar(null);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new GQuizPanel());
            frame.revalidate();
            frame.repaint();
        });

        // place quizModeBtn to the right
        add(Box.createHorizontalGlue());
        add(quizModeBtn);
    }

    /**
     * 툴바에 버튼을 추가하는 메소드
     *
     * @param name     버튼에 표시될 텍스트
     * @param listener 버튼에 추가할 리스너
     * @return 툴바에 추가할 버튼 객체
     */
    private JButton addToolBarButton(String name, String toolTip, ActionListener listener) {
        JButton button = new JButton(name);
        button.setToolTipText(toolTip);
        button.addActionListener(listener);
        return button;
    }

    private final GVisualPanel gVisualPanel;
}