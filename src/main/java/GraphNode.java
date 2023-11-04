/**
 * 그래프의 노드 표현 클래스
 */
public class GraphNode {
    /**
     * 노드 생성자, 디폴트 번호는 0.
     */
    public GraphNode() {
        nodeNumber = 0;
    }

    /**
     * 노드 생성자
     * @param nodeNumber 노드 번호
     */
    public GraphNode(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * 노드 번호
     */
    private int nodeNumber;
}
