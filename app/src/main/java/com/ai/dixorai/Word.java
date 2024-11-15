package com.ai.dixorai;

import java.util.ArrayList;

public class Word {
    public String word;
    public ArrayList<Tag> Tags = new ArrayList<>();
    public ArrayList<BookTag> BookTags = new ArrayList<>();
    public String date;
    public String ai_generated_def;
    public int n_defs;
    public String color_id;
    public boolean expanded_once = false;

    public Word(String word) {
        this.word = word;
    }

    public Word(String word, String date) {
        this.word = word;
        this.date = date;
        this.n_defs = 0;
        this.color_id = basic_user_data.getWord_color_ids().get(0); // Gets White Color
    }

    public Word(String word, String date, String ai_generated_def) {
        this.word = word;
        this.date = date;
        this.n_defs = 0;
        this.ai_generated_def = ai_generated_def;
        this.color_id = basic_user_data.getWord_color_ids().get(0); // Gets White Color
    }

    public Word(String word, ArrayList<Tag> tags) {
        this.word = word;
        this.Tags = tags;
        this.n_defs = 0;
        this.color_id = basic_user_data.getWord_color_ids().get(0); // Gets White Color
    }

    public Word(String word, ArrayList<Tag> tags, ArrayList<BookTag> bookTags) {
        this.word = word;
        Tags = tags;
        BookTags = bookTags;
    }

    public Word(String word, String date, BookTag tag){
        this.word = word;
        this.date = date;
        this.BookTags.add(tag);
        this.color_id = basic_user_data.getWord_color_ids().get(0);
    }

    public ArrayList<BookTag> getBookTags() {
        return BookTags;
    }

    public void setBookTags(ArrayList<BookTag> bookTags) {
        BookTags = bookTags;
    }

    public void setAi_generated_def(String ai_generated_def) {
        this.ai_generated_def = ai_generated_def;
        ++this.n_defs;
    }

    public void setExpanded_once(boolean expanded_once) {
        this.expanded_once = expanded_once;
    }

    public boolean isExpanded_once() {
        return expanded_once;
    }

    public String getColor_id() {
        return color_id;
    }

    public void setColor_id(String color_id) {
        this.color_id = color_id;
    }

    public String getAi_generated_def() {
        return ai_generated_def;
    }

    public String getDate() {
        return date;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<Tag> getTags() {
        return Tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        Tags = tags;
    }
}
