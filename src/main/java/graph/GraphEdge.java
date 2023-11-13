package graph;

import java.awt.*;

public class GraphEdge {
    public GraphEdge(GraphNode from, GraphNode to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }


    public GraphNode getFrom() {
        return from;
    }

    public GraphNode getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double edgeWeight) {
        this.weight = edgeWeight;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private GraphNode from;
    private GraphNode to;

    private double weight;


    private Color color = Color.BLACK;

}
