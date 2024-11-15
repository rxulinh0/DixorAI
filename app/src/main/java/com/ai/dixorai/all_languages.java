package com.ai.dixorai;

import java.util.ArrayList;
import java.util.Arrays;

public class all_languages {
    // Languages by Default
    public static ArrayList<lang_basic_data> all_languages = new ArrayList<>(Arrays.asList(new lang_basic_data("English","en","Define in one paragraph the word (with a word limit of "),new lang_basic_data("Español","es","Define en un párrafo la palabra(con un limite de palabras de ")));
    public all_languages(int[] resourcesIds) {
        int i = 0;
        for(lang_basic_data lang_iterator : all_languages){
            all_languages.get(i).setFlagResourceId(resourcesIds[i]);
            ++i;
        }
    }

    public static ArrayList<lang_basic_data> getAll_languages() {
        return all_languages;
    }
}
