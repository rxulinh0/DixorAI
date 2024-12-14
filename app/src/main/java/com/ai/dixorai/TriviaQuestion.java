package com.ai.dixorai;

public class TriviaQuestion {
    private String content;
    private String word;
    private boolean answered = false;

    public TriviaQuestion(String content, String word) {
        this.content = content;
        this.word = word;
        this.answered = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
