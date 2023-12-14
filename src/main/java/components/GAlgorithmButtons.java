package components;

import algorithm.*;
import graph.GraphNode;
import tools.ImageIconLoader;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class GAlgorithmButtons extends JPanel {
    public GAlgorithmButtons(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;

        JButton nextStepBtn = new JButton();
        nextStepBtn.setToolTipText("다음 단계로 진행");
        nextStepBtn.setEnabled(false);
        ImageIcon nextStepIcon = ImageIconLoader.getImageIcon("/next.png", 24);
        nextStepBtn.setIcon(nextStepIcon);

        JButton resumeBtn = new JButton();
        resumeBtn.setToolTipText("알고리즘 계속 진행");
        resumeBtn.setEnabled(false);
        ImageIcon resumeIcon = ImageIconLoader.getImageIcon("/resume.png", 24);
        resumeBtn.setIcon(resumeIcon);

        JButton stopBtn = new JButton();
        stopBtn.setToolTipText("알고리즘 중단");
        stopBtn.setEnabled(false);
        ImageIcon stopIcon = ImageIconLoader.getImageIcon("/stop.png", 24);
        stopBtn.setIcon(stopIcon);

        JButton DFSBtn = new JButton("깊이 우선");
        JButton BFSBtn = new JButton("너비 우선");
        JButton dijkstraBtn = new JButton("Dijkstra");
        JButton floydBtn = new JButton("Floyd");
        JButton kruskalBtn = new JButton("Kruskal");
        JButton primBtn = new JButton("Prim");

        final JButton[] algorithmBtns = {DFSBtn, BFSBtn, dijkstraBtn, floydBtn, kruskalBtn, primBtn};

        controlBtns = new JButton[]{nextStepBtn, resumeBtn, stopBtn};
        listener = new IAlgorithmListener() {
            @Override
            public void onAlgorithmStarted() {
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(true);
                gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

                for (JButton btn : algorithmBtns) {
                    btn.setEnabled(false);
                }
                for (JButton btn : controlBtns) {
                    btn.setEnabled(true);
                }
            }

            @Override
            public void onAlgorithmFinished() {
                gVisualPanelWrapper.getgVisualPanel().setAlgorithmRunning(false);
                gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.DEFAULT);

                for (JButton btn : algorithmBtns) {
                    btn.setEnabled(true);
                }
                for (JButton btn : controlBtns) {
                    btn.setEnabled(false);
                }
            }
        };

        DFSBtn.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                showMessageDialog(null, "그래프가 비어있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            GraphNode startNode = selectStartNode();
            if (startNode == null) return;

            SwingUtilities.invokeLater(() -> {
                var dfs = new DFSAlgorithm(gVisualPanelWrapper, startNode, listener);
                dfs.start();
            });
        });

        BFSBtn.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                showMessageDialog(null, "그래프가 비어있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            for (JButton btn : algorithmBtns) {
                btn.setEnabled(false);
            }

            GraphNode startNode = selectStartNode();
            if (startNode == null) return;

            SwingUtilities.invokeLater(() -> {
                var bfs = new BFSAlgorithm(gVisualPanelWrapper, startNode, listener);
                bfs.start();
            });
        });

        dijkstraBtn.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                showMessageDialog(null, "그래프가 비어있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().size() == 1) {
                showMessageDialog(null, "노드가 2개 이상이어야 합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            // 시작 노드와 끝 노드를 선택한다.
            GraphNode[] nodes = selectStartEndNodes();
            if (nodes == null) return;
            GraphNode startNode = nodes[0];
            GraphNode endNode = nodes[1];

            SwingUtilities.invokeLater(() -> {
                DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(gVisualPanelWrapper, startNode, endNode, listener);
                dijkstraAlgorithm.start();
            });
        });

        floydBtn.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                showMessageDialog(null, "그래프가 비어있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().size() == 1) {
                showMessageDialog(null, "노드가 2개 이상이어야 합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            // 시작 노드와 끝 노드를 선택한다.
            GraphNode[] nodes = selectStartEndNodes();
            if (nodes == null) return;
            GraphNode startNode = nodes[0];
            GraphNode endNode = nodes[1];

            SwingUtilities.invokeLater(() -> {
                FloydAlgorithm floydAlgorithm = new FloydAlgorithm(gVisualPanelWrapper, startNode, endNode, listener);
                floydAlgorithm.start();
            });
        });

        kruskalBtn.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                showMessageDialog(null, "그래프가 비어있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            SwingUtilities.invokeLater(() -> {
                KruskalAlgorithm kruskalAlgorithm = new KruskalAlgorithm(gVisualPanelWrapper, listener);
                kruskalAlgorithm.start();
            });
        });

        primBtn.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                showMessageDialog(null, "그래프가 비어있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);

            GraphNode startNode = selectStartNode();
            if (startNode == null) return;

            SwingUtilities.invokeLater(() -> {
                PrimAlgorithm primAlgorithm = new PrimAlgorithm(gVisualPanelWrapper, startNode, listener);
                primAlgorithm.start();
            });
        });

        // proceed current algorithm to next step
        nextStepBtn.addActionListener(e -> {
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm".toUpperCase())) {
                    var ga = (GraphAlgorithm) t;
                    ga.singleStep();
                }
            }
        });

        resumeBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.ALGORITHM_MODE);
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm".toUpperCase())) {
                    var ga = (GraphAlgorithm) t;
                    ga.resumeAlgorithm();
                }
            }
        });

        stopBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.DEFAULT);
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("Algorithm".toUpperCase())) {
                    t.interrupt();
                    gVisualPanelWrapper.getgVisualPanel().resetGraph();
                }
            }
            showMessageDialog(null, "알고리즘을 중단합니다.", "알고리즘 중단", JOptionPane.WARNING_MESSAGE);
            listener.onAlgorithmFinished();
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 알고리즘 버튼을 패널에 추가한다.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 4, 4);

        ImageIcon traversalIcon = ImageIconLoader.getImageIcon("/traversal.png", 32);
        ImageIcon shortestPathIcon = ImageIconLoader.getImageIcon("/path.png", 32);
        ImageIcon mstIcon = ImageIconLoader.getImageIcon("/mst.png", 32);

        algorithmButtonsPanel = new JPanel();
        algorithmButtonsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "알고리즘", TitledBorder.LEFT, TitledBorder.TOP));
        algorithmButtonsPanel.setLayout(new GridLayout(3, 3, 4, 4));
        algorithmButtonsPanel.add(new JLabel("그래프 순회", traversalIcon, JLabel.CENTER));
        algorithmButtonsPanel.add(DFSBtn);
        algorithmButtonsPanel.add(BFSBtn);
        algorithmButtonsPanel.add(new JLabel("최단 경로 탐색", shortestPathIcon, JLabel.CENTER));
        algorithmButtonsPanel.add(dijkstraBtn);
        algorithmButtonsPanel.add(floydBtn);
        algorithmButtonsPanel.add(new JLabel("MST 생성", mstIcon, JLabel.CENTER));
        algorithmButtonsPanel.add(kruskalBtn);
        algorithmButtonsPanel.add(primBtn);
        add(algorithmButtonsPanel, gbc);
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
        endNodeCbx.setSelectedItem(nodeNames[1]);
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

    public JButton[] getControlBtns() {
        return controlBtns;
    }
    public JPanel getAlgorithmButtonsPanel() {
        return algorithmButtonsPanel;
    }


    private final GVisualPanelWrapper gVisualPanelWrapper;

    private final JButton[] controlBtns;


    private final JPanel algorithmButtonsPanel;

    private final IAlgorithmListener listener;
}
