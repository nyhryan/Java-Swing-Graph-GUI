package components;

import algorithm.*;
import graph.*;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import static javax.swing.JOptionPane.showMessageDialog;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

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
        kit.getStyleSheet().addRule("table { table-layout: fixed; } table, th, td { border: 1px solid black; font-size: 16px; }");

        // 그래프 정보를 출력할 JEditorPane
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setEditorKit(kit);
        editorPane.setBackground(Color.WHITE);

        // 그래프 정보를 출력할 JEditorPane을 스크롤 패널에 추가한다.
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(300, 600));
        add(scrollPane);

        // 알고리즘 버튼들과 애니메이션 속도 슬라이더 등을 모은 패널
        JPanel buttonPanel = addGInfoPanelComponents();
        add(buttonPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);

        var mode = gVisualPanelWrapper.getgVisualPanel().getMode();

        if (mode == GVisualPanel.Mode.NODE_MODE ||
            mode == GVisualPanel.Mode.EDGE_MODE ||
            mode == GVisualPanel.Mode.DEFAULT ||
            mode == GVisualPanel.Mode.MOVE) {
            showAdjacencyList();
        }
    }

    private void showAdjacencyList() {
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

    private GraphNode[] selectStartEndNodes() {
        var graph = gVisualPanelWrapper.getgVisualPanel().getGraph();

        // names of all nodes
        String[] nodeNames = new String[graph.getNodes().size()];
        for (int i = 0; i < nodeNames.length; i++) {
            nodeNames[i] = graph.getNodes().get(i).getName();
        }

        // 시작 노드, 끝 노드를 선택할 수 있는 콤보 박스를 생성하고, 패널에 추가한다.
        JComboBox<String> startNodeCbx = new JComboBox<>(new DefaultComboBoxModel<>(nodeNames));
        JComboBox<String> endNodeCbx = new JComboBox<>(new DefaultComboBoxModel<>(nodeNames));
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(4, 4, 4, 4);
        panel.add(new JLabel("시작 노드 선택"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("끝 노드 선택"), gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(startNodeCbx, gbc);
        gbc.gridy++;
        panel.add(endNodeCbx, gbc);

        // get a start node and end node in a single showInputDialog
        JOptionPane.showMessageDialog(
                gVisualPanelWrapper,
                panel,
                "시작 노드와 끝 노드를 선택하세요.",
                JOptionPane.QUESTION_MESSAGE
        );

        // find actual start node from nodes based on startNodeStr
        GraphNode startNode = graph.getNodes()
                .stream()
                .filter(node -> node.getName()
                        .equals(startNodeCbx.getSelectedItem()))
                .findFirst()
                .orElse(null);

        GraphNode endNode = graph.getNodes()
                .stream()
                .filter(node -> node.getName()
                        .equals(endNodeCbx.getSelectedItem()))
                .findFirst()
                .orElse(null);

        // if startNode is null, return
        if (startNode == null || endNode == null) return null;
        if (startNode.equals(endNode)) {
            showMessageDialog(null, "시작 노드와 끝 노드가 같습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new GraphNode[]{startNode, endNode};
    }
    private JPanel addGInfoPanelComponents() {
        JButton dijkstraBtn = new JButton("Dijkstra Algorithm");
        dijkstraBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

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
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
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

        JButton kruskalBtn = new JButton("Kruskal Algorithm");
        kruskalBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            SwingUtilities.invokeLater(() -> {
                Thread t = new Thread(new KruskalAlgorithm(gVisualPanelWrapper), "Kruskal Algorithm".toUpperCase());
                t.start();
            });
        });

        JButton stopBtn = new JButton("알고리즘 중단");
        ImageIcon stopIcon = ImageIconLoader.getImageIcon("/stop.png");
        stopBtn.setIcon(stopIcon);
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
        buttonPanel.add(kruskalBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        buttonPanel.add(stopBtn, gbc);


        // 애니메이션 속도를 입력할 Spinner 추가
        JLabel animSpeedLabel = new JLabel("애니메이션 속도 (ms)");
        JSpinner animSpeed = new JSpinner(new SpinnerNumberModel(1000, 0, 10000, 100));
        animSpeed.addChangeListener(e -> {
            int value = (int) animSpeed.getValue();
            gVisualPanelWrapper.getgVisualPanel().setAnimationSpeed(value);
        });

        gbc.gridy++;
        buttonPanel.add(animSpeedLabel, gbc);
        gbc.gridx++;
        buttonPanel.add(animSpeed, gbc);

        return buttonPanel;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final JEditorPane editorPane;
}
