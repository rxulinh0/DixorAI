package com.ai.dixorai;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordTagViewModel extends ViewModel {
    private MutableLiveData<List<Word>> allWords;
    private MutableLiveData<List<Tag>> existing_tags;
    private MutableLiveData<List<BookTag>> existing_books;

    public WordTagViewModel() {
        allWords = new MutableLiveData<>(new ArrayList<>(basic_user_data.getAllWords()));
        existing_tags = new MutableLiveData<>(new ArrayList<>(basic_user_data.getAllExistingTags()));
        existing_books = new MutableLiveData<>(new ArrayList<>(basic_user_data.getAllExistingBookTags()));
    }

    public LiveData<List<Word>> getAllWords() {
        allWords = new MutableLiveData<>(new ArrayList<>(basic_user_data.getAllWords()));
        return allWords;
    }

    public LiveData<List<Tag>> getExisting_tags() {
        existing_tags = new MutableLiveData<>(new ArrayList<>(basic_user_data.getAllExistingTags()));
        return existing_tags;
    }

    public LiveData<List<BookTag>> getExisting_books() {
        existing_books = new MutableLiveData<>(new ArrayList<>(basic_user_data.getAllExistingBookTags()));
        return existing_books;
    }

    public void addWord(Word word){
        List<Word> currentList = allWords.getValue();
        if(currentList != null){
            currentList.add(0,word);
        }
    }

    public void addExistingTag(Tag tag){
        List<Tag> currentList = existing_tags.getValue();
        if(currentList != null){
            currentList.add(0, tag);
        }
    }

    public void addExistingBook(BookTag booktag){
        List<BookTag> currentList = existing_books.getValue();
        if(currentList != null){
            currentList.add(0, booktag);
        }
    }
}