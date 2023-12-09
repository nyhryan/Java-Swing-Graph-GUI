package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class BFSAlgorithm implements IGraphAlgorithm {
    public BFSAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        this.startNode = startNode;
        this.graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
    }

    @Override
    public void run() {
        synchronized (graph) {
            graph.resetGraphProperties();
            gVisualPanelWrapper.repaint();

            startNode.setFillColor(Color.GREEN);
            startNode.setVisited(true);

            var nodes = graph.getNodes();
            var adjacencyList = graph.getAdjacencyList();
            var editorPane = gVisualPanelWrapper.getgInfoPanel().getEditorPane();
            StringBuilder sb = new StringBuilder();

            sb.append("<h1>BFS Algorithm</h1><hr/>");
            sb.append(String.format("<h2>시작 노드: %s</h2>", startNode));
            editorPane.setText(sb.toString());
            waitAndRepaint();

            LinkedHashSet<GraphNode> vistedNodes = new LinkedHashSet<>();

            Queue<GraphNode> nodeQueue = new LinkedList<>();
            nodeQueue.offer(startNode);

            int round = 1;
            while (!nodeQueue.isEmpty()) {
                GraphNode node = nodeQueue.poll();
                node.setFillColor(Color.RED);
                vistedNodes.add(node);

                sb.setLength(0);
                sb.append(String.format("<h1>BFS Algorithm - 라운드: %d</h1><hr/>", round++));

                sb.append(DFSAlgorithm.vistedNodesString(vistedNodes));

                sb.append(String.format("<h2>현재 노드: %s</h2>", nodeQueue.peek()));

                editorPane.setText(sb + drawQueue(nodeQueue));
                gVisualPanelWrapper.getgInfoPanel().repaint();
                gVisualPanelWrapper.getgVisualPanel().repaint();

                for (var edge : adjacencyList.get(nodes.indexOf(node))) {
                    var adjacentNode = edge.getTo();

                    if (!adjacentNode.isVisited()) {
                        adjacentNode.setVisited(true);
                        nodeQueue.offer(adjacentNode);

                        vistedNodes.add(adjacentNode);
                        adjacentNode.setFillColor(Color.GREEN);
                    }

                    editorPane.setText(sb + drawQueue(nodeQueue));
                    waitAndRepaint();
                }

                // 노드, 간선 색깔 초기화
                for (var n : nodes) {
                    if (!n.isVisited()) {
                        n.setFillColor(Color.WHITE);
                        n.setTextColor(Color.BLACK);
                    } else {
                        n.setFillColor(Color.darkGray);
                        n.setTextColor(Color.WHITE);
                    }
                }
                for (var e : graph.getEdges()) {
                    e.setStrokeColor(Color.BLACK);
                    e.setTextColor(Color.WHITE);
                }

                gVisualPanelWrapper.getgVisualPanel().repaint();

                // 모든 노드를 방문했으면 그냥 끝내버린다.
                if (vistedNodes.size() == nodes.size()) {
                    break;
                }
            }

            sb.setLength(0);
            sb.append("<h1>BFS Algorithm</h1><hr/>");
            sb.append(DFSAlgorithm.vistedNodesString(vistedNodes));

            sb.append("</h1>");
            editorPane.setText(sb.toString());
            gVisualPanelWrapper.getgInfoPanel().repaint();

            showMessageDialog(null, "너비 우선 탐색을 완료했습니다.");
            gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(false);
        }
    }

    private String drawQueue(Queue<GraphNode> nodeQueue) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>큐</h2>")
                .append("<table border=\"1\">");

        sb.append("<tr>");
        for (GraphNode node : nodeQueue) {
            sb.append(String.format("<td>%s</td>", node));
        }
        sb.append("</tr>");

        sb.append("</table>");
        return sb.toString();
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
    private final GraphNode startNode;
    private final Graph graph;
}
