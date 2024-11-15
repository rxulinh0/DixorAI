package com.ai.dixorai;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class WordSharedViewModel extends ViewModel {
    private final MutableLiveData<List<Word>> itemList = new MutableLiveData<>();

    public LiveData<List<Word>> getItemList() {
        return itemList;
    }

    public void setItemList(List<Word> items) {
        itemList.setValue(items);
    }
}