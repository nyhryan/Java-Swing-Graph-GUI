package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class RandomGraph extends Graph {
    public RandomGraph() {
        super();

        Random rd = new Random();
        final int MAX_LIMIT = 7;
        final double WEIGHT_MAX = 9.0;

        int vertices = rd.nextInt(MAX_LIMIT) + 1;
        for (int i = 0; i < vertices; i++) {
            GraphNode node = new GraphNode(Integer.toString(i));
            node.setX(i * 10 + 10);
            node.setY(i * 10 + 10);
            addNode(node);
        }

        float p = rd.nextFloat();

        for (int i = 0; i < vertices; i++) {
            GraphNode iNode = nodes.get(i);
            for (int j = 0; j < vertices; j++) {
                GraphNode jNode = nodes.get(j);

                float edgeProbability = rd.nextFloat();
                if (edgeProbability < p) {
                    if (i == j) continue;
                    double weight = rd.nextDouble(WEIGHT_MAX) + 1;

                    // round weight to 1 decimal places
                    weight = Math.round(weight * 10.0) / 10.0;

                    addEdge(iNode, jNode, weight);
                }
            }
        }
    }

}
