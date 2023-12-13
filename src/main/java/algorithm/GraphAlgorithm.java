package algorithm;

import components.GVisualPanelWrapper;
import graph.GraphEdge;
import graph.GraphNode;
import tools.RandomColor;
import graph.Graph;

import javax.swing.*;
import java.awt.*;
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

    public void startAlgorithm() {
        semaphore.release();
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

    protected final IAlgorithmListener listener;
    protected final GVisualPanelWrapper gVisualPanelWrapper;
    protected final Semaphore semaphore;
    protected final Graph graph;
    protected boolean isCompleted = false;

    protected final Color COLOR_1 = RandomColor.getRandomColor();
    protected final Color COLOR_1_TEXT = RandomColor.getCorrectTextColor(COLOR_1);
    protected final Color COLOR_2 = RandomColor.getRandomColor();
    protected final Color COLOR_2_TEXT = RandomColor.getCorrectTextColor(COLOR_2);
    protected final Color COLOR_3 = RandomColor.getRandomColor();
    protected final Color COLOR_3_TEXT = RandomColor.getCorrectTextColor(COLOR_3);
}
