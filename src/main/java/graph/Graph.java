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
        adjacencyList.get(nodes.indexOf(startNode)).add(new GraphEdge(startNode, endNode, weight));
        adjacencyList.get(nodes.indexOf(endNode)).add(new GraphEdge(endNode, startNode, weight));
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
    public ArrayList<LinkedList<GraphEdge>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * 그래프에 있는 모든 노드 ArrayList
     */
    protected final ArrayList<GraphNode> nodes = new ArrayList<>();
    @Serial
    private static final long serialVersionUID = 1L;


    // 노드와 자신의 인접노드들을 담는 인접리스트
    protected final ArrayList<LinkedList<GraphEdge>> adjacencyList = new ArrayList<>();
}
