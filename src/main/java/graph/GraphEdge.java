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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GraphEdge e) {
            if (e.from == this.from && e.to == this.to)
                return true;
            else return e.from == this.to && e.to == this.from;
        }
        return false;
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

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }
    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private final GraphNode from;
    private final GraphNode to;

    private double weight;
    private float strokeWidth = 1.0f;

    private Color strokeColor = Color.BLACK;
    private Color textColor = Color.WHITE;
}
