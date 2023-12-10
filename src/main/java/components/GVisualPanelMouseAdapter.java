package components;

import graph.Graph;
import graph.GraphNode;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class GVisualPanelMouseAdapter extends MouseAdapter {
    public GVisualPanelMouseAdapter(GVisualPanelWrapper parentWrapperPanel) {
        this.gVisualPanelWrapper = parentWrapperPanel;
        this.graph = parentWrapperPanel.getgVisualPanel().getGraph();
    }

    // 노드 추가 혹은 삭제 시에 마우스 클릭 이벤트를 사용
    @Override
    public void mouseClicked(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();
        var adjacencyList = graph.getAdjacencyList();
        var nodes = graph.getNodes();

        if (panel.isAlgorithmRunning()) return;

        // 노드 추가 모드 + 왼쪽 더블클릭 한 경우에만 리스너 수행
        if (currentMode == GVisualPanel.Mode.NODE_MODE && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            GraphNode cursorNode = selectNodeFromCursorPos(e);

            // 노드 이름을 입력받는다.
            String nodeName;
            nodeName = JOptionPane.showInputDialog("새 노드 이름을 입력하세요.", cursorNode == null ? Integer.toString(nodes.size()) : cursorNode.getName());

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

            if (cursorNode != null) {
                cursorNode.setName(nodeName);
                e.getComponent().repaint();
                gVisualPanelWrapper.getgInfoPanel().repaint();
                return;
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

    // 간선 그리기 모드에서, 처음 마우스 버튼을 누른 순간의 노드를 기억함.
    @Override
    public void mousePressed(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();

        if (panel.isAlgorithmRunning()) return;

        if (currentMode == GVisualPanel.Mode.EDGE_MODE && e.getButton() == MouseEvent.BUTTON1) {
            edgeStartNode = selectNodeFromCursorPos(e);
        }
    }

    // 노드 이동 모드에서 노드를 드래드한 경우 노드를 이동시킨다.
    @Override
    public void mouseDragged(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();

        // 커서가 새로 이동한 위치로 노드의 좌표도 변경.
        if (currentMode == GVisualPanel.Mode.MOVE) {
            GraphNode node = selectNodeFromCursorPos(e);
            if (node != null) {
                node.setX(e.getX());
                node.setY(e.getY());
            }
            e.getComponent().repaint();
        }
    }

    // 간선 그리기 모드에서, 처음 누른 순간 기억한 노드로부터 마우스 버튼에서 땐 노드까지 간선을 그림
    @Override
    public void mouseReleased(MouseEvent e) {
        GVisualPanel panel = (GVisualPanel) e.getComponent();
        GVisualPanel.Mode currentMode = panel.getMode();
        var adjacencyList = graph.getAdjacencyList();
        var nodes = graph.getNodes();

        if (panel.isAlgorithmRunning()) return;

        if (currentMode == GVisualPanel.Mode.EDGE_MODE) {
            // 클릭을 뗀 곳에 노드가 있다면 해당 노드를 가져오기
            edgeEndNode = selectNodeFromCursorPos(e);

            // 간선의 끝 노드가 정해졌으면 가중치를 입력 받고 간선을 추가.
            // 0을 입력해서 간선을 제거.
            if (edgeEndNode != null) {
                double edgeWeight = Double.parseDouble(JOptionPane.showInputDialog("간선의 가중치를 입력하세요.", "1.0"));

                if (edgeWeight < 0) {
                    JOptionPane.showMessageDialog(null, "간선의 가중치는 0보다 커야 합니다.");
                    return;
                }
                else if (edgeWeight == 0.0) {
                    // 간선(양쪽 방향 두개)을 인접리스트로부터 제거하고 화면 다시 그리기
                    adjacencyList.get(nodes.indexOf(edgeStartNode))
                            .removeIf(edge -> edge.getTo().equals(edgeEndNode));
                    adjacencyList.get(nodes.indexOf(edgeEndNode))
                            .removeIf(edge -> edge.getTo().equals(edgeStartNode));

                    // edges에서도 지우기
                    if (nodes.indexOf(edgeStartNode) < nodes.indexOf(edgeEndNode)) {
                        graph.getEdges().removeIf(edge -> edge.getFrom().equals(edgeStartNode) && edge.getTo().equals(edgeEndNode));
                    } else {
                        graph.getEdges().removeIf(edge -> edge.getFrom().equals(edgeEndNode) && edge.getTo().equals(edgeStartNode));
                    }

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

    /**
     * 커서가 있는 지점에 노드가 있다면, graph의 nodes로부터 찾아옴.
     * @param e 마우스 이벤트
     * @return 커서가 있는 지점의 노드 리턴
     */
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
