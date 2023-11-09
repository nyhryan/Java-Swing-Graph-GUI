package graph;

/**
 * 그래프의 노드 표현 클래스
 */
public class GraphNode {
    /**
     * 노드 생성자
     * @param name 노드 이름
     */
    public GraphNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // 노드 이름
    private String name;

    // 노드의 화면상 좌표
    private int x;
    private int y;
}
