package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphEdge;
import graph.GraphNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.swing.JOptionPane.showMessageDialog;

public class PrimAlgorithm extends GraphAlgorithm {
    public PrimAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "Kruskal Algorithm".toUpperCase(), listener);
        this.startNode = startNode;
        nodes = graph.getNodes();

        for (var node : nodes) {
            node.setVisited(false);
        }

        for (var node : nodes) {
            distanceToMst.put(node, Double.POSITIVE_INFINITY);
        }

        mst = new ArrayList<>(nodes.size() - 1);

        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Prim Algorithm</h1><hr/><ul><li>현재 노드: %s</li>%s</ul>", startNode, drawMstNodes()));
        gVisualPanelWrapper.getgInfoPanel().repaint();
    }

    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            prim();
        }

        double totalWeight = 0.0;
        for (var edge : graph.getEdges()) {
            if (edge.getStrokeColor() == COLOR_1) {
                totalWeight += edge.getWeight();
                edge.setStrokeColor(Color.GREEN);
                edge.setTextColor(Color.BLACK);
                edge.setStrokeWidth(5.0f);
            } else {
                edge.setStrokeColor(Color.GRAY);
                edge.setStrokeWidth(0.1f);
            }
            waitAndRepaint(100);
        }
        showMessageDialog(null, "알고리즘 종료", "알림", JOptionPane.INFORMATION_MESSAGE);

        String msg = String.format("<ul><li>최소 신장 트리의 가중치 합: %.1f</ul><hr/><em>이 창을 클릭하여 인접리스트 보이기</em>", totalWeight);
        appendMessageToEditorPane(msg);
        SwingUtilities.invokeLater(() -> gVisualPanelWrapper.getgInfoPanel().repaint());

        listener.onAlgorithmFinished();
    }

    private void prim() {
        if (mst.size() >= nodes.size()) {
            isCompleted = true;
            return;
        }

        // 맨 첫단계는 시작 노드를 MST에 넣고 끝낸다.
        if (mst.isEmpty()) {
            mst.add(startNode);
            startNode.setVisited(true);
            startNode.setFillColor(Color.GREEN);
            startNode.setTextColor(Color.BLACK);

            calculateDistanceToMst();
            gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                    String.format("<h1>Prim Algorithm</h1><hr/><ul><li>현재 노드: %s</li>%s</ul>", startNode, drawMstNodes()));

            waitAndRepaint();
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

            minEdge.setStrokeWidth(5.0f);
            minEdge.setStrokeColor(COLOR_1);
            minEdge.setTextColor(COLOR_1_TEXT);

            var reversedEdge = graph.getEdge(minEdge.getTo(), minEdge.getFrom());
            reversedEdge.setStrokeWidth(3.0f);
            reversedEdge.setStrokeColor(COLOR_1);
            reversedEdge.setTextColor(COLOR_1_TEXT);
        }

        calculateDistanceToMst();
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                String.format("<h1>Prim Algorithm</h1><hr/><ul><li>현재 노드: %s</li>%s</ul>", startNode, drawMstNodes()));

        waitAndRepaint();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void calculateDistanceToMst() {
        for (var edge : graph.getEdges()) {
            GraphNode u = edge.getTo();
            GraphNode v = edge.getFrom();
            if (mst.contains(u) && !mst.contains(v)) {
                distanceToMst.put(v, edge.getWeight());
            } else if (mst.contains(v) && !mst.contains(u)) {
                distanceToMst.put(u, edge.getWeight());
            } else if (mst.contains(u) && mst.contains(v)) {
                distanceToMst.put(u, Double.POSITIVE_INFINITY);
                distanceToMst.put(v, Double.POSITIVE_INFINITY);
            }
        }
    }

    private String drawMstNodes() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\">")
                .append("<tr><td>노드</td>")
                .append("<td>MST까지의 거리</td>")
                .append("<td>채택</td></tr>");

        var nodesByDistance = new ArrayList<>(distanceToMst.entrySet());
        nodesByDistance.sort(Map.Entry.comparingByValue(Comparator.naturalOrder()));
        GraphNode minNode = nodesByDistance.get(0).getKey();

        for (var node : nodes) {
            sb.append(String.format("<tr><td>%s</td>", node));

            if (distanceToMst.get(node) == Double.POSITIVE_INFINITY) {
                sb.append("<td>∞</td>");
            } else if (minNode.equals(node)) {
                sb.append(String.format("<td style=\"background-color:#52F0A0;\">%.1f &#10003;</td>", distanceToMst.get(node)));
            }
            else {
                sb.append(String.format("<td style=\"background-color:#F8457C;\">%.1f</td>", distanceToMst.get(node)));
            }
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
        private final LinkedHashMap<GraphNode, Double> distanceToMst = new LinkedHashMap<>();
}