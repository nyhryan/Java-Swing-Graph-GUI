package graph;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 그래프 표현 클래스
 */
public class Graph implements Serializable {
    public Graph() {

    }

    public void addNode(GraphNode node) {
        nodes.add(node);
        adjacencyList.add(new LinkedList<>());
    }

    public void addEdge(GraphNode startNode, GraphNode endNode, double weight) {
        GraphEdge startToEndEdge = new GraphEdge(startNode, endNode, weight);
        GraphEdge endToStartEdge = new GraphEdge(endNode, startNode, weight);

        int startNodeIndex = nodes.indexOf(startNode);
        int endNodeIndex = nodes.indexOf(endNode);

        boolean isWeightUpdated = false;

        // check if edge already exsits
        for (var edge : adjacencyList.get(startNodeIndex)) {
            // 같은 간선을 발견했다면
            if (edge.getFrom().equals(startNode) && edge.getTo().equals(endNode)) {
                // 입력한 가중치가 다르다면 갱신
                if (edge.getWeight() != weight) {
                    edge.setWeight(weight);
                    isWeightUpdated = true;
                }
                // 아니면 이미 있는 간선이므로 return
                else return;
            }
        }

        // 반대방향 간선에 대해서도 똑같이 함
        for (var edge : adjacencyList.get(endNodeIndex)) {
            if (edge.getFrom().equals(endNode) && edge.getTo().equals(startNode)) {
                if (isWeightUpdated)
                    edge.setWeight(weight);
                return;
            }
        }

        adjacencyList.get(startNodeIndex).add(startToEndEdge);
        adjacencyList.get(endNodeIndex).add(endToStartEdge);

        if (startNodeIndex < endNodeIndex) {
            edges.add(startToEndEdge);
        } else {
            edges.add(endToStartEdge);
        }
    }

    public void resetGraphProperties() {
        for (GraphNode node : nodes) {
            node.setVisited(false);
            node.setDistanceFromStart(Double.POSITIVE_INFINITY);
            node.setFillColor(Color.WHITE);
        }

        for (var edges: adjacencyList) {
            for (var edge : edges) {
                edge.setColor(Color.BLACK);
                edge.setStrokeWidth(1.0f);
            }
        }
    }

    public void saveGraph(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this);
            oos.flush();
            oos.close();
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGraph(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Graph graph = (Graph) ois.readObject();
            this.nodes = graph.nodes;
            this.edges = graph.edges;
            this.adjacencyList = graph.adjacencyList;

            ois.close();
            fis.close();

            assert this.nodes != null;
            assert this.adjacencyList != null;
            assert this.edges != null;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<LinkedList<GraphEdge>> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(ArrayList<LinkedList<GraphEdge>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public ArrayList<GraphEdge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<GraphEdge> edges) {
        this.edges = edges;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    // 그래프에 있는 모든 노드 ArrayList
    protected ArrayList<GraphNode> nodes = new ArrayList<>();

    protected ArrayList<GraphEdge> edges = new ArrayList<>();

    // 노드와 자신의 인접노드들을 담는 인접리스트
    protected ArrayList<LinkedList<GraphEdge>> adjacencyList = new ArrayList<>();
}
