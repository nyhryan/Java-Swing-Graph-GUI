package graph;

import java.awt.*;
import java.io.Serial;

/**
 * 그래프의 노드 표현 클래스
 */
public class GraphNode implements java.io.Serializable {
    /**
     * 노드 생성자
     * @param name 노드 이름
     */
    public GraphNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
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

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }
    public double getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public GraphNode getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(GraphNode previousNode) {
        this.previousNode = previousNode;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    // 노드 이름
    private String name;
    // 노드의 화면상 좌표
    private int x;
    private int y;
    private Color fillColor = Color.WHITE;
    private Color strokeColor = Color.BLACK;
    private double distanceFromStart = Double.POSITIVE_INFINITY;
    private boolean visited = false;
    private GraphNode previousNode = null;
}
