package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static javax.swing.JOptionPane.showMessageDialog;

public class FloydAlgorithm extends GraphAlgorithm {
    public FloydAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, GraphNode endNode, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "Floyd Algorithm".toUpperCase(), listener);
        this.startNode = startNode;
        this.endNode = endNode;

        // Prim에 사용되는 배열들 초기화
        var nodes = graph.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                distance[i][j] = Double.POSITIVE_INFINITY;
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

        k = 0;
        size = nodes.size();

        String distMatrixString = distMatrixToString(-1, -1, -1, false);
        String prevMatrixString = prevMatrixToString(-1, -1, -1, false);
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Floyd Algorithm</h1><hr/>%s<hr/>%s", distMatrixString, prevMatrixString));
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            prim();
        }

        // 경로 재구축
        GraphNode node = endNode;
        ArrayList<GraphNode> path = new ArrayList<>();
        StringBuilder route = new StringBuilder();

        while (!node.equals(startNode)) {
            path.add(node);
            node = prev[graph.getNodes().indexOf(startNode)][graph.getNodes().indexOf(node)];

            if (node == null) {
            showMessageDialog(null, "경로가 존재하지 않습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            listener.onAlgorithmFinished();
            return;
            }
        }
        path.add(startNode);
        Collections.reverse(path);

        drawPath(path);

        for (GraphNode n : path) {
            route.append(n.getName());
            if (!n.equals(endNode))
                route.append(" → ");
        }
        String msg = String.format("<ul><li>%s - %s의 최단 거리: %.1f</li><li>경로: %s</li></ul><hr/><em>이 창을 클릭하여 인접리스트 보이기</em>",
                startNode, endNode, distance[graph.getNodes().indexOf(startNode)][graph.getNodes().indexOf(endNode)], route);

        String distMatrixString = distMatrixToString(-1, -1, -1, false);
        String prevMatrixString = prevMatrixToString(-1, -1, -1, false);
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Floyd Algorithm</h1><hr/>%s<hr/>%s", distMatrixString, prevMatrixString));

        appendMessageToEditorPane(msg);
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());

        showMessageDialog(null, "최단거리 탐색 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
        listener.onAlgorithmFinished();
    }


    private void prim() {
        if (k >= size) {
            isCompleted = true;
            return;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                GraphNode kNode = graph.getNodes().get(k);
                GraphNode iNode = graph.getNodes().get(i);
                GraphNode jNode = graph.getNodes().get(j);

                if (k != i && i != j && j != k) {
                    kNode.setFillColor(Color.YELLOW);
                    kNode.setTextColor(Color.BLACK);
                    iNode.setFillColor(COLOR_1);
                    iNode.setTextColor(COLOR_1_TEXT);
                    jNode.setFillColor(COLOR_2);
                    jNode.setTextColor(COLOR_2_TEXT);
                }

                boolean isUpdated = false;
                if (distance[i][j] > distance[i][k] + distance[k][j]) {
                    distance[i][j] = distance[i][k] + distance[k][j];
                    prev[i][j] = prev[k][j];
                    isUpdated = true;
                }

                String distMatrixString = distMatrixToString(k, i, j, isUpdated);
                String prevMatrixString = prevMatrixToString(k, i, j, isUpdated);
                gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                        String.format("<h1>Floyd Algorithm</h1><hr/>%s<hr/>%s", distMatrixString, prevMatrixString));
                waitAndRepaint();

                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for (var node : graph.getNodes()) {
                    node.setFillColor(Color.WHITE);
                    node.setTextColor(Color.BLACK);
                }
                SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgVisualPanel().repaint());
            }
        }
        k++;
    }

    private String distMatrixToString(int _k, int _i, int _j, boolean isUpdated) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>distance matrix</h2>")
                .append("<table><thead><tr>")
                .append("<th>노드</th>");

        for (int i = 0; i < graph.getNodes().size(); i++) {
            if (i == _k)
                sb.append(String.format("<th style=\"background-color:yellow;\">K = %s</th>", graph.getNodes().get(i).getName()));
            else
                sb.append(String.format("<th>&nbsp;&nbsp;&nbsp;&nbsp;%s</th>", graph.getNodes().get(i).getName()));
        }
        sb.append("</tr></thead>")
                .append("<tbody>");

        for (int i = 0; i < graph.getNodes().size(); i++) {
            sb.append(String.format("<tr><td>%s</td>", graph.getNodes().get(i).getName()));

            for (int j = 0; j < graph.getNodes().size(); j++) {
                String distanceStr = distance[i][j] == Double.POSITIVE_INFINITY ? "∞" : String.format("%.1f", distance[i][j]);

                if ((j == _j && i == _i) && isUpdated)
                    sb.append(String.format("<td style=\"background-color:#7FFFD4;\">%s</td>", distanceStr));
                else if (j == _j && i == _i)
                    sb.append(String.format("<td style=\"background-color:yellow;\">%s</td>", distanceStr));
                else
                    sb.append(String.format("<td>%s</td>", distanceStr));

            }
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");

        return sb.toString();
    }

    private String prevMatrixToString(int _k, int _i, int _j, boolean isUpdated) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>prev matrix</h2>")
                .append("<table><thead><tr>")
                .append("<th>노드</th>");

        for (int i = 0; i < graph.getNodes().size(); i++) {
            if (i == _k)
                sb.append(String.format("<th style=\"background-color:yellow;\">K = %s</th>", graph.getNodes().get(i).getName()));
            else
                sb.append(String.format("<th>&nbsp;&nbsp;&nbsp;&nbsp;%s</th>", graph.getNodes().get(i).getName()));
        }
        sb.append("</tr></thead>")
                .append("<tbody>");

        for (int i = 0; i < graph.getNodes().size(); i++) {
            sb.append(String.format("<tr><td>%s</td>", graph.getNodes().get(i).getName()));

            for (int j = 0; j < graph.getNodes().size(); j++) {
                String prevStr = prev[i][j] == null ? "NULL" : prev[i][j].toString();

                if ((j == _j && i == _i) && isUpdated)
                    sb.append(String.format("<td style=\"background-color:#7FFFD4;\">%s</td>", prevStr));
                else if (j == _j && i == _i)
                    sb.append(String.format("<td style=\"background-color:yellow;\">%s</td>", prevStr));
                else
                    sb.append(String.format("<td>%s</td>", prevStr));
            }
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");

        return sb.toString();
    }

    private final GraphNode startNode;
    private final GraphNode endNode;

    private final double[][] distance = new double[graph.getNodes().size()][graph.getNodes().size()];
    private final GraphNode[][] prev = new GraphNode[graph.getNodes().size()][graph.getNodes().size()];
    private int k;
    private final int size;
}
