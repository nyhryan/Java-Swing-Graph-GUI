package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class DijkstraAlgorithm implements IGraphAlgorithm {

    public DijkstraAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, GraphNode endNode) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        this.startNode = startNode;
        this.endNode = endNode;

        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        adjacencyList = graph.getAdjacencyList();
        nodes = graph.getNodes();
    }

    @Override
    public void run() {
        synchronized (graph) {
            // 초기화(색상, 거리, 방문 기록)
            graph.resetGraphProperties();
            gVisualPanelWrapper.repaint();

            // start algorithm from startNode
            startNode.setFillColor(Color.GREEN);
            startNode.setDistanceFromStart(0);
            startNode.setVisited(true);
            showDijkstraInfo();
            waitAndRepaint();

            // create priority queue
            PriorityQueue<DijkstraData> pq = new PriorityQueue<>();
            pq.add(new DijkstraData(startNode));

            // run algorithm
            while (!pq.isEmpty()) {
                // get minimum distance node
                GraphNode minNode = pq.poll().getNode();
                if (!minNode.equals(startNode))
                    minNode.setFillColor(Color.PINK);

                showDijkstraInfo();
                waitAndRepaint();

                // visit all adjacent nodes
                for (GraphEdge edge : adjacencyList.get(nodes.indexOf(minNode))) {
                    GraphNode adjacentNode = edge.getTo();
                    if (!adjacentNode.equals(startNode))
                        adjacentNode.setFillColor(Color.YELLOW);

                    if (!adjacentNode.isVisited()) {
                        adjacentNode.setVisited(true);
                        pq.add(new DijkstraData(adjacentNode));
                    }

                    // update distance
                    double distance = minNode.getDistanceFromStart() + edge.getWeight();
                    if (distance < adjacentNode.getDistanceFromStart()) {
                        adjacentNode.setDistanceFromStart(distance);
                        adjacentNode.setPreviousNode(minNode);
                        if (!adjacentNode.equals(startNode))
                            adjacentNode.setFillColor(Color.RED);
                    }
                    waitAndRepaint();
                }

                // 색깔 초기화
                for (GraphEdge e : adjacencyList.get(nodes.indexOf(minNode))) {
                    if (!e.getTo().equals(startNode))
                        e.getTo().setFillColor(Color.WHITE);
                }
                if (!minNode.equals(startNode))
                    minNode.setFillColor(Color.WHITE);
                waitAndRepaint();
            }

            // 목적지부터 시작점까지 최단 경로 탐색
            GraphNode node = endNode;
            ArrayList<GraphNode> path = new ArrayList<>();
            path.add(node);
            double distance = node.getDistanceFromStart();
            while (node != null) {
                node.setFillColor(Color.GREEN);

                // 양방향의 간선을 모두 파란색으로 변경
                GraphEdge edge = null;
                for (GraphEdge e : adjacencyList.get(nodes.indexOf(node))) {
                    if (e.getTo().equals(node.getPreviousNode())) {
                        e.setStrokeColor(Color.BLUE);
                        e.setStrokeWidth(5.0f);
                        edge = e;
                        break;
                    }
                }

                if (edge == null) break;
                for (GraphEdge e : adjacencyList.get(nodes.indexOf(edge.getTo()))) {
                    if (e.getTo().equals(node)) {
                        e.setStrokeColor(Color.BLUE);
                        e.setStrokeWidth(5.0f);
                        break;
                    }
                }

                node = node.getPreviousNode();
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
                    startNode, endNode, distance, route);

            // 탐색 완료 메시지 다이얼로그 출력
            showMessageDialog(null, String.format("<html>%s</html>", msg), "알고리즘 종료", JOptionPane.INFORMATION_MESSAGE);

            // infoPanel에도 출력
            var editorPane = gVisualPanelWrapper.getgInfoPanel().getEditorPane();

            // get content between body tag from text
            String text = editorPane.getText();
            int startIndex = text.indexOf("<body>");
            int endIndex = text.lastIndexOf("</body>");
            String content = text.substring(startIndex + 6, endIndex);

            editorPane.setText(
                    content + String.format("<h2>탐색결과</h2>%s</html>", msg));
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

    private void showDijkstraInfo() {
        Graph graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        ArrayList<GraphNode> nodes = graph.getNodes();

        // 알고리즘 진행상황을 출력
        StringBuilder sb = new StringBuilder();
        sb.append("<table><thead><tr>")
                .append("<th>노드</th>")
                .append("<th>거리</th>")
                .append("<th>선행 노드</th>")
                .append("</tr></thead>")
                .append("<tbody>");
        for (GraphNode node : nodes) {
            sb.append("<tr>")
                    .append(String.format("<td>%s</td>", node.getName()))
                    .append(String.format("<td>%.1f</td>", node.getDistanceFromStart()))
                    .append(String.format("<td>%s</td>", node.getPreviousNode() == null ? "null" : node.getPreviousNode().getName()))
                    .append("</tr>");
        }
        sb.append("</tbody></table>");
        gVisualPanelWrapper.getgInfoPanel().getEditorPane().setText("<h1>Dijkstra Algorithm</h1><hr/>" + sb);
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final Graph graph;
    private final ArrayList<LinkedList<GraphEdge>> adjacencyList;
    private final ArrayList<GraphNode> nodes;
    private final GraphNode startNode;
    private final GraphNode endNode;
}
