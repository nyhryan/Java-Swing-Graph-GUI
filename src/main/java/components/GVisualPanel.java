package components;

import graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * 그래프(노드, 간선)를 그리는 패널
 */
public class GVisualPanel extends JPanel {
    public GVisualPanel(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;

        setBackground(new Color(0xB0B0B0));
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 더 나은 Graphic2D를 사용함.
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);

        // Anti-aliasing 활성화
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        // 그리는데 사용할 인접 리스트의 사본
        ArrayList<LinkedList<GraphEdge>> adjacencyListCopy = new ArrayList<>(graph.getAdjacencyList());
        adjacencyListCopy.replaceAll(LinkedList::new);

        // 간선을 먼저 그린다.
        for (var nodes : adjacencyListCopy) {
            for (var edge : nodes) {
                GraphNode from = edge.getFrom();
                GraphNode to = edge.getTo();

                int x1 = from.getX();
                int y1 = from.getY();
                int x2 = to.getX();
                int y2 = to.getY();

                g2d.setColor(edge.getStrokeColor());
                g2d.setStroke(new BasicStroke(edge.getStrokeWidth()));
                g2d.drawLine(x1, y1, x2, y2);

                // 가중치를 그린다.
                int weightX = (x1 + x2) / 2;
                int weightY = (y1 + y2) / 2;
                String weight = Double.toString(edge.getWeight());

                //간선 중앙에 가중치를 그린다.
                g2d.setFont(font);
                int weightWidth = g2d.getFontMetrics().stringWidth(weight);
                int weightHeight = g2d.getFontMetrics().getHeight();

                g2d.setColor(edge.getStrokeColor());
                g2d.setStroke(new BasicStroke(1));
                g2d.fillRoundRect(weightX - weightWidth / 2, weightY - weightHeight / 2, weightWidth, weightHeight, 5, 5);
                g2d.setColor(edge.getTextColor());
                g2d.drawString(weight, weightX - weightWidth / 2, weightY - weightHeight / 2 + weightHeight / 4 * 3);

                // 인접 리스트 사본에서 현재 간선의 반대방향의 간선을 지운다.
                adjacencyListCopy.get(graph.getNodes().indexOf(to)).removeIf(e -> e.getTo().equals(from));

            }
        }

        // 노드를 그린다.
        for (var node : graph.getNodes()) {
            g2d.setColor(node.getFillColor());
            g2d.setStroke(new BasicStroke(1));
            g2d.fillOval(node.getX() - NODE_RADIUS / 2, node.getY() - NODE_RADIUS / 2, NODE_RADIUS, NODE_RADIUS);
            g2d.setColor(node.getStrokeColor());
            g2d.drawOval(node.getX() - NODE_RADIUS / 2, node.getY() - NODE_RADIUS / 2, NODE_RADIUS, NODE_RADIUS);

            // 노드 중앙에 이름을 그린다.
            String nameToDraw = node.getName();
            g2d.setFont(font);
            g2d.setColor(node.getTextColor());

            if (node.getName().length() == 3) {
                g2d.setFont(new Font("Sans Serif", Font.PLAIN, 14));
            }
            // 이름이 3글자 이상이면 세글자 뒤는 ...으로 표시
            else if (node.getName().length() > 3) {
                nameToDraw = node.getName().substring(0, 3) + "...";
                g2d.setFont(new Font("Sans Serif", Font.PLAIN, 12));
            }

            int width = g2d.getFontMetrics().stringWidth(nameToDraw);
            int height = g2d.getFontMetrics().getHeight();
            g2d.drawString(nameToDraw, node.getX() - width / 2, node.getY() + height / 4);

            // 다익스트라 모드에서 노드의 거리와 선행 노드를 이름 아래에 하얀색 박스 안에 검은색 텍스트로 그린다.
            if (node.getDistanceFromStart() == Double.POSITIVE_INFINITY || node.getPreviousNode() == null) continue;

            String distance = Double.toString(node.getDistanceFromStart());
            String previousNode = node.getPreviousNode() == null ? "" : node.getPreviousNode().getName();
            String textToDraw = String.format("선행: %s - 거리: %s", previousNode, distance);

            width = g2d.getFontMetrics().stringWidth(textToDraw);
            height = g2d.getFontMetrics().getHeight();

            g2d.setColor(node.getFillColor());
            g2d.fillRoundRect(node.getX() - width / 2, node.getY() + height / 4, width, height, 5, 5);

            g2d.setColor(node.getStrokeColor());
            g2d.drawRoundRect(node.getX() - width / 2, node.getY() + height / 4, width, height, 5, 5);

            g2d.drawString(textToDraw, node.getX() - width / 2, node.getY() + height / 4 * 5);
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setRandomGraph() {
        Graph randomGraph = new RandomGraph();
        graph.setNodes(randomGraph.getNodes());
        graph.setEdges(randomGraph.getEdges());
        graph.setAdjacencyList(randomGraph.getAdjacencyList());
        repaint();
        gVisualPanelWrapper.getgInfoPanel().repaint();
    }

    public void setEmptyGraph() {
        Graph emptyGraph = new Graph();
        graph.setNodes(emptyGraph.getNodes());
        graph.setEdges(emptyGraph.getEdges());
        graph.setAdjacencyList(emptyGraph.getAdjacencyList());
        repaint();
        gVisualPanelWrapper.getgInfoPanel().repaint();
    }

    public void resetGraph() {
        graph.resetGraphProperties();
        repaint();
        gVisualPanelWrapper.getgInfoPanel().repaint();
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    private final Graph graph = new Graph();

    public static final int NODE_RADIUS = 40;

    public enum Mode {NODE_MODE, EDGE_MODE, MOVE, ALGORITHM_MODE, DEFAULT}

    private Mode mode = Mode.DEFAULT;

    private final Font font = new Font("Sans Serif", Font.PLAIN, 16);

    private final GVisualPanelWrapper gVisualPanelWrapper;

    private int animationSpeed = 500;
}

