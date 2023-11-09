package graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
    }

    public ArrayList<GraphNode> getNodes() {
        return nodes;
    }
    public LinkedHashMap<GraphNode, ArrayList<GraphNode>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * 그래프에 있는 모든 노드 ArrayList
     */
    private ArrayList<GraphNode> nodes = new ArrayList<>();

    // 노드와 자신의 인접노드들을 담는 인접리스트
    private LinkedHashMap<GraphNode, ArrayList<GraphNode>> adjacencyList = new LinkedHashMap<>();

}
