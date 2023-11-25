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
        boolean isWeightUpdated = false;
        // check if edge already exsits
        for (var edge : adjacencyList.get(nodes.indexOf(startNode))) {
            if (edge.getFrom().equals(startNode) && edge.getTo().equals(endNode)) {
                // update if weight parameter is different, else return
                if (edge.getWeight() != weight) {
                    edge.setWeight(weight);
                }
                else return;
            }
        }
        // check the opposite direction edge too
        for (var edge : adjacencyList.get(nodes.indexOf(endNode))) {
            if (edge.getFrom().equals(endNode) && edge.getTo().equals(startNode)) {
                if (edge.getWeight() != weight) {
                    edge.setWeight(weight);
                    isWeightUpdated = true;
                }
                else return;
            }
        }

        if (isWeightUpdated) return;

        adjacencyList.get(nodes.indexOf(startNode)).add(new GraphEdge(startNode, endNode, weight));
        adjacencyList.get(nodes.indexOf(endNode)).add(new GraphEdge(endNode, startNode, weight));
    }

    public void resetGraphNodes() {
        for (GraphNode node : nodes) {
            node.setVisited(false);
            node.setDistanceFromStart(Double.POSITIVE_INFINITY);
            node.setFillColor(Color.WHITE);
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
            this.adjacencyList = graph.adjacencyList;

            ois.close();
            fis.close();

            assert this.nodes != null;
            assert this.adjacencyList != null;

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

    @Serial
    private static final long serialVersionUID = 1L;

    // 그래프에 있는 모든 노드 ArrayList
    protected ArrayList<GraphNode> nodes = new ArrayList<>();

    // 노드와 자신의 인접노드들을 담는 인접리스트
    protected ArrayList<LinkedList<GraphEdge>> adjacencyList = new ArrayList<>();
}
