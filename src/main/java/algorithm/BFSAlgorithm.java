package algorithm;

import components.GVisualPanelWrapper;
import graph.Graph;
import graph.GraphNode;

import javax.swing.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

import static javax.swing.JOptionPane.showMessageDialog;

public class BFSAlgorithm extends GraphAlgorithm {
    public BFSAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "BFS Algorithm".toUpperCase(), listener);
        this.graph = gVisualPanelWrapper.getgVisualPanel().getGraph();

        startNode.setVisited(true);
        queue.offer(startNode);
        vistedNodes.add(startNode);

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>너비 우선 탐색</h1><hr/>%s", drawQueue(false, null)));
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            bfs();
        }

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>너비 우선 탐색</h1><hr/><ul>%s</ul><hr/>%s",
                         DFSAlgorithm.vistedNodesString(vistedNodes), drawQueue(false, null)));
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());
        showMessageDialog(null, "알고리즘 종료", "알림", JOptionPane.INFORMATION_MESSAGE);
        listener.onAlgorithmFinished();
    }

    private void bfs() {
        if (queue.isEmpty()) {
            isCompleted = true;
            return;
        }
        if (isCompleted) {
            return;
        }

        GraphNode node = queue.poll();
        node.setFillColor(COLOR_1);
        node.setTextColor(COLOR_1_TEXT);
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>너비 우선 탐색</h1><hr/><ul><li>현재 노드: %s</li>%s</ul><hr/>%s",
                        node, DFSAlgorithm.vistedNodesString(vistedNodes), drawQueue(false, null)));

        waitAndRepaint();

        boolean isOffered = false;
        for (var edge : graph.getAdjacencyList().get(graph.getNodes().indexOf(node))) {
            var adjacentNode = edge.getTo();

            if (adjacentNode.isNotVisited()) {
                adjacentNode.setVisited(true);
                adjacentNode.setFillColor(COLOR_2);
                adjacentNode.setTextColor(COLOR_2_TEXT);
                queue.offer(adjacentNode);
                isOffered = true;
                vistedNodes.add(adjacentNode);
                waitAndRepaint();
            }
            gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                    String.format("<h1>너비 우선 탐색</h1><hr/><ul><li>현재 노드: %s</li>%s</ul><hr/>%s",
                            node, DFSAlgorithm.vistedNodesString(vistedNodes), drawQueue(isOffered, adjacentNode)));
            SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        node.setFillColor(COLOR_2);
        node.setTextColor(COLOR_2_TEXT);
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgVisualPanel().repaint());

        if (vistedNodes.size() == graph.getNodes().size()) {
            isCompleted = true;
        }
    }

    private String drawQueue(boolean isOffered, GraphNode offeredNode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>큐</h2>")
                .append("<table border=\"1\">");

        sb.append("<tr><td>←</td>");
        for (GraphNode node : queue) {
            if (isOffered && node.equals(offeredNode)) {
                sb.append(String.format("<td style=\"background-color:#7FFF00;\">%s</td>", node));
            }
            else {
                sb.append(String.format("<td>%s</td>", node));
            }
        }
        sb.append("<td>←</td></tr>");

        sb.append("</table>");
        return sb.toString();
    }

    private final Graph graph;

    private final LinkedHashSet<GraphNode> vistedNodes = new LinkedHashSet<>();
    private final Queue<GraphNode> queue = new LinkedList<>();
}
