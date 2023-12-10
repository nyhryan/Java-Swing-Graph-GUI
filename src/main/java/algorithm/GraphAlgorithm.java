package algorithm;

import components.GVisualPanelWrapper;

import javax.swing.*;
import java.util.concurrent.Semaphore;

public abstract class GraphAlgorithm extends Thread {
    public GraphAlgorithm(GVisualPanelWrapper gVisualPanelWrapper, String name) {
        super(name);
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        semaphore = gVisualPanelWrapper.semaphore;
        semaphore.drainPermits();
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

    protected final GVisualPanelWrapper gVisualPanelWrapper;
    protected final Semaphore semaphore;
}
