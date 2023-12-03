package graph;

import java.awt.*;
import java.io.Serial;

public class GraphEdge implements Comparable<GraphEdge>, java.io.Serializable {
    public GraphEdge(GraphNode from, GraphNode to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(GraphEdge e) {
        return Double.compare(this.weight, e.weight);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.2f)", from, to, weight);
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

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private final GraphNode from;
    private final GraphNode to;

    private double weight;
    private Stroke stroke = new BasicStroke(1);

    private Color color = Color.BLACK;
}
