package com.ai.dixorai;

import java.util.ArrayList;
import java.util.Collections;

public class TriviaUtil {
    public TriviaUtil() {
    }
    public ArrayList<Word> chooseRandomWords(ArrayList<Word> wordList,int n_words_to_choose){
        // Check if n is greater than the size of the original list
        if (n_words_to_choose > wordList.size()) {
            throw new IllegalArgumentException("n words to choose cannot be greater than the size of the original list");
        }

        // Create a new ArrayList to store the random elements
        ArrayList<Word> randomElements = new ArrayList<>();

        // Shuffle the original list
        Collections.shuffle(wordList);

        // Select the first n elements from the shuffled list
        for (int i = 0; i < n_words_to_choose; i++) {
            randomElements.add(wordList.get(i));
        }

        return randomElements;
    }

    public ArrayList<Word> shuffleWords(ArrayList<Word> wordList){
        Collections.shuffle(wordList);
        return wordList;
    }

    public ArrayList<Word> getWordsbyIdList(ArrayList<Word> wordList,int[] selected_ids){
        ArrayList<Word> result = new ArrayList<>();
        for(int i = 0 ; i < selected_ids.length ; ++i){
            result.add(wordList.get(selected_ids[i]));
        }
        Collections.shuffle(result);
        return result;
    }
}
