package components;

import algorithm.TestAlgorithm;
import graph.Graph;
import graph.GraphEdge;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 그래프 정보를 표시하는 패널
 */
class GInfoPanel extends JPanel {
    /**
     * @param gVisualPanelWrapper 이 패널을 가지는 부모 래퍼 클래스 레퍼런스
     */
    public GInfoPanel(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;

        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        adjacencyList = graph.getAdjacencyList();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 그래프 정보를 출력할 창에 CSS를 적용한다.
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule("span, p { margin: 0; padding: 0; font-size: 14px; } li { font-size: 14px; }");

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
        JPanel buttonPanel = new JPanel();
        JButton testBtn = new JButton("Test");

        testBtn.addActionListener(e -> {
            // 버튼을 누르면 알고리즘 테스트를 위한 쓰레드 생성 및 시작
            SwingUtilities.invokeLater(() -> {
                Thread t = new Thread(new TestAlgorithm(gVisualPanelWrapper));
                t.start();
            });
        });
        
        // 알고리즘 버튼을 임시로 추가
        buttonPanel.add(testBtn);
        buttonPanel.add(new JButton("2"));
        buttonPanel.add(new JButton("3"));
        buttonPanel.add(new JButton("4"));

        add(buttonPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);

        if (graph.getNodes() == null) return;

        // print adjacency list to current JEditorPane
        StringBuilder sb = new StringBuilder();
        int edgeCount = 0;
        for (int i = 0; i < adjacencyList.size(); i++) {
            LinkedList<GraphEdge> edges = adjacencyList.get(i);
            sb.append(String.format("<p>%s : ", graph.getNodes().get(i).getName()));

            if (edges.size() == 0) {
                sb.append("</p>\n");
                continue;
            }

            for (GraphEdge edge : edges) {
                sb.append(String.format("\n<span>%s (%.1f)</span> → ", edge.getTo().getName(), edge.getWeight()));
                edgeCount++;
            }
            sb.append("\n<span>null</span>\n</p>\n");
        }

        if (adjacencyList.size() != 0) {
            sb.append("<hr><ul><li>노드 개수: ").append(adjacencyList.size()).append("</li>");
            sb.append("<li>간선 개수: ").append(edgeCount / 2).append("</li></ul>");
        }

        editorPane.setText("<html><body><h1>Adjacency List</h1><hr>\n" + sb);
//        editorPane.setText(editorPane.getText() + "<hr><p>노드 개수: " + adjacencyList.size() + "</p></body></html>");
    }

    private final GVisualPanelWrapper gVisualPanelWrapper;
    private final Graph graph;
    private final ArrayList<LinkedList<GraphEdge>> adjacencyList;
    private final JEditorPane editorPane;
}
