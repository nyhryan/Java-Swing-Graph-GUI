package components;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;


public class GQuizPanel extends JPanel {
    private final GVisualPanelWrapper gVisualPanelWrapper;
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

    public GQuizPanel(GVisualPanelWrapper gVisualPanelWrapper) {
        this.gVisualPanelWrapper = gVisualPanelWrapper;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 퀴즈 데이터를 생성합니다.
        List<QuizData> quizList = Arrays.asList(
                new QuizData("/Quiz/Q1.png", "B - 36", "나그네가 우물로 물을 뜨러 가려고 합니다. 화살표가 표시된 우물까지 우물과 우물을 지나서 가려고 합니다. 그때 총 거리의 합과 지나지 않는 우물이 바르게 짝지어 진것은?", "A - 45", "C - 36", "B - 37", "B - 36"),
                new QuizData("/Quiz/Q2.png", "4 - 36", "언제나 위험한 바닷속에 사는 물고기는 언제나 도망갈 길을 만들어 둔다. 물고기가 위험에 처했을때 도망갈 곳이 별표가 있는 산호일때 물고기는 최단거리로 간다고 할때 지나는 산호의 갯수와 거리가 바르게 짝지어 진것은?", "4 - 36", "5 - 29", "3 - 34", "2 - 38"),
                new QuizData("/Quiz/Q3.png", "24", "방이 어두워 전등을 켜려고 한다. 스위치를 누르면 별표가 있는 전등까지 최단거리로 켜진다고 할때 지나는 전선의 길이는 얼마인가?", "25", "24", "27", "29"),
                new QuizData("/Quiz/Q4.png", "17", "강아지가 공놀이 중이다. 이 강아지는 공을 가져오기 위해 최소비용 신장트리를 따라 움직인다고 한다. 그렇다면 강아지가 제자리에서 'E' 공을 가져오기 위해 가야하는 거리는 얼마인가?", "15", "16", "17", "18"),
                new QuizData("/Quiz/Q5.png", "G", "철수는 길을 잃었다. 기억나는 것을 각 아파트와 학교를 이은 그래프를 Kruskal알고리즘에 따라 최소비용 신장 트리를 만들다 보면 학교 다음으로 선택된 아파트라고 할때 철수의 집은 어디인가?", "A", "B", "E", "G"),
                new QuizData("/Quiz/Q6.png", "A-C-G-D-E-F-B-H", "하늘의 별을 보고 prim 알고리즘에 따라 별자리를 만들려고 할때 선택된 별의 순서로 알맞은 것은?", "A-B-C-D-E-F-G-H", "A-C-G-D-E-F-B-H", "G-D-A-B-E-C-F-H", "H-B-F-E-D-G-C-A"),
                new QuizData("/Quiz/Q7.png", "3", "이 배는 prim 알고리즘에 따라 부표로 향해 작업을 한다. 이때 표시된 부표까지 몇개의 작업을 시행하며 넘어오는가?", "1", "2", "3", "4")
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
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(quiz.getImagePath()));
        int imageW = imageIcon.getIconWidth();
        int imageH = imageIcon.getIconHeight();

        int winH = gVisualPanelWrapper.getHeight();
        int newH = (int) (winH * 0.6);
        int newW = (int) (imageW * ((double) newH / imageH));
        // make image almost fit to the window
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH));

        JLabel imageLabel = new JLabel(imageIcon);
        panel.add(imageLabel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        JTextArea problemDescriptionArea = new JTextArea(quiz.getProblemDescription());
        problemDescriptionArea.setEditable(false);
        problemDescriptionArea.setLineWrap(true);
        problemDescriptionArea.setWrapStyleWord(true);
//        problemDescriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 30));
        problemDescriptionArea.setFont(gVisualPanelWrapper.getFont().deriveFont(30f));
        bottomPanel.add(problemDescriptionArea);
        for (String option : quiz.getOptions()) {
            JCheckBox checkBox = new JCheckBox(option);
            checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
//            checkBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
            checkBox.setFont(gVisualPanelWrapper.getFont().deriveFont(24f));
            bottomPanel.add(checkBox);
        }

        JButton submitBtn = new JButton("답안 제출하기");
        submitBtn.setFont(gVisualPanelWrapper.getFont().deriveFont(20f));
        submitBtn.setPreferredSize(new Dimension(120, 80));
        submitBtn.addActionListener(e -> {
            JButton src = (JButton) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(src.getParent());
            frame.getContentPane().removeAll();
//            remove(panel);
            for (Component component : bottomPanel.getComponents()) {
                if (component instanceof JCheckBox checkBox) {
                    // 체크박스가 선택되었고, 선택한 옵션이 정답과 같은 경우에 점수를 증가시킵니다.
                    if (checkBox.isSelected() && checkBox.getText().equals(quiz.getAnswer())) {
                        score++; // 선택한 옵션이 정답인 경우 점수를 증가시킴
                    }
                }
            }

            if (quizIterator.hasNext()) {
                frame.getContentPane().add(createQuizPanel(quizIterator.next()));
            }
            else {
                setLayout(new BorderLayout());

                JPanel resultPanel = new JPanel();
                resultPanel.setLayout(new BorderLayout());

                JLabel scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER); // 수평 정렬을 중앙으로 설정
//                scoreLabel.setFont(new Font(scoreLabel.getFont().getName(), Font.PLAIN, 30)); // 폰트 크기를 30으로 설정
                scoreLabel.setFont(gVisualPanelWrapper.getFont().deriveFont(Font.PLAIN, 30f));
                resultPanel.add(scoreLabel, BorderLayout.NORTH); // 프레임의 North 영역에 레이블 추가

                ImageIcon resultImage;
                String resultMessage;
//                File soundFile;
                // JAR 파일로 묶으면 ...jar!/Quiz/goodsound.wav 이런 식으로 경로가 나옴
                // 따라서 getAudioInputStream()에 경로를 URL로 직접 전달함.
                String soundFilePath;
                if (score > 5) {
                    resultImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Quiz/good_image.png")));
                    resultMessage = "축하합니다! 잘하셨어요!";
//                    soundFile = new File(Objects.requireNonNull(getClass().getResource("/Quiz/goodsound.wav")).getFile());
                    soundFilePath = "/Quiz/goodsound.wav";
                } else {
                    resultImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Quiz/bad_image.png")));
                    resultMessage = "조금 더 연습해봅시다!";
//                    soundFile = new File(Objects.requireNonNull(getClass().getResource("/Quiz/badsound.wav")).getFile());
                    soundFilePath = "/Quiz/badsound.wav";
                }

                JLabel resultLabel = new JLabel(resultMessage, SwingConstants.CENTER);
//                resultLabel.setFont(new Font("Serif", Font.BOLD, 24));
                resultLabel.setFont(gVisualPanelWrapper.getFont().deriveFont(24f));
//                resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JPanel resultImagePanel = new JPanel();
                resultImagePanel.setLayout(new BorderLayout());
                resultImagePanel.add(resultLabel, BorderLayout.NORTH);
                resultImagePanel.add(new JLabel(resultImage), BorderLayout.CENTER);

                resultPanel.add(resultImagePanel, BorderLayout.CENTER);

                // 사운드 자동 재생
                AudioInputStream audioInputStream = null;
                Clip clip = null;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(getClass().getResource(soundFilePath)));
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                frame.getContentPane().add(resultPanel, BorderLayout.CENTER);

                // 다시 학습하기 버튼 생성
                JButton retryButton = new JButton("다시 학습하기");
                retryButton.setFont(gVisualPanelWrapper.getFont().deriveFont(20f));
//                retryButton.setPreferredSize(new Dimension(120, 50)); // 버튼의 사이즈를 설정
                Clip finalClip = clip;
                AudioInputStream finalAudioInputStream = audioInputStream;
                retryButton.addActionListener(event -> {
                    assert finalClip != null;
                    if (finalClip.isActive()) {
                        finalClip.stop();
                        try {
                            finalAudioInputStream.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(new GQuizPanel(gVisualPanelWrapper));
                    frame.revalidate();
                    frame.repaint();
                });

                JButton graphButton = new JButton("그래프 모드로 가기");
                graphButton.setFont(gVisualPanelWrapper.getFont().deriveFont(20f));
//                graphButton.setPreferredSize(new Dimension(150, 50)); // 버튼의 사이즈를 설정
                graphButton.addActionListener(event -> {
                    assert finalClip != null;
                    if (finalClip.isActive()) {
                        finalClip.stop();
                        try {
                            finalAudioInputStream.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    frame.getContentPane().removeAll();
                    GVisualPanelWrapper _gVisualPanelWrapper = new GVisualPanelWrapper();
                    frame.getContentPane().add(_gVisualPanelWrapper);
                    frame.setJMenuBar(new GMenuBar(_gVisualPanelWrapper));
                    frame.revalidate();
                    frame.repaint();
                });

                // 버튼을 추가할 JPanel 생성
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(retryButton); // 버튼을 JPanel에 추가
                buttonPanel.add(graphButton); // 버튼을 JPanel에 추가
                resultPanel.add(buttonPanel, BorderLayout.SOUTH);
            }
            frame.revalidate();
            frame.repaint();
        });

        panel.add(bottomPanel, BorderLayout.CENTER);
        panel.add(submitBtn, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new BorderLayout());
        returnPanel.add(scrollPane, BorderLayout.CENTER);
        return returnPanel;
    }

}
