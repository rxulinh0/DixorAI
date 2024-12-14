package com.ai.dixorai;

import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class QuestionList {
    private ArrayList<TriviaQuestion> questions = new ArrayList<>();
    private ArrayList<String> word_list = new ArrayList<>();
    private Executor executor;
    private int n_questions_per_word;
    private Context context;
    public QuestionList(ArrayList<String> word_list, int n_questions_per_word, Executor executor,Context context){
        this.n_questions_per_word = n_questions_per_word;
        this.word_list = word_list;
        this.executor = executor;
        this.context = context;
    }
//    public QuestionList(ArrayList<String> word_list, ArrayList<TriviaQuestion> questions,
//                        int n_questions_per_word, Executor executor, Context context){
//        this.word_list = word_list;
//        this.questions = questions;
//        this.n_questions_per_word = n_questions_per_word;
//        this.executor = executor;
//        this.context = context;
//    }
    public ArrayList<TriviaQuestion> getQuestions() {
        return questions;
    }

    public void addQuestion(TriviaQuestion question){
        questions.add(0,question);
    }

    public void setQuestions(ArrayList<TriviaQuestion> questions) {
        this.questions = questions;
    }

    public QuestionList(ArrayList<TriviaQuestion> questions) {
        this.questions = questions;
    }

    public int getSize(){
        return questions.size();
    }

    public void aiGenQuestionList(){
        for(String word_it : word_list){
            AIGenQuestion generateQuestions = new AIGenQuestion(executor, context, word_it);
            if(n_questions_per_word > 1){
                ArrayList<TriviaQuestion> new_questions = generateQuestions.gen_word_question_list(n_questions_per_word);
                for(TriviaQuestion question_it : new_questions){
                    questions.add(question_it);
                }
            } else{
                TriviaQuestion new_question = generateQuestions.gen_question();
                questions.add(new_question);
            }
        }
    }
}