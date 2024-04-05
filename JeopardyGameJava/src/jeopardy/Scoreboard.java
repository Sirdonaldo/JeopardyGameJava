package jeopardy;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Scoreboard {

    private JFrame frame;
    private JPanel panel;
    private ArrayList<PlayerPanel> playerPanels;

    public Scoreboard(int numPlayers) {
        frame = new JFrame("Jeopardy Scoreboard");
        panel = new JPanel();
        playerPanels = new ArrayList<>();
        
        initializeComponents(numPlayers);
    }

    private void initializeComponents(int numPlayers) {
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setLayout(new GridLayout(numPlayers, 1));

        for (int i = 0; i < numPlayers; i++) {
            PlayerPanel playerPanel = new PlayerPanel("Player " + (i + 1), 0);
            playerPanels.add(playerPanel);
            panel.add(playerPanel);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    public void updateScores(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            playerPanels.get(i).updateScore(players[i].getName(), players[i].getScore());
        }
    }

    private class PlayerPanel extends JPanel {
        private JLabel nameLabel;
        private JLabel scoreLabel;

        public PlayerPanel(String name, int score) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            nameLabel = new JLabel(name);
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            add(nameLabel, BorderLayout.CENTER);

            scoreLabel = new JLabel("Score: " + score);
            scoreLabel.setHorizontalAlignment(JLabel.CENTER);
            add(scoreLabel, BorderLayout.SOUTH);
        }

        public void updateScore(String name, int score) {
            nameLabel.setText(name);
            scoreLabel.setText("Score: " + score);
        }
    }
}
