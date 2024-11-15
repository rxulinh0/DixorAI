package com.ai.dixorai;

public class TriviaQuestion {
    private String content;
    private String word;

    public TriviaQuestion(String content, String word) {
        this.content = content;
        this.word = word;
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
}
