package components;

import graph.Graph;
import graph.GraphEdge;
import tools.ImageIconLoader;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class GInfoPanel extends JPanel {
    public GInfoPanel(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 그래프 정보를 출력할 창에 CSS를 적용한다.
        HTMLEditorKit kit = new HTMLEditorKit();
        UIManager.put("EditorPane.foreground", Color.BLACK);

        InputStream is = GInfoPanel.class.getResourceAsStream("/D2Coding-Ver1.3.2-20180524.ttf");

        if (is != null) {
            Font D2Coding = null;
            try {
                D2Coding = Font.createFont(Font.TRUETYPE_FONT, is);
            } catch (FontFormatException | IOException e) {
                throw new RuntimeException(e);
            }
            UIManager.put("EditorPane.font", D2Coding.deriveFont(12f));
        }
        kit.getStyleSheet().addRule("span, p { margin: 0; padding: 0; font-size: 12px; } li { font-size: 12px; }");
        kit.getStyleSheet().addRule("table { table-layout: fixed; } table, th, td { border: 1px solid black; font-size: 14px; }");

        // 그래프 정보를 출력할 JEditorPane
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setEditorKit(kit);
        editorPane.setBackground(Color.WHITE);

        // 그래프 정보를 출력할 JEditorPane을 스크롤 패널에 추가한다.
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(300, 900));
        add(scrollPane);

        JPanel randomGraphPanel = new JPanel();
        randomGraphPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "랜덤 그래프", TitledBorder.LEFT, TitledBorder.TOP));
        randomGraphPanel.setLayout(new GridLayout(4, 2, 4, 4));

        // 랜덤 그래프에서 간선을 그릴 확률 입력 스피너
        JSpinner edgeSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));
        gVisualPanelWrapper.getgVisualPanel().setRandomEdgeProbability(((Double) edgeSpinner.getValue()).floatValue());
        edgeSpinner.addChangeListener(e -> {
            JSpinner _spinner = (JSpinner) e.getSource();
            gVisualPanelWrapper.getgVisualPanel().setRandomEdgeProbability(((Double) _spinner.getValue()).floatValue());
        });

        JSpinner nodesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        gVisualPanelWrapper.getgVisualPanel().setRandomNodeCount((Integer) nodesSpinner.getValue());
        nodesSpinner.addChangeListener(e -> {
            JSpinner _spinner = (JSpinner) e.getSource();
            gVisualPanelWrapper.getgVisualPanel().setRandomNodeCount((Integer) _spinner.getValue());
        });

        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(9.0, 1.0, 10.0, 1.0));
        gVisualPanelWrapper.getgVisualPanel().setRandomWeightMax((Double) weightSpinner.getValue());
        weightSpinner.addChangeListener(e -> {
            JSpinner _spinner = (JSpinner) e.getSource();
            gVisualPanelWrapper.getgVisualPanel().setRandomWeightMax((Double) _spinner.getValue());
        });

        // 랜덤 그래프 생성 버튼
        ImageIcon randomIcon = ImageIconLoader.getImageIcon("/random.png", 16);
        JButton randomBtn = new JButton(" 랜덤 그래프 생성");
        randomBtn.setToolTipText("랜덤 그래프를 생성합니다.");
        randomBtn.addActionListener(e -> {
            gVisualPanelWrapper.getgVisualPanel().setMode(GVisualPanel.Mode.DEFAULT);
            gVisualPanelWrapper.getgVisualPanel().setRandomGraph();
        });
        randomBtn.setIcon(randomIcon);
        randomGraphPanel.add(new JLabel("간선 그릴 확률"));
        randomGraphPanel.add(edgeSpinner);
        randomGraphPanel.add(new JLabel("노드 개수"));
        randomGraphPanel.add(nodesSpinner);
        randomGraphPanel.add(new JLabel("가중치 최대 값"));
        randomGraphPanel.add(weightSpinner);
        randomGraphPanel.add(randomBtn);

        add(randomGraphPanel);

        // 알고리즘 버튼들과 애니메이션 속도 슬라이더 등을 모은 패널
        GAlgorithmButtons algorithmBtns = new GAlgorithmButtons(gVisualPanelWrapper);
        JPanel algorithmPanel = algorithmBtns.getAlgorithmButtonsPanel();
        algorithmPanel.setSize(new Dimension(300, 300));
        add(algorithmPanel);

        // 애니메이션 속도 조절
        JLabel animSpeedLabel = new JLabel("애니메이션 속도(ms): ");
        JSpinner animSpeed = new JSpinner(new SpinnerNumberModel(500, 1, 10000, 100));
        gVisualPanelWrapper.getgVisualPanel().setAnimationSpeed((int) animSpeed.getValue());

        animSpeed.addChangeListener(e -> {
            int value = (int) animSpeed.getValue();
            gVisualPanelWrapper.getgVisualPanel().setAnimationSpeed(value);
        });

        JButton[] algorithmControlBtns = algorithmBtns.getControlBtns();
        JPanel controlBtnsPanel = new JPanel();
        controlBtnsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "알고리즘 제어 / 애니메이션 속도", TitledBorder.LEFT, TitledBorder.TOP));

        controlBtnsPanel.add(animSpeedLabel);
        controlBtnsPanel.add(animSpeed);
        for (var btn : algorithmControlBtns) {
            controlBtnsPanel.add(btn);
        }
        add(controlBtnsPanel);

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

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public void setEditorPaneText(String text) {
        editorPane.setText(text);
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final JEditorPane editorPane;
}
