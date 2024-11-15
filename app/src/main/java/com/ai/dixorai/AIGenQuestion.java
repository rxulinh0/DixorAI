package com.ai.dixorai;


import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class AIGenQuestion extends GeneratePrompt {
    private String word;
    private Context context;

    public AIGenQuestion(Executor executor, Context context, String word) {
        super("", executor);
        this.context = context;
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
    public TriviaQuestion gen_question(){
        return new TriviaQuestion(genPrompt(context.getString(R.string.trivia_prompt_make_random_question_a) + word), word);
    }
    public ArrayList<TriviaQuestion> gen_word_question_list(int n_questions){
        ArrayList<TriviaQuestion> result = new ArrayList<>(n_questions);
        String prompt = "";
        StringBuilder prompt_q_list = new StringBuilder("(");
        for(int i = 0 ; i < n_questions ; ++i){
            if(i==0){
                prompt = context.getString(R.string.trivia_prompt_make_random_question_a) + word;
                try{
                    result.add(0,new TriviaQuestion(genPrompt(prompt),word));
                    prompt_q_list.append(result.get(0)).append(", ");
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else{
                prompt = context.getString(R.string.trivia_prompt_make_random_question_list) + prompt_q_list;
                try{
                    result.add(0,new TriviaQuestion(genPrompt(prompt),word));
                } catch(Exception e){
                    e.printStackTrace();
                }
                if((n_questions-1)-i==1 && i!=n_questions-1){
                    prompt_q_list.append(result.get(0)).append(")");
                } else{
                    prompt_q_list.append(result.get(0)).append(", ");
                }
            }
        }
        return result;
    }
}
