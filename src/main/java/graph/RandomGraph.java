package graph;

import java.util.HashMap;
import java.util.Random;

public class RandomGraph extends Graph {
    public RandomGraph() {
        super();

        Random rd = new Random();
        final int MAX_LIMIT = 10;
        final double WEIGHT_MAX = 9.0;

        // 노드의 개수를 1 ~ MAX_LIMIT 사이의 랜덤한 수로 설정
        int vertices = rd.nextInt(MAX_LIMIT) + 1;
//        int vertices = MAX_LIMIT;
        for (int i = 0; i < vertices; i++) {
            GraphNode node = new GraphNode(Integer.toString(i));

            // 노드를 원형으로 배치
            double direction = i * (Math.PI * 2) / vertices;
            int radius = 200;
            int x = (int) (Math.cos(direction) * radius);
            int y = (int) (Math.sin(direction) * radius);

            x += 300;
            y += 300;

            node.setX(x);
            node.setY(y);
            addNode(node);
        }

        float p = rd.nextFloat();

        // 한 노드에 같은 노드로의 간선을 여러개 추가하지 않도록 이 맵에 추가한 간선을 저장
        var addedEdges = new HashMap<GraphNode, GraphNode>();

        for (int i = 0; i < vertices; i++) {
            GraphNode iNode = nodes.get(i);
            for (int j = 0; j < vertices; j++) {
                GraphNode jNode = nodes.get(j);

                float edgeProbability = rd.nextFloat();
                if (edgeProbability < p) {
                    if (i == j) continue;

                    double weight = rd.nextDouble(WEIGHT_MAX) + 1;

                    // round weight to 0 decimal place
                    weight = Math.round(weight);

                    addEdge(iNode, jNode, weight);
                    addedEdges.put(iNode, jNode);
                    addedEdges.put(jNode, iNode);
                }
            }
        }
    }

}
