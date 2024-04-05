package jeopardy;

public class Question {
	
    private String category;
    private String questionText;
    private String answer;
    private int score;
    private boolean answered;
    private boolean answerable;
    private int playersAttemptedIncorrectly;
    private int numPlayers;

    public Question(String category, String questionText, String answer, int score, int numPlayers) {
    	
        this.category = category;
        this.questionText = questionText;
        this.answer = answer;
        this.score = score;
        this.answered = false;
        this.answerable = true;
        this.playersAttemptedIncorrectly = 0;
        this.numPlayers = numPlayers;
    }

    public String getCategory() {
        return category;
    }
    public String getQuestionText() {
        return questionText;
    }
    public String getAnswer() {
        return answer;
    }
    public int getScore() {
        return score;
    }
    public boolean isAnswered() {
        return answered;
    }
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
    public boolean isAnswerable() {
        return answerable;
    }
    public void setAnswerable(boolean answerable) {
        this.answerable = answerable;
    }
    public void registerIncorrectAttempt() {
        this.playersAttemptedIncorrectly++;
        if (this.playersAttemptedIncorrectly >= numPlayers) {
            this.answerable = false;
        }
    }
}
