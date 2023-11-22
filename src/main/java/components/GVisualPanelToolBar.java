package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class GVisualPanelToolBar extends JToolBar {

    /**
     * 툴바 생성자
     *
     * @param visualPanelWrapper 툴바를 포함하는 GVisualPanelWrapper 패널
     */

    public GVisualPanelToolBar(GVisualPanelWrapper visualPanelWrapper) {
        this.visualPanelWrapper = visualPanelWrapper;
        this.parentPanel = visualPanelWrapper.getgVisualPanel();

        setFloatable(false);
        setMargin(new Insets(1, 1, 1, 1));

        JLabel currentModeLabel = new JLabel("모드 선택");
        currentModeLabel.setFont(parentPanel.getFont());

        add(addToolBarButton("노드 추가/제거", "좌 더블클릭으로 노드 추가, 우클릭으로 노드 제거", e -> {
            parentPanel.setMode(GVisualPanel.Mode.NODE_MODE);
            currentModeLabel.setText("모드: 노드 추가/제거");
        }));
        add(addToolBarButton("간선 추가/제거", "한 노드를 클릭하고 다른 노드로 드래그해서 간선 추가. 가중치로 0을 입력하면 간선 제거",
                e -> {
                    parentPanel.setMode(GVisualPanel.Mode.EDGE_MODE);
                    currentModeLabel.setText("모드: 간선 추가/제거");
                }));
        add(addToolBarButton("이동", "노드를 클릭해서 드래그해서 이동", e -> {
            parentPanel.setMode(GVisualPanel.Mode.MOVE);
            currentModeLabel.setText("모드: 이동");
        }));

        // 현재 선택된 모드 라벨
        addSeparator();
        add(currentModeLabel);

        // "Quiz Panel로 이동" 버튼 추가
        addSeparator();
        addQuizPanelButton();
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
        button.setFont(parentPanel.getFont());
        button.addActionListener(listener);
        return button;
    }

    private void addQuizPanelButton() {
        JButton quizPanelButton = new JButton("Quiz Panel로 이동");
        quizPanelButton.setToolTipText("Quiz Panel로 이동");
        quizPanelButton.setFont(parentPanel.getFont());
        quizPanelButton.addActionListener(e -> moveToQuizPanel());
        add(quizPanelButton);
    }

    private void moveToQuizPanel() {
        GQuizPanel quizPanel = new GQuizPanel();
        visualPanelWrapper.removeAll();
        visualPanelWrapper.add(quizPanel, BorderLayout.CENTER);
        visualPanelWrapper.revalidate();
        visualPanelWrapper.repaint();
    }

    private final GVisualPanel parentPanel;
    private final GVisualPanelWrapper visualPanelWrapper;
}
