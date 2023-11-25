package algorithm;

import graph.GraphNode;

public class DijkstraData implements Comparable<DijkstraData> {
    public DijkstraData(GraphNode node) {
        this.node = node;
    }

    @Override
    public int compareTo(DijkstraData o) {
        return Double.compare(node.getDistanceFromStart(), o.node.getDistanceFromStart());
    }

    public GraphNode getNode() {
        return node;
    }

    private final GraphNode node;
}
