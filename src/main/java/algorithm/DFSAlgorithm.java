package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Stack;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class DFSAlgorithm implements IGraphAlgorithm {
    public DFSAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        this.startNode = startNode;
        this.graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
    }

    @Override
    public void run() {
        synchronized (graph) {
            // 초기화(색상, 거리, 방문 기록)
            graph.resetGraphProperties();
            gVisualPanelWrapper.repaint();

            // start algorithm from startNode
            startNode.setFillColor(Color.GREEN);
            startNode.setVisited(true);

            var nodes = graph.getNodes();
            var adjacencyList = graph.getAdjacencyList();
            var editorPane = gVisualPanelWrapper.getgInfoPanel().getEditorPane();
            StringBuilder sb = new StringBuilder();
            sb.append("<h1>DFS Algorithm</h1><hr/>");
            editorPane.setText(sb.toString());
            waitAndRepaint();


            LinkedHashSet<GraphNode> vistedNodes = new LinkedHashSet<>();

            Stack<GraphNode> nodeStack = new Stack<>();
            nodeStack.push(startNode);

            int round = 1;
            while (!nodeStack.empty()) {
                sb.setLength(0);
                sb.append(String.format("<h1>DFS Algorithm -  라운드: %d</h1><hr/>", round++));

                sb.append(vistedNodesString(vistedNodes));
                sb.append("</h1>");
                sb.append(String.format("<h2>현재 노드: %s</h2>", nodeStack.peek()));

                GraphNode node = nodeStack.pop();
                node.setFillColor(Color.RED);
                vistedNodes.add(node);

                if (!node.isVisited()) {
                    node.setVisited(true);
                }

                editorPane.setText(sb + drawStack(nodeStack));
                gVisualPanelWrapper.getgInfoPanel().repaint();
                gVisualPanelWrapper.getgVisualPanel().repaint();

                for (var edge : adjacencyList.get(nodes.indexOf(node))) {
                    var adjacentNode = edge.getTo();

                    if (!adjacentNode.isVisited()) {
                        adjacentNode.setVisited(true);
                        nodeStack.push(adjacentNode);

                        vistedNodes.add(adjacentNode);
                        adjacentNode.setFillColor(Color.GREEN);
                    }

                    editorPane.setText(sb + drawStack(nodeStack));
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
            sb.append("<h1>DFS Algorithm</h1><hr/>");
            sb.append(vistedNodesString(vistedNodes));
            editorPane.setText(sb.toString());
            gVisualPanelWrapper.getgInfoPanel().repaint();

            showMessageDialog(null, "깊이 우선 탐색을 완료했습니다.");
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

    public static String vistedNodesString(LinkedHashSet<GraphNode> visitedNodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>방문한 노드: ");
        for (var n : visitedNodes) {
            sb.append(String.format("%s", n));
            if (n != visitedNodes.toArray()[visitedNodes.size() - 1]) {
                sb.append(" → ");
            }
        }
        sb.append("</h1><hr/>");
        return sb.toString();
    }

    private String drawStack(Stack<GraphNode> stack) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h2>스택 크기: %d</h2>", stack.size()));
        sb.append("<table border=\"1\">")
            .append("<tr><td>TOP</td></tr>");

        for (int i = stack.size() - 1; i > -1; i--) {
            sb.append(String.format("<tr><td>%s</td></tr>", stack.get(i)));
        }
        return sb.toString();
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final GraphNode startNode;
    private final Graph graph;
}
