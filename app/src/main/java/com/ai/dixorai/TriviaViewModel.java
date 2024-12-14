package com.ai.dixorai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TriviaViewModel extends ViewModel{
    private final MutableLiveData<ArrayList<TriviaQuestion>> itemList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<ArrayList<TriviaQuestion>> getItemList() {
        return itemList;
    }

    public void addItem(TriviaQuestion item) {
        ArrayList<TriviaQuestion> currentList = itemList.getValue();
        if (currentList != null) {
            currentList.add(0, item);
            itemList.setValue(currentList);
        }
    }
}
