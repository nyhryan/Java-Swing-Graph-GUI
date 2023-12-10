package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class PrimAlgorithm extends GraphAlgorithm {
    public PrimAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "Kruskal Algorithm".toUpperCase(), listener);
        this.startNode = startNode;
        nodes = graph.getNodes();

        for (var node : nodes) {
            node.setVisited(false);
        }

        mst = new ArrayList<>(nodes.size() - 1);

        startNode.setVisited(true);
        startNode.setFillColor(Color.GREEN);
        gVisualPanelWrapper.getgVisualPanel().repaint();
        mst.add(startNode);
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            try {
                semaphore.acquire();
                prim();
                semaphore.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        double totalWeight = 0.0;
        for (var edge : graph.getEdges()) {
            if (edge.getStrokeColor() == COLOR_1) {
                totalWeight += edge.getWeight();
                edge.setStrokeColor(Color.GREEN);
                edge.setTextColor(Color.BLACK);
                edge.setStrokeWidth(3.0f);
            } else {
                edge.setStrokeColor(Color.GRAY);
                edge.setStrokeWidth(0.1f);
            }
            waitAndRepaint();
        }
        String msg = String.format("<ul><li>최소 신장 트리의 가중치 합: %.1f</li></ul>", totalWeight);
        showMessageDialog(null, String.format("<html>%s</html>", msg), "알고리즘 종료", JOptionPane.INFORMATION_MESSAGE);

        String text = gVisualPanelWrapper.getgInfoPanel().getEditorPane().getText();
        int startIndex = text.indexOf("<body>");
        int endIndex = text.lastIndexOf("</body>");
        String content = text.substring(startIndex + 6, endIndex);
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(content + String.format("<h2>탐색결과</h2><hr/>%s", msg));

        listener.onAlgorithmFinished();
    }

    private void prim() {
        if (mst.size() == nodes.size()) {
            isCompleted = true;
            return;
        }

        GraphEdge minEdge = null;
        double minWeight = Double.POSITIVE_INFINITY;

        for (var node : mst) {
            for (var edge : graph.getAdjacencyList().get(nodes.indexOf(node))) {
                if (!mst.contains(edge.getTo()) && edge.getWeight() < minWeight) {
                    minEdge = edge;
                    minWeight = edge.getWeight();
                }
            }
        }

        if (minEdge != null) {
            minEdge.getTo().setVisited(true);
            mst.add(minEdge.getTo());
            minEdge.getTo().setFillColor(COLOR_1);
            minEdge.getTo().setTextColor(COLOR_1_TEXT);

            minEdge.setStrokeWidth(3.0f);
            minEdge.setStrokeColor(COLOR_1);
            minEdge.setTextColor(COLOR_1_TEXT);
            var reversedEdge = graph.getEdge(minEdge.getTo(), minEdge.getFrom());
            reversedEdge.setStrokeWidth(3.0f);
            reversedEdge.setStrokeColor(COLOR_1);
            reversedEdge.setTextColor(COLOR_1_TEXT);
        }

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Prim Algorithm</h1><hr/><ul><li>현재 노드: %s</li>%s</ul>", startNode, drawMstNodes()));

        waitAndRepaint();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String drawMstNodes() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\">")
                .append("<tr><td>노드</td>")
                .append("<td>채택</td></tr>");

        for (var node : nodes) {
            sb.append(String.format("<tr><td>%s</td>", node));

            if (mst.contains(node)) {
                sb.append("<td style=\"background-color:#7FFF00;\">&#10003;</td>");
            } else {
                sb.append("<td>&#10007;</td>");
            }

            sb.append("</tr>");
        }

        return sb.toString();
    }

    private final GraphNode startNode;
    private final ArrayList<GraphNode> nodes;
    private final ArrayList<GraphNode> mst;
}