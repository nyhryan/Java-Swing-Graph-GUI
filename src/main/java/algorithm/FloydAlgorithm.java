package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class FloydAlgorithm implements IGraphAlgorithm {
    public FloydAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, GraphNode endNode) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        adjacencyList = graph.getAdjacencyList();
        nodes = graph.getNodes();
        this.startNode = startNode;
        this.endNode = endNode;
    }

    @Override
    public void run() {
        synchronized (graph) {
            // 초기화(색상, 거리, 방문 기록)
            graph.resetGraphProperties();
            gVisualPanelWrapper.repaint();

            double[][] distance = new double[nodes.size()][nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = 0; j < nodes.size(); j++) {
                    distance[i][j] = Double.POSITIVE_INFINITY;
                }
            }

            GraphNode[][] prev = new GraphNode[nodes.size()][nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = 0; j < nodes.size(); j++) {
                    prev[i][j] = null;
                }
            }
            for (var edges : graph.getAdjacencyList()) {
                for (var edge : edges) {
                    int u = nodes.indexOf(edge.getFrom());
                    int v = nodes.indexOf(edge.getTo());
                    distance[u][v] = edge.getWeight();
                    prev[u][v] = edge.getFrom();
                }
            }
            for (var node : nodes) {
                int v = nodes.indexOf(node);
                distance[v][v] = 0;
                prev[v][v] = node;
            }

            var editorPane = gVisualPanelWrapper.getgInfoPanel().getEditorPane();

            String disatnceMatrixString = null;
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < nodes.size(); k++) {
                sb.setLength(0);
                sb.append("<h1>Floyd Algorithm</h1><hr/>")
                        .append("<h2>distance matrix</h2>")
                        .append("<table><thead><tr>")
                        .append("<th>노드</th>");

                for (int i = 0; i < nodes.size(); i++) {
                    GraphNode node = nodes.get(i);
                    if (i == k)
                        sb.append(String.format("<th style=\"background-color:yellow;\">%s</th>", node.getName()));
                    else
                        sb.append(String.format("<th>%s</th>", node.getName()));
                }
                sb.append("</tr></thead>")
                        .append("<tbody>");

                for (int i = 0; i < nodes.size(); i++) {
                    sb.append(String.format("<tr><td>%s</td>", nodes.get(i).getName()));

                    for (int j = 0; j < nodes.size(); j++) {
                        if (distance[i][j] > distance[i][k] + distance[k][j]) {
                            distance[i][j] = distance[i][k] + distance[k][j];
                            prev[i][j] = prev[k][j];
                            String distanceStr = String.format("%.1f", distance[i][j]);
                            sb.append(String.format("<td style=\"background-color:red;\">%s</td>", distanceStr));
                        } else {
                            String distanceStr = distance[i][j] == Double.POSITIVE_INFINITY ? "∞" : String.format("%.1f", distance[i][j]);
                            sb.append(String.format("<td>%s</td>", distanceStr));
                        }
                    }
                    sb.append("</tr>");
                }
                sb.append("</tbody></table>");

                editorPane.setText(sb.toString());
                disatnceMatrixString = sb.toString();
                waitAndRepaint();
            }

            // append prev matrix
            sb.setLength(0);
            sb.append("<h2>prev matrix</h2>")
                    .append("<table><thead><tr>")
                    .append("<th>노드</th>");

            for (GraphNode node : nodes)
                sb.append(String.format("<th>%s</th>", node.getName()));

            sb.append("</tr></thead>")
                    .append("<tbody>");

            for (int i = 0; i < nodes.size(); i++) {
                sb.append(String.format("<tr><td>%s</td>", nodes.get(i).getName()));

                for (int j = 0; j < nodes.size(); j++) {
                    if (prev[i][j] == null) {
                        sb.append("<td>NULL</td>");
                    } else {
                        sb.append(String.format("<td>%s</td>", prev[i][j].getName()));
                    }
                }
                sb.append("</tr>");
            }
            sb.append("</tbody></table>");
            editorPane.setText(disatnceMatrixString + sb);
            waitAndRepaint();

            // 최단 경로 탐색
            GraphNode node = endNode;
            ArrayList<GraphNode> path = new ArrayList<>();
            path.add(node);
            while (node != null) {
                node.setFillColor(Color.GREEN);

                GraphNode prevNode = prev[nodes.indexOf(startNode)][nodes.indexOf(node)];
                GraphEdge edge = null;
                if (prevNode != null) {
                    for (GraphEdge e : adjacencyList.get(nodes.indexOf(prevNode))) {
                        if (e.getTo().equals(node)) {
                            e.setColor(Color.BLUE);
                            e.setStroke(new BasicStroke(5));
                            edge = e;
                            break;
                        }
                    }
                }

                if (edge == null) break;
                for (GraphEdge e : adjacencyList.get(nodes.indexOf(edge.getTo()))) {
                    if (e.getTo().equals(prevNode)) {
                        e.setColor(Color.BLUE);
                        e.setStroke(new BasicStroke(5));
                        break;
                    }
                }

                node = prev[nodes.indexOf(startNode)][nodes.indexOf(node)];
                path.add(node);
                waitAndRepaint();
            }

            Collections.reverse(path);
            StringBuilder route = new StringBuilder();
            for (GraphNode n : path) {
                route.append(n.getName());
                if (!n.equals(endNode))
                    route.append(" → ");
            }
            String msg = String.format("<ul><li>%s - %s의 최단 거리: %.1f</li><li>경로: %s</li></ul>",
                    startNode, endNode, distance[nodes.indexOf(startNode)][nodes.indexOf(endNode)], route);

            // 탐색 완료 메시지 다이얼로그 출력
            showMessageDialog(null, String.format("<html>%s</html>", msg), "알고리즘 종료", JOptionPane.INFORMATION_MESSAGE);

            // get content between body tag from text
            String text = editorPane.getText();
            int startIndex = text.indexOf("<body>");
            int endIndex = text.lastIndexOf("</body>");
            String content = text.substring(startIndex + 6, endIndex);

            editorPane.setText(
                    content + String.format("<h2>탐색결과</h2><hr/>%s",msg)
            );
            gVisualPanelWrapper.getgInfoPanel().repaint();
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


    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final Graph graph;
    private final ArrayList<LinkedList<GraphEdge>> adjacencyList;
    private final ArrayList<GraphNode> nodes;
    private final GraphNode startNode;
    private final GraphNode endNode;
}
