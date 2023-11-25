package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class DijkstraAlgorithm implements IGraphAlgorithm {

    public DijkstraAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, GraphNode startNode, GraphNode endNode) {
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

            // start algorithm from startNode
            startNode.setFillColor(Color.GREEN);
            startNode.setDistanceFromStart(0);
            startNode.setVisited(true);
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

            // 최단 경로 탐색
            GraphNode node = endNode;
            while (node != null) {
                node.setFillColor(Color.GREEN);

                GraphEdge edge = null;
                for (GraphEdge e : adjacencyList.get(nodes.indexOf(node))) {
                    if (e.getTo().equals(node.getPreviousNode())) {
                        e.setColor(Color.BLUE);
                        edge = e;
                        break;
                    }
                }

                if (edge == null) break;
                for (GraphEdge e : adjacencyList.get(nodes.indexOf(edge.getTo()))) {
                    if (e.getTo().equals(node)) {
                        e.setColor(Color.BLUE);
                        break;
                    }
                }

                node = node.getPreviousNode();
                waitAndRepaint();
            }

            // 탐색 완료 메시지 다이얼로그 출력
            showMessageDialog(null, "탐색이 완료되었습니다.");
        }
    }

    private void waitAndRepaint() {
        try {
            sleep(gVisualPanelWrapper.getgVisualPanel().getAnimationDelay());
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
