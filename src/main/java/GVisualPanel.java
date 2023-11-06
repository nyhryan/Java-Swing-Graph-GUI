import algorithm.AlgorithmRunner;
import algorithm.FloydAlgorithm;
import algorithm.Graph;

import javax.swing.*;
import java.awt.*;

/**
 * 그래프(노드, 간선)를 그리는 패널
 */
public class GVisualPanel extends JPanel {
    public GVisualPanel() {
        setBackground(Color.GRAY);

        graph.addNode(0);
        graph.addNode(1);

        algorithmRunner = new AlgorithmRunner(new FloydAlgorithm());
        SwingUtilities.invokeLater(() -> {
            algorithmRunner.run();
            repaint();
        });
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
            int offset = 100;
            int x = 50, y = 50;
            // 모든 노드들을 순회하며 화면에 원과 노드 번호 그리기
            for (var node : nodes) {
                int index = node.getNodeNumber();

                if (nodes.indexOf(node) != nodes.size() - 1) {
                    g2d.drawLine(x + 25, y + 25, x + offset + 25, y + offset + 25);
                }

                g2d.setColor(Color.WHITE);
                g2d.fillOval(x, y, 50, 50);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 20));
                g2d.drawString(Integer.toString(index), x + 20, y + 35);

                x += offset;
                y += offset;
            }
        }
    }

    // 그래프 객체
    private Graph graph = new Graph();
    private AlgorithmRunner algorithmRunner;

}
