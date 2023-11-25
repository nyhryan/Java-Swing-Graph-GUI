package components;

import graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * 그래프(노드, 간선)를 그리는 패널
 */
public class GVisualPanel extends JPanel {
    public GVisualPanel(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;

        setBackground(Color.GRAY);
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

                g2d.setColor(edge.getColor());
                g2d.drawLine(x1, y1, x2, y2);

                // 가중치를 그린다.
                int weightX = (x1 + x2) / 2;
                int weightY = (y1 + y2) / 2;
                String weight = Double.toString(edge.getWeight());

                //간선 중앙에 가중치를 그린다.
                g2d.setFont(font);
                int weightWidth = g2d.getFontMetrics().stringWidth(weight);
                int weightHeight = g2d.getFontMetrics().getHeight();

                g2d.setColor(edge.getColor());
                g2d.fillRoundRect(weightX - weightWidth / 2, weightY - weightHeight / 2, weightWidth, weightHeight, 5, 5);
                g2d.setColor(Color.WHITE);
                g2d.drawString(weight, weightX - weightWidth / 2, weightY - weightHeight / 2 + weightHeight / 4 * 3);

                // 인접 리스트 사본에서 현재 간선의 반대방향의 간선을 지운다.
                adjacencyListCopy.get(graph.getNodes().indexOf(to)).removeIf(e -> e.getTo().equals(from));

            }
        }

        // 노드를 그린다.
        for (var node : graph.getNodes()) {
            g2d.setColor(node.getFillColor());
            g2d.fillOval(node.getX() - NODE_RADIUS / 2, node.getY() - NODE_RADIUS / 2, NODE_RADIUS, NODE_RADIUS);
            g2d.setColor(node.getStrokeColor());
            g2d.drawOval(node.getX() - NODE_RADIUS / 2, node.getY() - NODE_RADIUS / 2, NODE_RADIUS, NODE_RADIUS);

            // 노드 중앙에 이름을 그린다.
            String nameToDraw = node.getName();
            g2d.setFont(font);
            g2d.setColor(Color.BLACK);

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

            // 노드의 거리와 선행 노드를 이름 아래에 하얀색 박스 안에 검은색 텍스트로 그린다.
            if (node.getDistance() == Double.POSITIVE_INFINITY) continue;

            String distance = Double.toString(node.getDistance());
            String previousNode = node.getPreviousNode() == null ? "" : node.getPreviousNode().getName();
            String textToDraw = String.format("선행: %s - 거리: %s", previousNode, distance);

            width = g2d.getFontMetrics().stringWidth(textToDraw);
            height = g2d.getFontMetrics().getHeight();

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(node.getX() - width / 2, node.getY() + height / 4, width, height, 5, 5);

            g2d.setColor(Color.BLACK);
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
        graph.setAdjacencyList(randomGraph.getAdjacencyList());
        repaint();
        gVisualPanelWrapper.getgInfoPanel().repaint();
    }

    public void setEmptyGraph() {
        Graph emptyGraph = new Graph();
        graph.setNodes(emptyGraph.getNodes());
        graph.setAdjacencyList(emptyGraph.getAdjacencyList());
        repaint();
        gVisualPanelWrapper.getgInfoPanel().repaint();
    }

    public int getAnimationDelay() {
        return animationDelay;
    }

    public void setAnimationDelay(int animationDelay) {
        this.animationDelay = animationDelay;
    }


    // 그래프 객체
    private Graph graph = new Graph();
    public static final int NODE_RADIUS = 40;

    public enum Mode {NODE_MODE, EDGE_MODE, MOVE, DIJKSTRA_MODE, DEFAULT}

    private Mode mode = Mode.DEFAULT;
    private final Font font = new Font("Sans Serif", Font.PLAIN, 16);

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private int animationDelay = 500;
}

class GVisualPanelMouseAdapter extends MouseAdapter {
    /**
     * 더블클릭으로 노드 추가하는 리스너 생성자
     *
     * @param parentWrapperPanel 노드 추가 리스너를 추가할 그래프 패널
     */
    public GVisualPanelMouseAdapter(GVisualPanelWrapper parentWrapperPanel) {
        this.gVisualPanelWrapper = parentWrapperPanel;
        this.graph = parentWrapperPanel.getgVisualPanel().getGraph();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();
        var adjacencyList = panel.getGraph().getAdjacencyList();
        var nodes = panel.getGraph().getNodes();

        // 노드 추가 모드 + 왼쪽 더블클릭 한 경우에만 리스너 수행
        if (currentMode == GVisualPanel.Mode.NODE_MODE && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            // 노드 이름을 입력받는다.
            String nodeName;
            nodeName = JOptionPane.showInputDialog("새 노드 이름을 입력하세요.");

            // 노드 이름이 2글자 이상이어야 한다.
            if (nodeName == null || nodeName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "노드 이름은 최소 1글자 이상이어야 합니다.");
                return;
            }

            // 노드 이름이 이미 존재하는 이름이면 안된다.
            for (GraphNode node : graph.getNodes()) {
                if (node.getName().equals(nodeName)) {
                    JOptionPane.showMessageDialog(null, "이미 존재하는 노드 이름입니다.");
                    return;
                }
            }

            GraphNode newNode = new GraphNode(nodeName);
            newNode.setX(e.getX());
            newNode.setY(e.getY());
            graph.addNode(newNode);

            e.getComponent().repaint();
            gVisualPanelWrapper.getgInfoPanel().repaint();
        }

        // 노드 삭제
        if (currentMode == GVisualPanel.Mode.NODE_MODE && e.getButton() == MouseEvent.BUTTON3) {
            // 클릭한 노드를 찾아오기
            GraphNode node = selectNodeFromCursorPos(e);

            // 찾는 것에 성공했다면, nodes 배열하고 인접리스트에서 제거
            if (node != null) {
                String name = node.getName();

                adjacencyList.remove(graph.getNodes().indexOf(node));
                adjacencyList.forEach(list -> list.removeIf(edge -> edge.getTo().getName().equals(name)));

                nodes.remove(node);
            }
            e.getComponent().repaint();
            gVisualPanelWrapper.getgInfoPanel().repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();

        if (currentMode == GVisualPanel.Mode.EDGE_MODE && e.getButton() == MouseEvent.BUTTON1) {
            edgeStartNode = selectNodeFromCursorPos(e);
        }
    }

    // 노드 이동 모드에서 노드를 드래드한 경우 노드를 이동시킨다.
    @Override
    public void mouseDragged(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();

        if (currentMode == GVisualPanel.Mode.MOVE) {
            GraphNode node = selectNodeFromCursorPos(e);
            if (node != null) {
                node.setX(e.getX());
                node.setY(e.getY());
            }
            e.getComponent().repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();
        var adjacencyList = panel.getGraph().getAdjacencyList();
        var nodes = panel.getGraph().getNodes();


        if (currentMode == GVisualPanel.Mode.EDGE_MODE) {
            edgeEndNode = selectNodeFromCursorPos(e);
            // get edge weight from dialog box
            if (edgeEndNode != null) {
                double edgeWeight = Double.parseDouble(JOptionPane.showInputDialog("간선의 가중치를 입력하세요."));
                if (edgeWeight < 0) {
                    JOptionPane.showMessageDialog(null, "간선의 가중치는 0보다 커야 합니다.");
                    return;
                }
                else if (edgeWeight == 0.0) {
                    // remove edge from graph
                    adjacencyList.get(nodes.indexOf(edgeStartNode))
                            .removeIf(edge -> edge.getTo().equals(edgeEndNode));
                    adjacencyList.get(nodes.indexOf(edgeEndNode))
                            .removeIf(edge -> edge.getTo().equals(edgeStartNode));

                    e.getComponent().repaint();
                    gVisualPanelWrapper.getgInfoPanel().repaint();
                    return;
                }

                // add edge to graph
                graph.addEdge(edgeStartNode, edgeEndNode, edgeWeight);

                e.getComponent().repaint();
                gVisualPanelWrapper.getgInfoPanel().repaint();

            }
        }
    }

    private GraphNode selectNodeFromCursorPos(MouseEvent e) {
        GraphNode node = null;
        for (GraphNode n : graph.getNodes()) {
            if (e.getX() >= n.getX() - GVisualPanel.NODE_RADIUS && e.getX() <= n.getX() + GVisualPanel.NODE_RADIUS &&
                    e.getY() >= n.getY() - GVisualPanel.NODE_RADIUS && e.getY() <= n.getY() + GVisualPanel.NODE_RADIUS) {
                node = n;
                break;
            }
        }

        return node;
    }

    private final Graph graph;
    private final GVisualPanelWrapper gVisualPanelWrapper;
    private GraphNode edgeStartNode = null;
    private GraphNode edgeEndNode = null;
}
