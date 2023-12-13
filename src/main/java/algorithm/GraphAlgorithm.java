package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphEdge;
import graph.GraphNode;
import tools.RandomColor;
import graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public abstract class GraphAlgorithm extends Thread {
    public GraphAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, String name, IAlgorithmListener listener) {
        super(name);
        this.listener = listener;
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        semaphore = gVisualPanelWrapper.semaphore;
        semaphore.drainPermits();

        // 그래프 초기화
        graph = gVisualPanelWrapper.getgVisualPanel().getGraph();
        graph.resetGraphProperties();
        SwingUtilities.invokeLater(() -> {
            gVisualPanelWrapper.getgVisualPanel().repaint();
            gVisualPanelWrapper.getgInfoPanel().repaint();
        });
    }

    @Override
    public abstract void run();

    protected void drawPath(ArrayList<GraphNode> path) {
        for (int i = 0; i <= path.size() - 1; i++) {
            path.get(i).setFillColor(Color.GREEN);
            if (i < path.size() - 1) {
                GraphEdge edge;
                if (graph.getNodes().indexOf(path.get(i)) > graph.getNodes().indexOf(path.get(i + 1))) {
                    edge = graph.getEdge(path.get(i + 1), path.get(i));
                } else {
                    edge = graph.getEdge(path.get(i), path.get(i + 1));
                }

                if (edge == null) {
                    break;
                }

                edge.setStrokeColor(Color.GREEN);
                edge.setTextColor(Color.BLACK);
                edge.setStrokeWidth(5.0f);
            }

            waitAndRepaint(100);
        }
    }

    protected void appendMessageToEditorPane(String msg) {
        String currentEditorPaneText = gVisualPanelWrapper.getgInfoPanel().getEditorPane().getText();
        int startIndex = currentEditorPaneText.indexOf("<body>");
        int endIndex = currentEditorPaneText.lastIndexOf("</body>");
        String content = currentEditorPaneText.substring(startIndex + 6, endIndex);
        gVisualPanelWrapper.getgInfoPanel().setEditorPaneText(
                content + String.format("<hr/><h2>탐색결과</h2>%s</html>", msg)
        );
    }

    public void singleStep() {
        semaphore.drainPermits();
        semaphore.release();
    }

    public void resumeAlgorithm() {
        semaphore.release(Integer.MAX_VALUE);
    }

    protected void waitAndRepaint() {
        try {
            sleep(gVisualPanelWrapper.getgVisualPanel().getAnimationSpeed());
            SwingUtilities.invokeLater(() -> {
                gVisualPanelWrapper.getgVisualPanel().repaint();
                gVisualPanelWrapper.getgInfoPanel().repaint();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void waitAndRepaint(int ms) {
        try {
            sleep(ms);
            SwingUtilities.invokeLater(() -> {
                gVisualPanelWrapper.getgVisualPanel().repaint();
                gVisualPanelWrapper.getgInfoPanel().repaint();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected final IAlgorithmListener listener;
    protected final GVisualPanelWrapper gVisualPanelWrapper;
    protected final Semaphore semaphore;
    protected final Graph graph;
    protected boolean isCompleted = false;

    protected final Color COLOR_1 = RandomColor.getRandomColor();
    protected final Color COLOR_1_TEXT = RandomColor.getCorrectTextColor(COLOR_1);
    protected final Color COLOR_2 = RandomColor.getRandomColor();
    protected final Color COLOR_2_TEXT = RandomColor.getCorrectTextColor(COLOR_2);
}
