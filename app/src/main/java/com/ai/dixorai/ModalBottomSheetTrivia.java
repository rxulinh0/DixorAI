package com.ai.dixorai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

public class ModalBottomSheetTrivia extends DialogFragment {
    private int contentResourceId;
    private Context packageContext;
    public ModalBottomSheetTrivia(int contentResourceId,Context packageContext) {
        this.contentResourceId = contentResourceId;
        this.packageContext = packageContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(contentResourceId, container, false);
        if(contentResourceId==R.layout.trivia_all_words_modal_bottom_sheet_content){
            bindAllWordsModalBottomSheet(view);
        } else if(contentResourceId==R.layout.trivia_books_modal_bottom_sheet_content){
            bindBooksModalBottomSheet(view);
        } else if(contentResourceId==R.layout.trivia_tags_modal_bottom_sheet_content){
            bindTagsModalBottomSheet(view);
        }
        return view;
    }

    public void bindAllWordsModalBottomSheet(View v){
        MaterialButton startTrivia = v.findViewById(R.id.trivia_all_words_modal_bsc_start_trivia_button);
        MaterialSwitch standardMode = v.findViewById(R.id.trivia_all_words_modal_bsc_standard_mode_switch);
        Slider number_of_words_slider = v.findViewById(R.id.trivia_all_words_modal_bsc_num_words_slider);
        Slider number_of_questions_per_word = v.findViewById(R.id.trivia_all_words_modal_bsc_num_questions_per_word_slider);

        final boolean[] isInStandardMode = {false};
        startTrivia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInStandardMode[0]){
                    sendAllWordsTriviaIntent(getAllWordsIds(),basic_user_data.getAllWords().size(),1);
                } else{
                    int N_WORDS = (int).getValue();
                    int N_QUESTIONS_PER_WORD = (int)
                }
            }
        });
        standardMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isInStandardMode[0] = b;
            }
        });
    }

    public void sendAllWordsTriviaIntent(int[] WORD_ID_ARRAY, int N_WORDS, int N_QUESTIONS_PER_WORD) {
        Intent new_trivia_intent = new Intent(packageContext, Trivia.class);
        new_trivia_intent.putExtra("WORD_ID_ARRAY",getAllWordsIds());
        new_trivia_intent.putExtra("N_WORDS",N_WORDS);
        new_trivia_intent.putExtra("N_QUESTIONS_PER_WORD",N_QUESTIONS_PER_WORD);
        new_trivia_intent.putExtra("WORD_ORIGIN_MODE","ALL_WORDS");
    }

    public int[] getAllWordsIds(){
        int[] ids = new int[basic_user_data.getAllWords().size()];
        for(int i = 0 ; i < basic_user_data.getAllWords().size() ; ++i){
            ids[i] = i;
        }
        return ids;
    }

    public void bindBooksModalBottomSheet(View v){

    }

    public void bindTagsModalBottomSheet(View v){

    }

    public static final String TAG = "ModalBottomSheet";
}