package components;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.lang.reflect.Method;

public class GMenuBar extends JMenuBar {
    public GMenuBar(GVisualPanelWrapper gVisualPanelWrapper) {
        JMenu fileMenu = new JMenu("File");

        // 그래프 파일 열기, 저장, 종료 메뉴
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
            if (gVisualPanelWrapper.getgVisualPanel().getGraph().getNodes().isEmpty()) {
                JOptionPane.showMessageDialog(null, "그래프를 먼저 그려야합니다.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

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

        JMenu customizeMenu = new JMenu("Customize");
        JMenuItem themesMenuItem = new JMenu("Themes");
        var themes = FlatAllIJThemes.INFOS;
        for (var theme : themes) {
            JMenuItem themeMenuItem = new JMenuItem(theme.getName());
            themeMenuItem.addActionListener(e -> {
                try {
                    Object themeObj = Class.forName(theme.getClassName()).getDeclaredConstructor().newInstance();
                    Method installMethod = themeObj.getClass().getMethod("install");
                    installMethod.invoke(themeObj);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                FlatLaf.updateUI();
            });
            themesMenuItem.add(themeMenuItem);
        }


        customizeMenu.add(themesMenuItem);
        add(customizeMenu);
    }
}
