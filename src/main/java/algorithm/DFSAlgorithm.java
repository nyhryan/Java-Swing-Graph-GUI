package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphNode;

import java.util.LinkedHashSet;
import java.util.Stack;

import static javax.swing.JOptionPane.showMessageDialog;

public class DFSAlgorithm extends GraphAlgorithm {
    public DFSAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, IAlgorithmListener listener) {
        super(gVisualPanelWrapper, "DFS Algorithm".toUpperCase(), listener);
        this.startNode = startNode;

        startNode.setVisited(true);
        stack.push(startNode);
        vistedNodes.add(startNode);
    }


    @Override
    public void run() {
        listener.onAlgorithmStarted();

        while (!isCompleted) {
            try {
                semaphore.acquire();
                dfs();
                semaphore.release();
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted", e);
            }
        }

        showMessageDialog(null, "Algorithm completed");
        listener.onAlgorithmFinished();
    }

    private void dfs() {
        if (stack.empty()) {
            isCompleted = true;
            return;
        }
        if (isCompleted) {
            return;
        }

        GraphNode node = stack.pop();
        node.setFillColor(COLOR_1);
        node.setTextColor(COLOR_1_TEXT);
        waitAndRepaint();

        for (var edge : graph.getAdjacencyList().get(graph.getNodes().indexOf(node))) {
            var adjacentNode = edge.getTo();

            if (!adjacentNode.isVisited()) {
                adjacentNode.setVisited(true);
                adjacentNode.setFillColor(COLOR_2);
                adjacentNode.setTextColor(COLOR_2_TEXT);
                stack.push(adjacentNode);
                vistedNodes.add(adjacentNode);

                gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                        String.format("<h1>깊이 우선 탐색</h1><hr/><ul><li>현재 노드: %s</li>%s</ul><hr/>%s", node, vistedNodesString(vistedNodes), drawStack(stack)));
                waitAndRepaint();

                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        node.setFillColor(COLOR_2);
        node.setTextColor(COLOR_2_TEXT);
        gVisualPanelWrapper.getgVisualPanel().repaint();

        if (vistedNodes.size() == graph.getNodes().size()) {
            System.out.println("모든 노드를 방문했습니다.");
            isCompleted = true;
        }
    }

    public static String vistedNodesString(LinkedHashSet<GraphNode> visitedNodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<li>방문한 노드: ");
        for (var n : visitedNodes) {
            sb.append(String.format("%s", n));
            if (n != visitedNodes.toArray()[visitedNodes.size() - 1]) {
                sb.append(" → ");
            }
        }
        sb.append("</li>");
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

    private final GraphNode startNode;
    private final Stack<GraphNode> stack = new Stack<>();
    private final LinkedHashSet<GraphNode> vistedNodes = new LinkedHashSet<>();
}
