package algorithm;

import components.GVisualPanelWrapper;
import graph.Graph;
import java.awt.*;
import static java.lang.Thread.sleep;

public class TestAlgorithm implements IGraphAlgorithm {
    public TestAlgorithm(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
    }

    // 알고리즘을 실행하는 메소드
    public void run() {
        traverseNodes();
    }

    // 1초 간격으로 순회하며 노드를 방문하는 메소드
    private synchronized void traverseNodes() {
        for (var node : graph.getNodes()) {
            try {
                sleep(1000);
                gVisualPanelWrapper.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            node.setFillColor(Color.GREEN);
            System.out.println(node.getName());
        }
    }

    private final Graph graph;
    private final GVisualPanelWrapper gVisualPanelWrapper;
}
