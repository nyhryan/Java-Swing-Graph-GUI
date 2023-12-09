package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class KruskalAlgorithm implements IGraphAlgorithm {
    public KruskalAlgorithm(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        nodes = graph.getNodes();
    }

    @Override
    public void run() {
        synchronized (graph) {
            // 초기화(색상, 거리, 방문 기록)
            graph.resetGraphProperties();
            gVisualPanelWrapper.repaint();

            // Sort edges by weight
            ArrayList<GraphEdge> edges = graph.getEdges();
            edges.sort(GraphEdge::compareTo);

            HashMap<GraphEdge, Boolean> chosen = new HashMap<>();
            for (GraphEdge edge : edges) {
                chosen.put(edge, false);
            }

            // Initialize the parent array
            LinkedHashMap<GraphNode, GraphNode> parent = new LinkedHashMap<>();
            for (GraphNode node : nodes) {
                parent.put(node, node);
            }

            // Initialize the result array
            ArrayList<GraphEdge> mst = new ArrayList<>(nodes.size() - 1);

            var editorPane = gVisualPanelWrapper.getgInfoPanel().getEditorPane();
            StringBuilder sb = new StringBuilder();

            // Loop through all edges
            for (GraphEdge edge : edges) {
                sb.setLength(0);
                sb.append("<h1>Kruskal Algorithm</h1><hr/>")
                        .append("<table border=\"1\">")
                        .append("<tr><td>간선</td>")
                        .append("<td>가중치</td>")
                        .append("<td>채택</td></tr>");

                // 간선, 채택 표 그리기
                for (GraphEdge edgeStr : edges) {
                    if (edgeStr.equals(edge))
                        sb.append("<tr style=\"background-color:yellow;\">");
                    else
                        sb.append("<tr>");

                    sb.append(String.format("<td>%s - %s</td><td>%.1f</td>", edgeStr.getFrom().getName(), edgeStr.getTo().getName(), edgeStr.getWeight()));

                    if (chosen.get(edgeStr))
                        sb.append("<td style=\"background-color:#7FFF00;\">&#10003;</td>");
                    else
                        sb.append("<td>&#10007;</td>");

                    sb.append("</tr>");
                }
                editorPane.setText(sb.toString());

                GraphNode u = edge.getFrom();
                GraphNode v = edge.getTo();

                // Find the root of the tree that u belongs to
                GraphNode uRoot = find(parent, u);

                // Find the root of the tree that v belongs to
                GraphNode vRoot = find(parent, v);

                // If they belong to different trees, merge them
                if (uRoot != vRoot) {
                    union(parent, uRoot, vRoot, edge);
                    mst.add(edge);
                    chosen.put(edge, true);
                }
                waitAndRepaint();
            }

            sb.setLength(0);
            sb.append("<h1>Kruskal Algorithm</h1><hr/>")
                    .append("<table border=\"1\">")
                    .append("<tr><td>간선</td>")
                    .append("<td>가중치</td>")
                    .append("<td>채택</td></tr>");

            // 간선, 채택 표 그리기
            for (GraphEdge edgeStr : edges) {
                sb.append(String.format("<td>%s - %s</td><td>%.1f</td>", edgeStr.getFrom().getName(), edgeStr.getTo().getName(), edgeStr.getWeight()));

                if (chosen.get(edgeStr))
                    sb.append("<td style=\"background-color:#7FFF00;\">&#10003;</td>");
                else
                    sb.append("<td>&#10007;</td>");

                sb.append("</tr>");
            }
            editorPane.setText(sb.toString());
            gVisualPanelWrapper.getgInfoPanel().repaint();

            double totalWeight = 0;

            for (GraphEdge edge : mst) {
                edge.setStrokeColor(Color.blue);
                edge.setStrokeWidth(5.0f);
                totalWeight += edge.getWeight();
                waitAndRepaint();
            }

            for (GraphEdge edge : edges) {
                if (!mst.contains(edge)) {
                    edge.setStrokeColor(Color.GRAY);
                    edge.setStrokeWidth(1.0f);
                }
            }
            waitAndRepaint();

            String msg = String.format("<ul><li>최소 신장 트리의 가중치 합: %.1f</li></ul>", totalWeight);
            showMessageDialog(null, String.format("<html>%s</html>", msg), "알고리즘 종료", JOptionPane.INFORMATION_MESSAGE);

            String text = editorPane.getText();
            int startIndex = text.indexOf("<body>");
            int endIndex = text.lastIndexOf("</body>");
            String content = text.substring(startIndex + 6, endIndex);
            editorPane.setText(content + String.format("<h2>탐색결과</h2><hr/>%s", msg));
            gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(false);
        }
    }

    private void waitAndRepaint() {
        try {
            sleep(gVisualPanelWrapper.getgVisualPanel().getAnimationSpeed());
            gVisualPanelWrapper.getgVisualPanel().repaint();
            gVisualPanelWrapper.getgInfoPanel().repaint();
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

    private void union(LinkedHashMap<GraphNode, GraphNode> parent, GraphNode u, GraphNode v, GraphEdge e) {
        GraphNode uRoot = find(parent, u);
        GraphNode vRoot = find(parent, v);

        if (!uRoot.equals(vRoot)) {
            if (uRoot.getFillColor().equals(Color.WHITE) && vRoot.getFillColor().equals(Color.WHITE)) {
                // get random RGB value
                int r = (int) (Math.random() * 256);
                int g = (int) (Math.random() * 256);
                int b = (int) (Math.random() * 256);
                Color color = new Color(r, g, b);
                uRoot.setFillColor(color);
                vRoot.setFillColor(color);
            } else {
                if (uRoot.getFillColor().equals(Color.WHITE))
                    uRoot.setFillColor(vRoot.getFillColor());
                else
                    vRoot.setFillColor(uRoot.getFillColor());

                // make all node's color same that is in the same tree
                for (GraphNode node : nodes) {
                    var root = find(parent, node);
                    if (root.equals(uRoot))
                        node.setFillColor(uRoot.getFillColor());
                    else if (root.equals(vRoot))
                        node.setFillColor(vRoot.getFillColor());
                }
            }
            e.setStrokeColor(vRoot.getFillColor());
            parent.put(uRoot, vRoot);
        }
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final Graph graph;
    private final ArrayList<GraphNode> nodes;
}
