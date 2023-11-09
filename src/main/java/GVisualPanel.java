import algorithm.AlgorithmRunner;
import algorithm.FloydAlgorithm;
import graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 그래프(노드, 간선)를 그리는 패널
 */
public class GVisualPanel extends JPanel {
    public GVisualPanel() {
        setBackground(Color.GRAY);

        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        JLabel currentModeLabel = new JLabel("모드 선택");

        toolBar.add(addToolBarButton("노드 추가", e -> {
            mode = Mode.ADD_NODE;
            currentModeLabel.setText("노드 추가");
        }));
        toolBar.add(addToolBarButton("간선 추가", e -> {
            mode = Mode.ADD_EDGE;
            currentModeLabel.setText("간선 추가");
        }));
        toolBar.add(addToolBarButton("삭제", e -> {
            mode = Mode.REMOVE;
            currentModeLabel.setText("삭제");
        }));
        toolBar.addSeparator();
        toolBar.add(currentModeLabel);

        add(toolBar, BorderLayout.NORTH);

        // 화면을 더블클릭한 위치에 새로운 노드 추가
        addMouseListener(new DoubleClickToAddNode(graph));

//        algorithmRunner = new AlgorithmRunner(new FloydAlgorithm());
//        SwingUtilities.invokeLater(() -> {
//            algorithmRunner.run();
//            repaint();
//        });
    }

    private JButton addToolBarButton(String name, ActionListener listener) {
        JButton button = new JButton(name);
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.addActionListener(listener);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 더 나은 Graphic2D를 사용함.
        Graphics2D g2d = (Graphics2D) g;

        // Anti-aliasing 활성화
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);


        var nodes = graph.getNodes();

        // 그래프의 노드가 1개 이상이면 화면에 그린다.
        if (!nodes.isEmpty()) {
            // 모든 노드들을 순회하며 화면에 원과 노드 번호 그리기
            for (var node : nodes) {
                String name = node.getName();
                int currentIndex = nodes.indexOf(node);
                int x = node.getX();
                int y = node.getY();

                // 간선을 먼저 그리고
                if (nodes.indexOf(node) != nodes.size() - 1 && nodes.get(currentIndex + 1) != null) {
                    g2d.drawLine(
                            x + NODE_RADIUS / 2,
                            y + NODE_RADIUS / 2,
                            nodes.get(currentIndex + 1).getX() + NODE_RADIUS / 2,
                            nodes.get(currentIndex + 1).getY() + NODE_RADIUS / 2);
                }

                // 노드를 그린다.
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x, y, NODE_RADIUS, NODE_RADIUS);
                g2d.drawRect(x, y, NODE_RADIUS, NODE_RADIUS);

                // 노드 중앙에 이름을 그린다.
                String nameToDraw = name;
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 20));

                // 이름이 3글자 이상이면 세글자 뒤는 ...으로 표시
                if (name.length() > 3) {
                    nameToDraw = name.substring(0, 3) + "...";
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                }
                int width = g2d.getFontMetrics().stringWidth(nameToDraw);
                int height = g2d.getFontMetrics().getHeight();
                g2d.drawString(nameToDraw, x + (NODE_RADIUS - width) / 2, y + NODE_RADIUS / 2 + height / 4);
            }
        }
    }

    // 그래프 객체
    private Graph graph = new Graph();
    private static final int NODE_RADIUS = 50;

    public enum Mode {ADD_NODE, ADD_EDGE, REMOVE, DEFAULT};

    public Mode getMode() {
        return mode;
    }

    private Mode mode = Mode.DEFAULT;
//    private AlgorithmRunner algorithmRunner;
}

class DoubleClickToAddNode extends MouseAdapter {
    /**
     * 더블클릭으로 노드 추가하는 리스너 생성자
     * @param graph 그래프 객체 레퍼런스
     */
    public DoubleClickToAddNode(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();

        // 노드 추가 모드 + 왼쪽 더블클릭 한 경우에만 리스너 수행
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 &&
                currentMode == GVisualPanel.Mode.ADD_NODE) {
            String nodeName = "";
            // Get nodeName at least two letters long from JOptionPane.showInputDialog, or show error dialog
            while (nodeName.length() < 2) {
                nodeName = JOptionPane.showInputDialog("새 노드 이름을 입력하세요.");
                if (nodeName.length() < 2) {
                    JOptionPane.showMessageDialog(null, "노드 이름은 최소 2글자 이상이어야 합니다.");
                }
            }

            GraphNode newNode = new GraphNode(nodeName);
            newNode.setX(e.getX());
            newNode.setY(e.getY());
            graph.addNode(newNode);

            e.getComponent().repaint();
        }
    }


    private final Graph graph;
}