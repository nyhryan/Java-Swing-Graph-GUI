package algorithm;

import components.GVisualPanelWrapper;
import graph.*;

import java.util.ArrayList;

public class PrimAlgorithm implements IGraphAlgorithm {
    public PrimAlgorithm(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        nodes = graph.getNodes();
    }
    @Override
    public void run() {
        synchronized (graph) {

        }
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final Graph graph;
    private final ArrayList<GraphNode> nodes;
}
