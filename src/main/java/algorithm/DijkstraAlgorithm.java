package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphNode;

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
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            dijkstra();
            semaphore.release();
        }

        for (var node : graph.getNodes()) {
            node.setFillColor(Color.WHITE);
            node.setTextColor(Color.BLACK);
        }

        // 경로 재구축
        GraphNode node = endNode;
        ArrayList<GraphNode> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getPreviousNode();
        }
        Collections.reverse(path);
        double distance = path.get(path.size() - 1).getDistanceFromStart();

        for (int i = 0; i <= path.size() - 2; i++) {
            path.get(i).setFillColor(Color.GREEN);

            var edge = graph.getEdge(path.get(i), path.get(i + 1));
            edge.setStrokeColor(Color.GREEN);
            edge.setTextColor(Color.BLACK);
            edge.setStrokeWidth(5.0f);

            waitAndRepaint();
        }
        path.get(path.size() - 1).setFillColor(Color.GREEN);
        waitAndRepaint();

        StringBuilder route = new StringBuilder();
        for (GraphNode n : path) {
            route.append(n.getName());
            if (!n.equals(endNode))
                route.append(" → ");
        }
        String msg = String.format("<ul><li>%s - %s의 최단 거리: %.1f</li><li>경로: %s</li></ul>",
                startNode, endNode, distance, route);

        String currentEditorPaneText = gVisualPanelWrapper.getgInfoPanel().getEditorPane().getText();
        int startIndex = currentEditorPaneText.indexOf("<body>");
        int endIndex = currentEditorPaneText.lastIndexOf("</body>");
        String content = currentEditorPaneText.substring(startIndex + 6, endIndex);
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                content + String.format("<hr/><h2>탐색결과</h2>%s</html>", msg)
        );
        gVisualPanelWrapper.getgInfoPanel().repaint();

        showMessageDialog(null, "알고리즘 종료", "알림", JOptionPane.INFORMATION_MESSAGE);
        listener.onAlgorithmFinished();
    }

    private void dijkstra() {
        if (pq.isEmpty()) {
            isCompleted = true;
            return;
        }
//        if (isCompleted) {
//            return;
//        }

        // 매 단계 색상을 초기화.
        for (var node : graph.getNodes()) {
            node.setFillColor(Color.WHITE);
            node.setTextColor(Color.BLACK);
        }
        gVisualPanelWrapper.getgVisualPanel().repaint();

        assert pq.peek() != null;
        GraphNode minNode = pq.poll().getNode();
        minNode.setFillColor(COLOR_1);
        minNode.setTextColor(COLOR_1_TEXT);
        pollingLog.append(String.format("<ul><li>현재 노드: %s, 인접노드: ", minNode));
        waitAndRepaint();

        for (var edge : graph.getAdjacencyList().get(graph.getNodes().indexOf(minNode))) {
            var adjacentNode = edge.getTo();
            adjacentNode.setFillColor(COLOR_2);
            adjacentNode.setTextColor(COLOR_2_TEXT);
            pollingLog.append(String.format("%s →", adjacentNode));

            if (!adjacentNode.isVisited()) {
                adjacentNode.setVisited(true);
                pq.offer(new DijkstraData(adjacentNode));
            }

            double distance = minNode.getDistanceFromStart() + edge.getWeight();
            if (distance < adjacentNode.getDistanceFromStart()) {
                adjacentNode.setDistanceFromStart(distance);
                adjacentNode.setPreviousNode(minNode);
                if (!adjacentNode.equals(startNode)) {
                    adjacentNode.setFillColor(COLOR_3);
                    adjacentNode.setTextColor(COLOR_3_TEXT);
                }
            }

            gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                    String.format("<h1>Dijkstra Algorithm</h1><hr/><div>%s</div><hr/>%s",
                            pollingLog, drawPQ())
            );
            waitAndRepaint();

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        pollingLog.append("</ul>");
    }

    private String drawPQ() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h2>우선순위 큐 크기 : %d</h2>", pq.size()));
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
}
