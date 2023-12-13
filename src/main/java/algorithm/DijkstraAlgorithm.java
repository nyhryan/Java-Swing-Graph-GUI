package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphNode;
import tools.RandomColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import static javax.swing.JOptionPane.showMessageDialog;

public class DijkstraAlgorithm extends GraphAlgorithm {
    public DijkstraAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, GraphNode endNode, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "Dijkstra Algorithm".toUpperCase(), listener);
        this.startNode = startNode;
        this.endNode = endNode;

        startNode.setDistanceFromStart(0);
        startNode.setVisited(true);
        pq.offer(new DijkstraData(startNode));

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Dijkstra Algorithm</h1><hr/>%s", drawDijkstraInfo(null, null))
        );
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            dijkstra();
        }

        for (var node : graph.getNodes()) {
            node.setFillColor(Color.WHITE);
            node.setTextColor(Color.BLACK);
        }

        // 경로 재구축
        GraphNode node = endNode;
        ArrayList<GraphNode> path = new ArrayList<>();
        StringBuilder route = new StringBuilder();

        while (!node.equals(startNode)) {
            path.add(node);
            node = node.getPreviousNode();

            if (node == null) {
                showMessageDialog(null, "경로가 존재하지 않습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                listener.onAlgorithmFinished();
                return;
            }
        }
        path.add(startNode);
        Collections.reverse(path);

        double distance = path.get(path.size() - 1).getDistanceFromStart();
        drawPath(path);

        for (GraphNode n : path) {
            route.append(n.getName());
            if (!n.equals(endNode))
                route.append(" → ");
        }
        String msg = String.format("<ul><li>%s - %s의 최단 거리: %.1f</li><li>경로: %s</li></ul>",
                startNode, endNode, distance, route);

        appendMessageToEditorPane(msg);
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());

        showMessageDialog(null, "최단거리 탐색 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
        listener.onAlgorithmFinished();
    }

    private void dijkstra() {
        if (pq.isEmpty()) {
            isCompleted = true;
            return;
        }

        // 매 단계 색상을 초기화.
        for (var node : graph.getNodes()) {
            node.setFillColor(Color.WHITE);
            node.setTextColor(Color.BLACK);
        }
        gVisualPanelWrapper.getgVisualPanel().repaint();

        assert pq.peek() != null;
        GraphNode minNode = pq.poll().getNode();
        minNode.setFillColor(CURRENT_COLOR);
        minNode.setTextColor(CURRENT_TEXT_COLOR);
        waitAndRepaint();

        var outgoingEdges = graph.getAdjacencyList().get(graph.getNodes().indexOf(minNode));
        for (var edge : outgoingEdges) {
            var adjacentNode = edge.getTo();
            adjacentNode.setFillColor(NOT_CHANGED_COLOR);
            adjacentNode.setTextColor(NOT_CHANGED_TEXT_COLOR);

            if (adjacentNode.isNotVisited()) {
                adjacentNode.setVisited(true);
                pq.offer(new DijkstraData(adjacentNode));
            }

            double distance = minNode.getDistanceFromStart() + edge.getWeight();
            if (distance < adjacentNode.getDistanceFromStart()) {
                adjacentNode.setDistanceFromStart(distance);
                adjacentNode.setPreviousNode(minNode);
                if (!adjacentNode.equals(startNode)) {
                    adjacentNode.setFillColor(UPDATED_COLOR);
                    adjacentNode.setTextColor(UPDATED_TEXT_COLOR);
                }
            }

            gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                    String.format("<h1>Dijkstra Algorithm</h1><hr/>%s",
                            drawDijkstraInfo(minNode, adjacentNode))
            );
            waitAndRepaint();

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String drawDijkstraInfo(GraphNode currentNode, GraphNode adjNode) {
        StringBuilder sb = new StringBuilder();
        ArrayList<GraphNode> adjNodes = new ArrayList<>();
        if (currentNode != null) {
            for (var edge : graph.getAdjacencyList().get(graph.getNodes().indexOf(currentNode))) {
                adjNodes.add(edge.getTo());
            }
        }
        sb.append(String.format("<h2>현재 노드: %s → 조사할 노드: %s</h2><hr/>", currentNode, adjNodes));

        sb.append(String.format("<h2>노드 %s으로부터 모든 노드로의 거리 표</h2>", startNode));
        sb.append("<table><thead><tr><th>노드</th><th>거리</th><th>선행</th><th>현재</th></tr></thead><tbody>");

        for (var node : graph.getNodes()) {
            String style = "";
            if (node.equals(currentNode))
                style = String.format(" style=\"background-color:%s; color:%s;\"", RandomColor.getHexColor(currentNode.getFillColor()), RandomColor.getHexColor(currentNode.getTextColor()));
            else if (node.equals(adjNode))
                style = String.format(" style=\"background-color:%s; color:%s;\"", RandomColor.getHexColor(adjNode.getFillColor()), RandomColor.getHexColor(adjNode.getTextColor()));

            String current = "";
            if (node.equals(currentNode)) {
                current = "&#9754;";
            } else if (node.equals(adjNode))
                current = "&#63;";

            if (node.getDistanceFromStart() == Double.POSITIVE_INFINITY)
                sb.append(String.format("<tr%s><td>%s</td><td>∞</td><td>%s</td><td>%s</td></tr>", style, node, node.getPreviousNode(), current));
            else if (node.getDistanceFromStart() == 0)
                sb.append(String.format("<tr%s><td>%s</td><td>-</td><td>%s</td><td>%s</td></tr>", style, node, node.getPreviousNode(), current));
            else
                sb.append(String.format("<tr%s><td>%s</td><td>%.1f</td><td>%s</td><td>%s</td></tr>", style, node, node.getDistanceFromStart(), node.getPreviousNode(), current));
        }
        sb.append("</tbody></table>");
        sb.append(String.format("<h2>색상 참고: <em style=\"color:%s;\">&lt;현재 노드&gt;</em>&nbsp;<em style=\"color:%s;\">&lt;변화x&gt;</em>&nbsp;<em style=\"color:%s;\">&lt;갱신!&gt;</em></h2>",
                RandomColor.getHexColor(CURRENT_COLOR), RandomColor.getHexColor(NOT_CHANGED_COLOR), RandomColor.getHexColor(UPDATED_COLOR)));

        sb.append("<hr/>");
        sb.append(String.format("<h2>%s로부터의 거리 우선순위 큐</h2>", startNode));
        sb.append("<table><thead><tr><th>노드</th><th>거리</th></tr></thead><tbody>");

        ArrayList<DijkstraData> sortedPQ = new ArrayList<>(pq);
        sortedPQ.sort(Comparator.comparingDouble(o -> o.getNode().getDistanceFromStart()));

        for (DijkstraData data : sortedPQ) {
            sb.append(
                    String.format("<tr><td>%s</td><td>%.1f</td></tr>", data.getNode().getName(), data.getNode().getDistanceFromStart()));
        }
        return sb.toString();
    }

    private final GraphNode startNode;
    private final GraphNode endNode;
    private final PriorityQueue<DijkstraData> pq = new PriorityQueue<>();
    private final StringBuilder pollingLog = new StringBuilder();

    private final Color CURRENT_COLOR = new Color(0x30D39D);
    private final Color CURRENT_TEXT_COLOR = new Color(0x000000);
    private final Color NOT_CHANGED_COLOR = new Color(0xFA247C);
    private final Color NOT_CHANGED_TEXT_COLOR = new Color(0x000000);
    private final Color UPDATED_COLOR = new Color(0x7FFF00);
    private final Color UPDATED_TEXT_COLOR = new Color(0x000000);
}
