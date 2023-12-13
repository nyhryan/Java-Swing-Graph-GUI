package components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class GQuizPanel extends JPanel {
    private int score = 0; // 사용자의 점수를 기록합니다.
    public class QuizData {
        private String imagePath;
        private String answer;
        private String problemDescription; // 추가된 부분: 문제 설명을 담는 변수
        private List<String> options;

        // 생성자에서 problemDescription을 받아와서 할당
        public QuizData(String imagePath, String answer, String problemDescription, String... options) {
            this.imagePath = imagePath;
            this.answer = answer;
            this.problemDescription = problemDescription;
            this.options = Arrays.asList(options);
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getAnswer() {
            return answer;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getProblemDescription() {
            return problemDescription;
        }

        public void setProblemDescription(String problemDescription) {
            this.problemDescription = problemDescription;
        }
    }

    private Iterator<QuizData> quizIterator;

    public GQuizPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 퀴즈 데이터를 생성합니다.
        List<QuizData> quizList = Arrays.asList(
                new QuizData("Quiz/Q1.png", "정답1", "문제1: 이것은 첫 번째 문제입니다.", "선택지1-1", "선택지1-2", "선택지1-3", "선택지1-4"),
                new QuizData("Quiz/Q2.png", "정답2", "문제2: 이것은 두 번째 문제입니다.", "선택지2-1", "선택지2-2", "선택지2-3", "선택지2-4"),
                new QuizData("Quiz/Q3.png", "정답3", "문제2: 이것은 두 번째 문제입니다.", "선택지2-1", "선택지2-2", "선택지2-3", "선택지2-4"),
                new QuizData("Quiz/Q4.png", "정답4", "문제2: 이것은 두 번째 문제입니다.", "선택지2-1", "선택지2-2", "선택지2-3", "선택지2-4"),
                new QuizData("Quiz/Q5.png", "정답5", "문제2: 이것은 두 번째 문제입니다.", "선택지2-1", "선택지2-2", "선택지2-3", "선택지2-4"),
                new QuizData("Quiz/Q6.png", "정답6", "문제2: 이것은 두 번째 문제입니다.", "선택지2-1", "선택지2-2", "선택지2-3", "선택지2-4"),
                new QuizData("Quiz/Q7.png", "정답7", "문제2: 이것은 두 번째 문제입니다.", "선택지2-1", "선택지2-2", "선택지2-3", "선택지2-4")
                // ... 나머지 퀴즈들
        );
        Collections.shuffle(quizList);
        quizIterator = quizList.iterator();

        if (quizIterator.hasNext()) {
            add(createQuizPanel(quizIterator.next()));
        }
    }

    private JPanel createQuizPanel(QuizData quiz) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        ImageIcon imageIcon = new ImageIcon(quiz.getImagePath());
        JLabel imageLabel = new JLabel(imageIcon);
        panel.add(imageLabel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        JTextArea problemDescriptionArea = new JTextArea(quiz.getProblemDescription());
        problemDescriptionArea.setEditable(false);
        problemDescriptionArea.setLineWrap(true);
        problemDescriptionArea.setWrapStyleWord(true);
        problemDescriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bottomPanel.add(problemDescriptionArea);
        for (String option : quiz.getOptions()) {
            JCheckBox checkBox = new JCheckBox(option);
            checkBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
            bottomPanel.add(checkBox);
        }

        JButton submitBtn = new JButton("답안 제출하기");
        submitBtn.setPreferredSize(new Dimension(120, 80));
        submitBtn.addActionListener(e -> {
            remove(panel);

            if (quizIterator.hasNext()) {
                add(createQuizPanel(quizIterator.next()));
            } else {
                setLayout(new BorderLayout());

                JPanel resultPanel = new JPanel();
                resultPanel.setLayout(new BorderLayout());

                JLabel scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER); // 수평 정렬을 중앙으로 설정
                scoreLabel.setFont(new Font(scoreLabel.getFont().getName(), Font.PLAIN, 30)); // 폰트 크기를 30으로 설정
                resultPanel.add(scoreLabel, BorderLayout.NORTH); // 프레임의 North 영역에 레이블 추가

                // 다시 학습하기 버튼 생성
                JButton retryButton = new JButton("다시 학습하기");
                retryButton.setPreferredSize(new Dimension(120, 50)); // 버튼의 사이즈를 설정

                // 버튼을 추가할 JPanel 생성
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(retryButton); // 버튼을 JPanel에 추가

                resultPanel.add(buttonPanel, BorderLayout.SOUTH);


                ImageIcon resultImage;
                String resultMessage;
                File soundFile;
                if (score > 5) {
                    resultImage = new ImageIcon("/Quiz/good_image.png");
                    resultMessage = "축하합니다! 잘하셨어요!";
                    soundFile = new File("Quiz/goodsound.wav");
                } else {
                    resultImage = new ImageIcon("Quiz/bad_image.png");
                    resultMessage = "조금 더 연습해봅시다!";
                    soundFile = new File("Quiz/badsound.wav");
                }

                JLabel resultLabel = new JLabel(resultMessage, resultImage, JLabel.CENTER);
                resultLabel.setFont(new Font("Serif", Font.BOLD, 24));
                resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                resultPanel.add(resultLabel, BorderLayout.CENTER);

                // 사운드 자동 재생
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                add(resultPanel, BorderLayout.CENTER);
            }
            revalidate();
            repaint();
        });

        panel.add(bottomPanel, BorderLayout.CENTER);
        panel.add(submitBtn, BorderLayout.SOUTH);

        return panel;
    }

}
