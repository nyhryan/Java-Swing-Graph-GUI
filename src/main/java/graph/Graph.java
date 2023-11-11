package graph;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 그래프 표현 클래스
 */
public class Graph {
    public Graph() {

    }

    /**
     * 그래프에 노드 추가
     *
     * @param name 노드 이름
     */
    public void addNode(String name) {
        nodes.add(new GraphNode(name));
    }

    public void addNode(int integerName) {
        nodes.add(new GraphNode(Integer.toString(integerName)));
    }

    public void addNode(GraphNode node) {
        nodes.add(node);
        adjacencyList.add(new LinkedList<>());
    }

    public void addEdge(GraphNode startNode, GraphNode endNode, double weight) {
        adjacencyList.get(nodes.indexOf(startNode)).add(new GraphEdge(startNode, endNode, weight));
        adjacencyList.get(nodes.indexOf(endNode)).add(new GraphEdge(endNode, startNode, weight));
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
    private ArrayList<GraphNode> nodes = new ArrayList<>();

    // 노드와 자신의 인접노드들을 담는 인접리스트
    private ArrayList<LinkedList<GraphEdge>> adjacencyList = new ArrayList<>();

}
