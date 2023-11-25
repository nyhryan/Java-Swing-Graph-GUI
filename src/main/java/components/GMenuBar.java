package components;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GMenuBar extends JMenuBar {
    public GMenuBar(GVisualPanelWrapper gVisualPanelWrapper) {
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open Graph...");
        openMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Graph");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Graph File", "graph"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                gVisualPanelWrapper.getgVisualPanel().getGraph().loadGraph(path);
                gVisualPanelWrapper.getgVisualPanel().repaint();
                gVisualPanelWrapper.getgInfoPanel().repaint();
            }
        });

        JMenuItem saveMenuItem = new JMenuItem("Save Graph...");
        saveMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Graph");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Graph File", "graph"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".graph")) {
                    path += ".graph";
                }
                gVisualPanelWrapper.getgVisualPanel().getGraph().saveGraph(path);
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        add(fileMenu);
    }
}
