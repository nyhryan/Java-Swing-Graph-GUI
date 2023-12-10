package components;

import algorithm.*;
import graph.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
        JPanel buttonPanel = panelWithAlgorithmButtons();
        add(buttonPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);

        var mode = gVisualPanelWrapper.getgVisualPanel().getMode();

        if (gVisualPanelWrapper.getgVisualPanel().isAlgorithmRunning()) return;

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

    private GraphNode selectStartNode() {
        // 시작 노드 선택
        var graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        String[] nodeNames = new String[graph.getNodes().size()];
        for (int i = 0; i < nodeNames.length; i++) {
            nodeNames[i] = graph.getNodes().get(i).getName();
        }
        JComboBox<String> startNodeCbx = new JComboBox<>(new DefaultComboBoxModel<>(nodeNames));
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(4, 4, 4, 4);
        panel.add(new JLabel("시작 노드 선택"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(startNodeCbx, gbc);

        var result = JOptionPane.showConfirmDialog(
                gVisualPanelWrapper,
                panel,
                "시작 노드를 선택하세요.",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.CANCEL_OPTION) return null;

        return graph.getNodes()
                .stream()
                .filter(node -> node.getName()
                        .equals(startNodeCbx.getSelectedItem()))
                .findFirst()
                .orElse(null);
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
        var result = JOptionPane.showConfirmDialog(
                gVisualPanelWrapper,
                panel,
                "시작 노드와 끝 노드를 선택하세요.",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.CANCEL_OPTION) return null;

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

    private JPanel panelWithAlgorithmButtons() {
        JButton DFSBtn = new JButton("깊이 우선");
        DFSBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            GraphNode startNode = selectStartNode();
            if (startNode == null) return;

            SwingUtilities.invokeLater(() -> {
                var dfs = new DFSAlgorithm(gVisualPanelWrapper, startNode);
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                dfs.startAlgorithm();
                dfs.start();
            });
        });

        JButton BFSBtn = new JButton("너비 우선");
        BFSBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            GraphNode startNode = selectStartNode();
            if (startNode == null) return;

            SwingUtilities.invokeLater(() -> {
                Thread t = new BFSAlgorithm(gVisualPanelWrapper, startNode);
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                t.start();
            });
        });

        JButton dijkstraBtn = new JButton("Dijkstra");
        dijkstraBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            // 시작 노드와 끝 노드를 선택한다.
            GraphNode[] nodes = selectStartEndNodes();
            if (nodes == null) return;
            GraphNode startNode = nodes[0];
            GraphNode endNode = nodes[1];

            SwingUtilities.invokeLater(() -> {
                Thread t = new DijkstraAlgorithm(gVisualPanelWrapper, startNode, endNode);
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                t.start();
            });
        });

        JButton floydBtn = new JButton("Floyd");
        floydBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            // 시작 노드와 끝 노드를 선택한다.
            GraphNode[] nodes = selectStartEndNodes();
            if (nodes == null) return;
            GraphNode startNode = nodes[0];
            GraphNode endNode = nodes[1];

            SwingUtilities.invokeLater(() -> {
                Thread t = new FloydAlgorithm(gVisualPanelWrapper, startNode, endNode);
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                t.start();
            });
        });

        JButton kruskalBtn = new JButton("Kruskal");
        kruskalBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            SwingUtilities.invokeLater(() -> {
                Thread t = new KruskalAlgorithm(gVisualPanelWrapper);
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                t.start();
            });
        });

        JButton primBtn = new JButton("Prim");
        primBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            GraphNode startNode = selectStartNode();
            if (startNode == null) return;

            SwingUtilities.invokeLater(() -> {
                Thread t = new PrimAlgorithm(gVisualPanelWrapper, startNode);
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                t.start();
            });
        });

        JButton nextStepBtn = new JButton("다음 단계");
        // proceed current algorithm to next step
        nextStepBtn.addActionListener(e -> {
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm".toUpperCase())) {
                    switch (t.getName()) {
                        case "DFS ALGORITHM" :
                            ((DFSAlgorithm) t).singleStep();
                            break;
//                        case "BFSAlgorithm":
//                            ((BFSAlgorithm) t).performNextStep();
//                            break;
//                        case "DijkstraAlgorithm":
//                            ((DijkstraAlgorithm) t).performNextStep();
//                            break;
//                        case "FloydAlgorithm":
//                            ((FloydAlgorithm) t).performNextStep();
//                            break;
//                        case "KruskalAlgorithm":
//                            ((KruskalAlgorithm) t).performNextStep();
//                            break;
//                        case "PrimAlgorithm":
//                            ((PrimAlgorithm) t).performNextStep();
//                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + t.getName());
                    }
                }
            }
        });

        JButton resumeBtn = new JButton("알고리즘 재개");
        resumeBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm".toUpperCase())) {
                    switch (t.getName()) {
                        case "DFS ALGORITHM":
                            ((DFSAlgorithm) t).resumeAlgorithm();
                            break;
//                        case "BFSAlgorithm":
//                            ((BFSAlgorithm) t).performNextStep();
//                            break;
//                        case "DijkstraAlgorithm":
//                            ((DijkstraAlgorithm) t).performNextStep();
//                            break;
//                        case "FloydAlgorithm":
//                            ((FloydAlgorithm) t).performNextStep();
//                            break;
//                        case "KruskalAlgorithm":
//                            ((KruskalAlgorithm) t).performNextStep();
//                            break;
//                        case "PrimAlgorithm":
//                            ((PrimAlgorithm) t).performNextStep();
//                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + t.getName());
                    }
                }
            }
        });

        JButton stopBtn = new JButton("알고리즘 중단");
        ImageIcon stopIcon = ImageIconLoader.getImageIcon("/stop.png");
        stopBtn.setIcon(stopIcon);
        stopBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.DEFAULT);
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm".toUpperCase())) {
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

        JPanel traversalPanel = new JPanel();
        traversalPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "순회 알고리즘", TitledBorder.LEFT, TitledBorder.TOP));
        traversalPanel.add(DFSBtn);
        traversalPanel.add(BFSBtn);

        buttonPanel.add(traversalPanel, gbc);

        gbc.gridx++;
        JPanel routingPanel = new JPanel();
        routingPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "경로 탐색 알고리즘", TitledBorder.LEFT, TitledBorder.TOP));
        routingPanel.add(dijkstraBtn);
        routingPanel.add(floydBtn);

        buttonPanel.add(routingPanel, gbc);

        gbc.gridx++;
        JPanel MSTPanel = new JPanel();
        MSTPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "MST 탐색 알고리즘", TitledBorder.LEFT, TitledBorder.TOP));
        MSTPanel.add(kruskalBtn);
        MSTPanel.add(primBtn);
        buttonPanel.add(MSTPanel, gbc);

        // 애니메이션 속도를 입력할 Spinner 추가
        JPanel animSpeedPanel = new JPanel();
        animSpeedPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "애니메이션 속도", TitledBorder.LEFT, TitledBorder.TOP));
        JLabel animSpeedLabel = new JLabel("밀리초(ms)");
        animSpeedPanel.add(animSpeedLabel);
        JSpinner animSpeed = new JSpinner(new SpinnerNumberModel(1000, 0, 10000, 100));
        animSpeed.addChangeListener(e -> {
            int value = (int) animSpeed.getValue();
            gVisualPanelWrapper.getgVisualPanel().setAnimationSpeed(value);
        });
        animSpeedPanel.add(animSpeed);

        gbc.gridx = 0;
        gbc.gridy++;
        buttonPanel.add(animSpeedPanel, gbc);

        gbc.gridy++;
        buttonPanel.add(nextStepBtn, gbc);
        gbc.gridx++;
        buttonPanel.add(resumeBtn, gbc);
        gbc.gridx++;
        buttonPanel.add(stopBtn, gbc);

        return buttonPanel;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public void setEditorPaneText(String text) {
        editorPane.setText(text);
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final JEditorPane editorPane;
}
