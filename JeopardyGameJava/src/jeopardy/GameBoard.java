package jeopardy;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameBoard extends JFrame {

    private JButton[][] buttons;
    private int numPlayers;
    private String[] playerNames;
    private int currentPlayerIndex = 0;
    private Player[] players;
    private Scoreboard scoreboard;

    public GameBoard() {
        setTitle("Jeopardy Board");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton finishButton = new JButton("Finish");
        finishButton.setBackground(Color.RED);
        finishButton.addActionListener(e -> endGame());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(finishButton);
        add(buttonPanel, BorderLayout.SOUTH);

        String numPlayersStr = JOptionPane.showInputDialog(this, "Enter the number of players (up to 4):");
        numPlayers = Math.min(Integer.parseInt(numPlayersStr), 4);
        playerNames = new String[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            playerNames[i] = JOptionPane.showInputDialog(this, "Enter name for Player " + (i + 1) + ":");
        }

        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(playerNames[i]);
        }

        JPanel panel = new JPanel(new GridLayout(6, 5, 5, 5));
        buttons = new JButton[6][5];

        List<Question> allQuestions = loadQuestionsFromFile("resources/JeopardyQuestions.txt");
        List<String> allCategories = extractCategories(allQuestions);
        List<String> selectedCategories = selectRandomCategories(allCategories, 5);

        populateButtons(allQuestions, selectedCategories, panel);

        add(panel);
        setVisible(true);
        scoreboard = new Scoreboard(numPlayers);
    }

    private List<Question> loadQuestionsFromFile(String filename) {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String category = parts[0].trim();
                    String questionText = parts[1].trim();
                    String answer = parts[2].trim();
                    int score = Integer.parseInt(parts[3].trim());
                    questions.add(new Question(category, questionText, answer, score, numPlayers));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private List<String> extractCategories(List<Question> questions) {
        List<String> categories = new ArrayList<>();
        for (Question q : questions) {
            if (!categories.contains(q.getCategory())) {
                categories.add(q.getCategory());
            }
        }
        return categories;
    }

    private List<String> selectRandomCategories(List<String> allCategories, int numCategories) {
        List<String> selectedCategories = new ArrayList<>(allCategories);
        Collections.shuffle(selectedCategories);
        int size = Math.min(numCategories, selectedCategories.size());
        return selectedCategories.subList(0, size);
    }

    private void populateButtons(List<Question> allQuestions, List<String> selectedCategories, JPanel panel) {

        for (int i = 0; i < selectedCategories.size(); i++) {
            String category = selectedCategories.get(i);
            JButton categoryButton = new JButton(category);
            categoryButton.setPreferredSize(new Dimension(150, 100));
            categoryButton.setEnabled(false);
            panel.add(categoryButton);
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < selectedCategories.size(); j++) {
                String category = selectedCategories.get(j);
                List<Question> categoryQuestions = new ArrayList<>();
                for (Question question : allQuestions) {
                    if (question.getCategory().equals(category) && question.getScore() == ((i + 1) * 100)) {
                        categoryQuestions.add(question);
                    }
                }
                Collections.shuffle(categoryQuestions);
                if (!categoryQuestions.isEmpty()) {
                    Question q = categoryQuestions.get(0);
                    String label = "$" + ((i + 1) * 100);
                    JButton button = new JButton(label);
                    button.setPreferredSize(new Dimension(150, 100));
                    int finalJ = j;
                    button.addActionListener(e -> buttonClicked(finalJ, q));
                    buttons[i][j] = button;
                    panel.add(button);
                } else {
                    JButton emptyButton = new JButton("");
                    emptyButton.setPreferredSize(new Dimension(150, 100));
                    emptyButton.setEnabled(false); 
                    panel.add(emptyButton);
                }
            }
        }
    }


    private void buttonClicked(int category, Question question) {
        if (question.isAnswered()) {
            JOptionPane.showMessageDialog(this, "This question has already been answered!");
            return;
        }

        if (!question.isAnswerable()) {
            JOptionPane.showMessageDialog(this, "All players have attempted to answer this question incorrectly. It is no longer answerable.");
            return;
        }

        String answer = JOptionPane.showInputDialog(this, question.getQuestionText());

        if (answer != null && answer.equalsIgnoreCase(question.getAnswer())) {
            JOptionPane.showMessageDialog(this, "Correct! You earned $" + question.getScore());
            players[currentPlayerIndex].incrementScore(question.getScore());
            scoreboard.updateScores(players);
            question.setAnswered(true);
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect! You lose $" + question.getScore());

            players[currentPlayerIndex].incrementScore(-question.getScore());
            scoreboard.updateScores(players);

            boolean allPassed = true; 
            int nextPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
            for (int i = nextPlayerIndex; i != currentPlayerIndex; i = (i + 1) % numPlayers) {
                int response = JOptionPane.showConfirmDialog(this, "Player " + players[i].getName() + ", do you want to attempt to answer this question?", "Player Turn", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    allPassed = false;
                    String otherAnswer = JOptionPane.showInputDialog(this, question.getQuestionText());
                    if (otherAnswer != null && otherAnswer.equalsIgnoreCase(question.getAnswer())) {
                        JOptionPane.showMessageDialog(this, "Correct! Player " + players[i].getName() + " earned $" + question.getScore());
                        players[i].incrementScore(question.getScore());
                        scoreboard.updateScores(players);
                        question.setAnswered(true);
                        currentPlayerIndex = i; 
                        return;
                    } else {
                        players[i].incrementScore(-question.getScore());
                        scoreboard.updateScores(players);
                        JOptionPane.showMessageDialog(this, "Incorrect! Player " + players[i].getName() + " loses $" + question.getScore());
                    }
                }
            }
            if (allPassed) {
                question.setAnswerable(false);
                JOptionPane.showMessageDialog(this, "All players passed. This question can no longer be answered.");
                currentPlayerIndex = nextPlayerIndex;
            }
        }
    }


    private void endGame() {
        int maxScore = Integer.MIN_VALUE;
        String winnerName = "";

        for (Player player : players) {
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
                winnerName = player.getName();
            }
        }

        JOptionPane.showMessageDialog(this, "Game Over! The winner is: " + winnerName + " with a score of $" + maxScore);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameBoard::new);
    }
}
