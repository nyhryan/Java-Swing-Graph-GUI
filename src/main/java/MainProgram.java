import javax.swing.*;

public class MainProgram {
    public static void main(String[] args) {
        // 스윙 전용 스레드(EDT)에서 그래프 프로그램을 시작
        SwingUtilities.invokeLater(() -> new GMainFrame().setVisible(true));
    }
}
