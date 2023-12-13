package components;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.lang.reflect.Method;

public class GMenuBar extends JMenuBar {
    public GMenuBar(GVisualPanelWrapper gVisualPanelWrapper) {
        JMenu fileMenu = new JMenu("File");

        // 그래프 파일 열기, 저장, 종료 메뉴
        JMenuItem openMenuItem = new JMenuItem("Open Graph...");
        openMenuItem.addActionListener(e -> {
            if (gVisualPanelWrapper.getgVisualPanel().isAlgorithmRunning()) {
                JOptionPane.showMessageDialog(null, "알고리즘 실행 중에는 불러올 수 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

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
            if (gVisualPanelWrapper.getgVisualPanel().isAlgorithmRunning()) {
                JOptionPane.showMessageDialog(null, "알고리즘 실행 중에는 저장할 수 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

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

        JMenu customizeMenu = new JMenu("Edit");
        add(customizeMenu);

        JMenuItem themesMenuItem = new JMenu("Themes");
        var themes = FlatAllIJThemes.INFOS;
        for (var theme : themes) {
            JMenuItem themeMenuItem = new JMenuItem(theme.getName());
            themeMenuItem.addActionListener(e -> {
                if (gVisualPanelWrapper.getgVisualPanel().isAlgorithmRunning()) {
                    JOptionPane.showMessageDialog(null, "알고리즘 실행 중에는 테마를 변경할 수 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Object themeObj = Class.forName(theme.getClassName()).getDeclaredConstructor().newInstance();
                    Method installMethod = themeObj.getClass().getMethod("setup");
                    installMethod.invoke(themeObj);
                    Color c = theme.isDark() ? new Color(0x222222) : new Color(0xF8F8F8);
                    gVisualPanelWrapper.getgVisualPanel().setBackground(c);

                    var edges = gVisualPanelWrapper.getgVisualPanel().getGraph().getEdges();
                    if (theme.isDark()) {
                        for (var edge : edges) {
                            edge.setStrokeColor(Color.WHITE);
                            edge.setTextColor(Color.BLACK);

                        }
                    } else {
                        for (var edge : edges) {
                            edge.setStrokeColor(Color.BLACK);
                            edge.setTextColor(Color.WHITE);
                        }
                    }

                    gVisualPanelWrapper.getgVisualPanel().repaint();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    FlatLaf.updateUI();
                }
            });
            themesMenuItem.add(themeMenuItem);
        }
        customizeMenu.add(themesMenuItem);
    }
}
