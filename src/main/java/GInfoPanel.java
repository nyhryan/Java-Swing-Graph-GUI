import graph.Graph;
import graph.GraphEdge;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 그래프 정보를 표시하는 패널
 */
class GInfoPanel extends JPanel {
    public GInfoPanel(Graph graph) {
        this.graph = graph;
        adjacencyList = graph.getAdjacencyList();
    }

    private final Graph graph;
    private final ArrayList<LinkedList<GraphEdge>> adjacencyList;
}
