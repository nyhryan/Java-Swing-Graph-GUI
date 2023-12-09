package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class PrimAlgorithm implements IGraphAlgorithm {
    public PrimAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        this.startNode = startNode;
        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        nodes = graph.getNodes();
    }

    @Override
    public void run() {
        synchronized (graph) {
            var adjacencyList = graph.getAdjacencyList();
            var editorPane = gVisualPanelWrapper.getgInfoPanel().getEditorPane();

            graph.resetGraphProperties();
            gVisualPanelWrapper.repaint();

            StringBuilder sb = new StringBuilder();


            for (GraphNode node : nodes) {
                node.setVisited(false);
            }

            var visitedEdges = new HashMap<GraphEdge, Boolean>();
            for (var edge : graph.getEdges()) {
                visitedEdges.put(edge, false);
            }

            startNode.setVisited(true);
            startNode.setFillColor(Color.RED);
            waitAndRepaint();
            int visitedNodes = 1;

            double totalWeight = 0.0;

            while (visitedNodes < nodes.size()) {
                sb.setLength(0);
                sb.append("<h1>Prim Algorithm</h1><hr/>")
                        .append("<table border=\"1\">")
                        .append("<tr><td>간선</td>")
                        .append("<td>가중치</td>")
                        .append("<td>채택</td></tr>");

                // 간선, 채택 표 그리기
                for (GraphEdge edge : graph.getEdges()) {
                    sb.append(String.format("<td>%s - %s</td><td>%.1f</td>", edge.getFrom().getName(), edge.getTo().getName(), edge.getWeight()));

                    if (visitedEdges.get(edge))
                        sb.append("<td style=\"background-color:#7FFF00;\">&#10003;</td>");
                    else
                        sb.append("<td>&#10007;</td>");

                    sb.append("</tr>");
                }
                editorPane.setText(sb.toString());

                GraphEdge minEdge = null;
                double minWeight = Double.POSITIVE_INFINITY;

                for (GraphNode node : nodes) {
                    if (node.isVisited()) {
                        for (GraphEdge edge : adjacencyList.get(nodes.indexOf(node))) {
                            if (!edge.getTo().isVisited() && edge.getWeight() < minWeight) {
                                minEdge = edge;
                                minWeight = edge.getWeight();
                            }
                        }
                    }
                }

                if (minEdge != null) {
                    minEdge.getTo().setVisited(true);
                    GraphEdge reverseEdge = graph.getEdge(minEdge.getTo(), minEdge.getFrom());

                    if (visitedEdges.containsKey(minEdge)) {
                        visitedEdges.put(minEdge, true);
                    } else if (visitedEdges.containsKey(reverseEdge)) {
                        visitedEdges.put(reverseEdge, true);
                    } else {
                        throw new RuntimeException("Edge not found!");
                    }

                    // MST의 간선을 색칠
                    minEdge.setStrokeColor(Color.RED);
                    minEdge.setStrokeWidth(5.0f);
                    // color the reverse edge into red too
                    reverseEdge.setStrokeColor(Color.RED);
                    reverseEdge.setStrokeWidth(5.0f);

                    gVisualPanelWrapper.getgInfoPanel().getEditorPane().setText(sb.toString());
                    waitAndRepaint();

                    // MST의 노드를 색칠
                    minEdge.getTo().setFillColor(Color.RED);
                    waitAndRepaint();

                    visitedNodes++;
                    totalWeight += minEdge.getWeight();
                } else {
                    break;
                }
            }

            // color the edges that are not in the MST into gray
            for (var edge : graph.getEdges()) {
                if (edge.getStrokeColor() != Color.RED) {
                    edge.setStrokeColor(Color.GRAY);
                }
            }
            gVisualPanelWrapper.getgVisualPanel().repaint();

            String msg = String.format("<ul><li>%s부터 시작한 최소 신장 트리의 가중치 합: %.1f</li></ul>", startNode, totalWeight);
            showMessageDialog(null, String.format("<html>%s</html>", msg), "알고리즘 종료", JOptionPane.INFORMATION_MESSAGE);

            String text = editorPane.getText();
            int startIndex = text.indexOf("<body>");
            int endIndex = text.lastIndexOf("</body>");
            String content = text.substring(startIndex + 6, endIndex);
            editorPane.setText(content + String.format("<h2>탐색결과</h2><hr/>%s", msg));
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

    private final GraphNode startNode;
    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final Graph graph;
    private final ArrayList<GraphNode> nodes;
}
