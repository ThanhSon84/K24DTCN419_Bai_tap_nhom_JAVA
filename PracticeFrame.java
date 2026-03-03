import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PracticeFrame extends JFrame {

    private List<Flashcard> cards;
    private int currentIndex = 0;
    private int score = 0;

    private JLabel questionLabel;
    private JLabel instructionLabel;
    private JRadioButton[] options;
    private ButtonGroup group;

    private JButton submitButton;
    private JButton nextButton;

    private JProgressBar progressBar;

    private List<String> currentOptions;
    private String correctAnswer;

    public PracticeFrame(List<Flashcard> shuffledCards) {

        this.cards = shuffledCards;

        setTitle("Trắc nghiệm ôn tập");
        setSize(540, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Background
        Color background = new Color(248, 245, 252);
        getContentPane().setBackground(background);
        setLayout(new BorderLayout());

        // ===== Card Panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40),
                BorderFactory.createLineBorder(new Color(230, 225, 240))
        ));

        // ===== Câu hỏi =====
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        questionLabel.setForeground(new Color(70, 60, 120)); // tím đậm
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        cardPanel.add(questionLabel);

        // ==== Action cần làm ====
        instructionLabel = new JLabel("Vui lòng chọn nghĩa tiếng Việt", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(new Color(130, 120, 170)); // pastel tím nhạt
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        cardPanel.add(instructionLabel);


        // ===== Options =====
        options = new JRadioButton[4];
        group = new ButtonGroup();

        for (int i = 0; i < 4; i++) {

            options[i] = new JRadioButton();
            options[i].setFont(new Font("Segoe UI", Font.PLAIN, 16));
            options[i].setForeground(new Color(60, 60, 60)); // chữ đậm
            options[i].setBackground(Color.WHITE);
            options[i].setFocusPainted(false);
            options[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            options[i].setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

            group.add(options[i]);
            cardPanel.add(options[i]);
            cardPanel.add(Box.createVerticalStrut(12));
        }

        add(cardPanel, BorderLayout.CENTER);

        // ===== Bottom Panel =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(background);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 25, 40));

        // ===== Progress Bar =====
        progressBar = new JProgressBar(0, cards.size());
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(190, 180, 255)); // lavender nhạt
        progressBar.setBackground(Color.WHITE);
        progressBar.setPreferredSize(new Dimension(100, 18));

        bottomPanel.add(progressBar, BorderLayout.NORTH);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(background);

        submitButton = pastelButton("Submit");
        nextButton = pastelButton("Next");

        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion();

        submitButton.addActionListener(e -> checkAnswer());
        nextButton.addActionListener(e -> nextQuestion());

        setVisible(true);
    }

    // ==============================
    // Load Question
    // ==============================
    private void loadQuestion() {

        Flashcard current = cards.get(currentIndex);
        questionLabel.setText("Từ tiếng Anh: " + current.getWord());

        correctAnswer = current.getMeaning();

        currentOptions = new ArrayList<>();
        currentOptions.add(correctAnswer);

        Random rand = new Random();
        int maxOptions = Math.min(4, cards.size());

        while (currentOptions.size() < maxOptions) {
            String wrong = cards.get(rand.nextInt(cards.size())).getMeaning();
            if (!currentOptions.contains(wrong)) {
                currentOptions.add(wrong);
            }
        }

        Collections.shuffle(currentOptions);

        for (JRadioButton option : options) {
            option.setVisible(false);
        }

        for (int i = 0; i < currentOptions.size(); i++) {
            options[i].setText(currentOptions.get(i));
            options[i].setVisible(true);
            options[i].setSelected(false);
        }

        group.clearSelection();

        progressBar.setValue(currentIndex + 1);
        progressBar.setString((currentIndex + 1) + " / " + cards.size());
    }

    // ==============================
    // Check Answer
    // ==============================
    private void checkAnswer() {

        for (JRadioButton option : options) {
            if (option.isVisible() && option.isSelected()) {

                if (option.getText().equals(correctAnswer)) {
                    score++;
                    JOptionPane.showMessageDialog(this,
                            "Chính xác");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Sai rồi :( \nĐáp án đúng là: " + correctAnswer);
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Hãy chọn nghĩa đúng của từ trong tiếng Việt nha.");
    }

    // ==============================
    // Next Question
    // ==============================
    private void nextQuestion() {

        if (currentIndex < cards.size() - 1) {
            currentIndex++;
            loadQuestion();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ôn tập hoàn tất! \nĐiểm của bạn là: " + score + "/" + cards.size());
            dispose();
        }
    }

    // ==============================
    // Pastel Button 
    // ==============================
    private JButton pastelButton(String text) {

        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(200, 190, 255)); // lavender pastel
        button.setForeground(new Color(50, 50, 80));    // chữ đậm
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        button.setOpaque(true);
        button.setBorderPainted(false);

        return button;
    }
}