package algorithm;

import java.util.ArrayList;

/**
 * 그래프 표현 클래스
 */
public class Graph {
    public Graph() {

    }

    /**
     * 그래프에 번호를 가지는 노드 추가
     * @param index 노드 번호
     */
    public void addNode(int index) {
        nodes.add(new GraphNode(index));
    }

    public ArrayList<GraphNode> getNodes() {
        return nodes;
    }

    /**
     * 그래프에 있는 모든 노드 ArrayList
     */
    private ArrayList<GraphNode> nodes = new ArrayList<>();
}
