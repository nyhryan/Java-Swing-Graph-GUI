package components;

import algorithm.*;
import graph.*;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * 그래프 정보를 표시하는 패널
 */
public class GInfoPanel extends JPanel {
    /**
     * @param gVisualPanelWrapper 이 패널을 가지는 부모 래퍼 클래스 레퍼런스
     */
    public GInfoPanel(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 그래프 정보를 출력할 창에 CSS를 적용한다.
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule("span, p { margin: 0; padding: 0; font-size: 12px; } li { font-size: 12px; }");

        // 그래프 정보를 출력할 JEditorPane
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setEditorKit(kit);
        editorPane.setBackground(Color.WHITE);

        // 그래프 정보를 출력할 JEditorPane을 스크롤 패널에 추가한다.
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        add(scrollPane);

        // 알고리즘 테스트 버튼
        JButton dijkstraBtn = new JButton("Dijkstra Algorithm");
        dijkstraBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.DIJKSTRA_MODE);

            // 시작 노드와 끝 노드를 선택한다.
            GraphNode[] nodes = selectStartEndNodes();
            if (nodes == null) return;
            GraphNode startNode = nodes[0];
            GraphNode endNode = nodes[1];

            SwingUtilities.invokeLater(() -> {
                Thread t = new Thread(new DijkstraAlgorithm(gVisualPanelWrapper, startNode, endNode), "Dijkstra Algorithm".toUpperCase());
                t.start();
            });
        });

        JButton floydBtn = new JButton("Floyd Algorithm");
        floydBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.FLOYD_MODE);
            // 시작 노드와 끝 노드를 선택한다.
            GraphNode[] nodes = selectStartEndNodes();
            if (nodes == null) return;
            GraphNode startNode = nodes[0];
            GraphNode endNode = nodes[1];

            SwingUtilities.invokeLater(() -> {
                Thread t = new Thread(new FloydAlgorithm(gVisualPanelWrapper, startNode, endNode), "Floyd Algorithm".toUpperCase());
                t.start();
            });
        });

        JButton stopBtn = new JButton("알고리즘 중단");
        stopBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.DEFAULT);
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm")) {
                    t.interrupt();
                    gVisualPanelWrapper.getgVisualPanel().resetGraph();
                    showMessageDialog(null, "알고리즘을 중단합니다.", "알고리즘 중단", JOptionPane.WARNING_MESSAGE);
                    break;
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 알고리즘 버튼을 패널에 추가한다.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 4, 4);
        buttonPanel.add(dijkstraBtn, gbc);
        gbc.gridx++;
        buttonPanel.add(floydBtn, gbc);
        gbc.gridx++;
        buttonPanel.add(stopBtn, gbc);
        gbc.gridy++;
        gbc.gridx = 0;

        // 애니메이션 속도를 조절할 슬라이더를 패널에 추가한다.
        JLabel animSpeed = new JLabel("애니메이션 속도");
        JSlider slider = new JSlider(0, 1000, 500);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(200);
        slider.setMinorTickSpacing(100);
        slider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                gVisualPanelWrapper.getgVisualPanel().setAnimationDelay(source.getValue());
            }
        });

        buttonPanel.add(animSpeed, gbc);
        gbc.gridx++;
        buttonPanel.add(slider, gbc);

        add(buttonPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);

        if (gVisualPanelWrapper.getgVisualPanel().getMode() == GVisualPanel.Mode.DIJKSTRA_MODE) {
            drawDijkstraInfo();
        } else {
            drawAdjacencyList();
        }
    }

    private void drawAdjacencyList() {
        Graph graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        ArrayList<LinkedList<GraphEdge>> adjacencyList = graph.getAdjacencyList();

        if (graph.getNodes() == null) return;

        // print adjacency list to current JEditorPane
        StringBuilder sb = new StringBuilder();
        int edgeCount = 0;
        for (int i = 0; i < adjacencyList.size(); i++) {
            LinkedList<GraphEdge> edges = adjacencyList.get(i);
            sb.append(String.format("<p>%s : ", graph.getNodes().get(i).getName()));

            if (edges.isEmpty()) {
                sb.append("</p>\n");
                continue;
            }

            for (GraphEdge edge : edges) {
                sb.append(String.format("\n<span>%s (%.1f)</span> → ", edge.getTo().getName(), edge.getWeight()));
                edgeCount++;
            }
            sb.append("\n<span>null</span>\n</p>\n");
        }

        if (!adjacencyList.isEmpty()) {
            sb.append("<hr><ul><li>노드 개수: ").append(adjacencyList.size()).append("</li>");
            sb.append("<li>간선 개수: ").append(edgeCount / 2).append("</li></ul>");
        }

        editorPane.setText("<h1>Adjacency List</h1><hr>\n" + sb);
    }

    private void drawDijkstraInfo() {
        Graph graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        ArrayList<GraphNode> nodes = graph.getNodes();

        // 알고리즘 진행상황을 출력
        StringBuilder sb = new StringBuilder();
        for (GraphNode node : nodes) {
            sb.append("<p>")
                    .append(String.format("<span>%s</span> : ", node.getName()))
                    .append(String.format("<span>distance: %.1f</span>, ", node.getDistance()))
                    .append(String.format("<span>previous: %s</span>", node.getPreviousNode() == null ? "null" : node.getPreviousNode().getName()))
                    .append("</p>");

            editorPane.setText("<h1>Dijkstra Algorithm</h1><hr/>" + sb);
        }
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final JEditorPane editorPane;
}
