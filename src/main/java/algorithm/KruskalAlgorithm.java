package algorithm;

import components.GVisualPanelWrapper;
import tools.RandomColor;
import graph.GraphEdge;
import graph.GraphNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static javax.swing.JOptionPane.showMessageDialog;

public class KruskalAlgorithm extends GraphAlgorithm {
    public KruskalAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "Kruskal Algorithm".toUpperCase(), listener);
        nodes = graph.getNodes();

        sortedEdges = graph.getEdges();
        sortedEdges.sort(GraphEdge::compareTo);

        for (GraphEdge edge : sortedEdges) {
            chosen.put(edge, false);
        }

        for (GraphNode node : nodes) {
            parent.put(node, node);
        }

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Kruskal Algorithm</h1><hr/>%s", drawSortedEdges(-1)));
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            kruskal();
        }

        double totalWeight = 0.0;

        // MST 그리기
        for (var edge : sortedEdges) {
            if (chosen.get(edge)) {
                edge.setStrokeColor(Color.GREEN);
                edge.setTextColor(Color.BLACK);
                edge.setStrokeWidth(5.0f);
                totalWeight += edge.getWeight();
            }
            else {
                edge.setStrokeColor(Color.GRAY);
                edge.setStrokeWidth(0.1f);
            }
            waitAndRepaint(100);
        }

        showMessageDialog(null, "알고리즘 종료", "알림", JOptionPane.INFORMATION_MESSAGE);

        String msg = String.format("<ul><li>최소 신장 트리의 가중치 합: %.1f</ul>", totalWeight);
        appendMessageToEditorPane(msg);
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());

        listener.onAlgorithmFinished();
    }

    private void kruskal() {
        if (index >= sortedEdges.size()) {
            isCompleted = true;
            return;
        }

        GraphEdge edge = sortedEdges.get(index++);
        GraphNode u = edge.getFrom();
        GraphNode v = edge.getTo();

        GraphNode uRoot = find(parent, u);
        GraphNode vRoot = find(parent, v);

        if (uRoot != vRoot) {
            chosen.put(edge, true);
            union(parent, u, v);
        }

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Kruskal Algorithm</h1><hr/>%s", drawSortedEdges(sortedEdges.indexOf(edge))));

        waitAndRepaint();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private GraphNode find(LinkedHashMap<GraphNode, GraphNode> parent, GraphNode i) {
        if (parent.get(i).equals(i)) {
            return i;
        }
        return find(parent, parent.get(i));
    }

    private void union(LinkedHashMap<GraphNode, GraphNode> parent, GraphNode u, GraphNode v) {
        GraphNode uRoot = find(parent, u);
        GraphNode vRoot = find(parent, v);

        if (!uRoot.equals(vRoot)) {
            if (uRoot.getFillColor().equals(Color.WHITE) && vRoot.getFillColor().equals(Color.WHITE)) {
                Color randColor = RandomColor.getRandomColor();
                Color randTextColor = RandomColor.getCorrectTextColor(randColor);
                uRoot.setFillColor(randColor);
                uRoot.setTextColor(randTextColor);

                vRoot.setFillColor(randColor);
                vRoot.setTextColor(randTextColor);

            } else if (uRoot.getFillColor().equals(Color.WHITE)) {
                uRoot.setFillColor(vRoot.getFillColor());
                uRoot.setTextColor(vRoot.getTextColor());
            } else if (vRoot.getFillColor().equals(Color.WHITE)) {
                vRoot.setFillColor(uRoot.getFillColor());
                vRoot.setTextColor(uRoot.getTextColor());
            }

            parent.put(uRoot, vRoot);

            // make all node's color same that is in the same tree
            for (GraphNode node : nodes) {
                var root = find(parent, node);
                if (root.equals(uRoot)) {
                    node.setFillColor(uRoot.getFillColor());
                    node.setTextColor(uRoot.getTextColor());
                } else if (root.equals(vRoot)) {
                    node.setFillColor(vRoot.getFillColor());
                    node.setTextColor(vRoot.getTextColor());
                }
            }

            // make all edge's color same that is in the same tree
            for (GraphEdge edge : graph.getEdges()) {
                boolean isChosenEdge = false;
                for (var _e : chosen.keySet()) {
                    if (chosen.get(_e) && _e.equals(edge)) {
                        isChosenEdge = true;
                        break;
                    }
                }
                if (!isChosenEdge) {
                    continue;
                }

                var _u = edge.getFrom();
                var _v = edge.getTo();
                var uRoot2 = find(parent, _u);
                var vRoot2 = find(parent, _v);
                if (uRoot2.equals(vRoot2)) {
                    edge.setStrokeColor(uRoot2.getFillColor());
                    edge.setTextColor(uRoot2.getTextColor());
                    edge.setStrokeWidth(5.0f);

                    var reversedEdge = graph.getEdge(_v, _u);
                    reversedEdge.setStrokeColor(uRoot2.getFillColor());
                    reversedEdge.setTextColor(uRoot2.getTextColor());
                    reversedEdge.setStrokeWidth(5.0f);
                }
            }
        }
    }

    private String drawSortedEdges(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\">")
                .append("<tr><td>간선</td>")
                .append("<td>가중치</td>")
                .append("<td>채택</td>")
                .append("<td>현재</td></tr>");

        for (GraphEdge edge : sortedEdges) {
            sb.append("<tr>");
            sb.append(String.format("<td>%s - %s</td><td>%.1f</td>", edge.getFrom().getName(), edge.getTo().getName(), edge.getWeight()));

            if (chosen.get(edge))
                sb.append("<td style=\"background-color:#7FFF00;\">&#10003;</td>");
            else
                sb.append("<td style=\"background-color:#FF4646;\">&#10007;</td>");

            if (sortedEdges.indexOf(edge) == i)
                sb.append("<td style=\"background-color:#FFFFFF\">&#9754;</td>");
            else
                sb.append("<td style=\"background-color:#FFFFFF\">&nbsp;</td>");

            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private final ArrayList<GraphNode> nodes;
    private final ArrayList<GraphEdge> sortedEdges;
    private final HashMap<GraphEdge, Boolean> chosen = new HashMap<>();
    private final LinkedHashMap<GraphNode, GraphNode> parent = new LinkedHashMap<>();
    private int index = 0;
}
