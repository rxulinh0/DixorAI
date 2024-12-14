package com.ai.dixorai;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.transition.platform.MaterialElevationScale;

import java.util.ArrayList;

public class ModalBottomSheetTrivia extends BottomSheetDialogFragment {
    private int contentResourceId;
    private Context packageContext;
    private ArrayList<Word> mergedWordList = new ArrayList<>();
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
        RelativeLayout expandable_rl = v.findViewById(R.id.trivia_all_words_modal_bsc_expanded_rl);
        RelativeLayout select_words_rl = v.findViewById(R.id.trivia_all_words_modal_bsc_select_words_rl);
        RelativeLayout number_of_question_per_word_rl = v.findViewById(R.id.trivia_all_words_modal_bsc_num_questions_per_word_rl);
        MaterialButton startTrivia = v.findViewById(R.id.trivia_all_words_modal_bsc_start_trivia_button);
        MaterialSwitch standardMode = v.findViewById(R.id.trivia_all_words_modal_bsc_standard_mode_switch);
        MaterialSwitch select_words_switch = v.findViewById(R.id.trivia_all_words_modal_bsc_select_words_switch);
        Slider number_of_words_slider = v.findViewById(R.id.trivia_all_words_modal_bsc_num_words_slider);
        Slider number_of_questions_per_word_slider = v.findViewById(R.id.trivia_all_words_modal_bsc_num_questions_per_word_slider);
        Slider time_duration = v.findViewById(R.id.trivia_all_words_modal_bsc_time_slider);
        selectWordRvAdapter selectWordRvAdapter = new selectWordRvAdapter(packageContext,basic_user_data.getAllWords());
        TriviaUtil triviaUtil = new TriviaUtil();
        //BottomSheetBehavior modalBottomSheetBehavior = ((BottomSheetDialog)getDialog()).getBehavior();
        //modalBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        RecyclerView wordStaggeredRV = v.findViewById(R.id.trivia_all_words_bsc_select_words_staggeredRV);
        selectWordRvAdapter wordStaggeredRvAdapter = new selectWordRvAdapter(packageContext, basic_user_data.getAllWords());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL);
        wordStaggeredRV.setLayoutManager(staggeredGridLayoutManager);
        wordStaggeredRV.setAdapter(wordStaggeredRvAdapter);
        adjustUI_NWORDS(v);
        startTrivia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(standardMode.isChecked()){
                    ArrayList<Word> trivia_choosen_words = triviaUtil.chooseRandomWords(basic_user_data.getAllWords(),5);
                    sendAllWordsTriviaIntent(getAllWordsIds(trivia_choosen_words),trivia_choosen_words.size(),1,60000);
                } else{
                    if(!select_words_switch.isChecked()){
                        int N_WORDS;
                        if(basic_user_data.getAllWords().size()==3){
                            N_WORDS = 3;
                        } else if(basic_user_data.getAllWords().size() < 10){
                            N_WORDS = 5;
                        } else{
                            N_WORDS = (int)number_of_words_slider.getValue();
                        }
                        int N_QUESTIONS_PER_WORD = (int)number_of_questions_per_word_slider.getValue();
                        int TIME_IN_MILLIS = (int) (time_duration.getValue()*1000);
                        ArrayList<Word> trivia_choose_words = triviaUtil.chooseRandomWords(basic_user_data.getAllWords(),N_WORDS);
                        sendAllWordsTriviaIntent(getAllWordsIds(trivia_choose_words),N_WORDS,N_QUESTIONS_PER_WORD,TIME_IN_MILLIS);
                    } else{
                        int N_WORDS = (int)number_of_words_slider.getValue();
                        int N_QUESTIONS_PER_WORD = (int)number_of_questions_per_word_slider.getValue();
                        int TIME_IN_MILLIS = (int) (time_duration.getValue()*1000);
                        int[] IDS = selectWordRvAdapter.getSelectedIds();
                        sendAllWordsTriviaIntent(IDS,N_WORDS,N_QUESTIONS_PER_WORD,TIME_IN_MILLIS);
                    }
                }
            }
        });
        standardMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TransitionManager.beginDelayedTransition(expandable_rl, new MaterialElevationScale(true));
                expandable_rl.setVisibility(expandable_rl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        select_words_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TransitionManager.beginDelayedTransition(select_words_rl,new MaterialElevationScale(true));
                select_words_rl.setVisibility(b ? View.VISIBLE : View.GONE);
                number_of_question_per_word_rl.setVisibility(b ? View.GONE : View.VISIBLE);
            }
        });

    }

    public void adjustUI_NWORDS(View v){
        int size;
        if(contentResourceId==R.layout.trivia_all_words_modal_bottom_sheet_content){
            size = basic_user_data.getAllWords().size();

            Slider nwordsslider =v.findViewById(R.id.trivia_all_words_modal_bsc_num_words_slider);
            if(size==3 || size < 10){
//                nwordsslider.setValueFrom(3); Not possible
//                nwordsslider.setValueTo(3);
                nwordsslider.setVisibility(View.GONE);
            } else{
                int max = 0;
                for(int i = 5 ; i <= 30 ; i+=5){
                    if(size >= i){
                        max = i;
                    } else{
                        break;
                    }
                }
                nwordsslider.setValueFrom(5);
                nwordsslider.setValueTo((float)max);
            }
        } else if(contentResourceId==R.layout.trivia_tags_modal_bottom_sheet_content){
            size = basic_user_data.getAllWords().size();

            Slider nwordsslider =v.findViewById(R.id.trivia_all_words_modal_bsc_num_words_slider);
            if(size==3 || size < 10){
//                nwordsslider.setValueFrom(3); Not possible
//                nwordsslider.setValueTo(3);
                nwordsslider.setVisibility(View.GONE);
            } else{
                int max = 0;
                for(int i = 5 ; i <= 30 ; i+=5){
                    if(size >= i){
                        max = i;
                    } else{
                        break;
                    }
                }
                nwordsslider.setValueFrom(5);
                nwordsslider.setValueTo((float)max);
            }
        } else if(contentResourceId==R.layout.trivia_books_modal_bottom_sheet_content){

        }
    }

    public void sendAllWordsTriviaIntent(int[] WORD_ID_ARRAY, int N_WORDS, int N_QUESTIONS_PER_WORD, int timeInMillis) {
        Intent new_trivia_intent = new Intent(packageContext, Trivia.class);
        new_trivia_intent.putExtra("WORD_ID_ARRAY",getAllWordsIds());
        new_trivia_intent.putExtra("N_WORDS",N_WORDS);
        new_trivia_intent.putExtra("N_QUESTIONS_PER_WORD",N_QUESTIONS_PER_WORD);
        new_trivia_intent.putExtra("WORD_ORIGIN_MODE","ALL_WORDS");
        new_trivia_intent.putExtra("TIME_IN_MILLIS",timeInMillis);
        startActivity(new_trivia_intent);
    }

    public int[] getAllWordsIds(){
        int[] ids = new int[basic_user_data.getAllWords().size()];
        for(int i = 0 ; i < basic_user_data.getAllWords().size() ; ++i){
            ids[i] = i;
        }
        return ids;
    }

    public int[] getAllWordsIds(ArrayList<Word> wordList){
        ArrayList<Word> allWords = basic_user_data.getAllWords();
        int[] ids = new int[wordList.size()];
        int i = 0;
        int c = 0;
        for(Word word_it : wordList){
            if(word_it.getWord().equals(allWords.get(i).getWord())){
               ids[c] = i;
               ++c;
            }
            ++i;
        }
        return ids;
    }

    public void bindBooksModalBottomSheet(View v){

    }

    public void bindTagsModalBottomSheet(View v){

    }

    public static final String TAG = "ModalBottomSheet";
}