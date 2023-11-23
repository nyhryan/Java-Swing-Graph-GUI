package components;

import javax.swing.*;

public class GQuizPanel extends JPanel {
    public GQuizPanel() {
        JButton returnBtn = new JButton("그래프 모드로 돌아가기");
        returnBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new GVisualPanelWrapper());
            frame.revalidate();
            frame.repaint();
        });
        add(returnBtn);
    }
}
