package com.ai.dixorai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.FullScreenCarouselStrategy;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class Trivia extends AppCompatActivity {
    private RecyclerView carouselRV;
    private TriviaRV triviaCarouselAdapter;
    private LottieAnimationView loadingAnimationView;
    private QuestionList generatedQuestionList;
    private int[] ids;
    private int n_words,n_questions_per_word;
    private String WORD_ORIGIN_MODE,BOOK_TAG_NAME,TAG_NAME;
    private ArrayList<String> words;
    private QuestionList questionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trivia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getIntentExtras();
        getWords();
        checkingNetwork();
        createQuestions();
        stopLoadingAnimation();
        bind();
    }
    private void getIntentExtras(){
        Intent intent = getIntent();
        ids = intent.getIntArrayExtra("WORD_ID_ARRAY");
        n_words = intent.getIntExtra("N_WORDS",-1);
        n_questions_per_word = intent.getIntExtra("N_QUESTIONS_PER_WORD",-1);
        if(n_words==-1 || n_questions_per_word==-1){
            Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong_try_again),Toast.LENGTH_LONG).show();
        }
        WORD_ORIGIN_MODE = intent.getStringExtra("WORD_ORIGIN_MODE");
    }
    private void getWords(){
        //TODO: para cada modo
        switch(WORD_ORIGIN_MODE){
            case "ALL_WORDS":
                for(int i = 0 ; i < n_words ; ++i){
                    words.add(basic_user_data.getAllWords().get(i).getWord());
                }
                break;
            case "BY_TAG":
                Tag reference_tag = new Tag(TAG_NAME);
                for(int i = 0 ; i < n_words ; ++i){
                    words.add(basic_user_data.getAllWords(reference_tag).get(i).getWord());
                }
                break;
            case "BY_BOOK_TAG":
                BookTag reference_booktag = new BookTag(BOOK_TAG_NAME);
                for(int i = 0 ; i < n_words ; ++i){
                    words.add(basic_user_data.getAllWords(reference_booktag).get(i).getWord());
                }
                break;
            default:
                Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong_try_again),Toast.LENGTH_LONG).show();
                break;
        }

    }
    protected void checkingNetwork(){
        if (!NetworkUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    protected void bind(){
        carouselRV = findViewById(R.id.trivia_question_fullscreen_rv);
        triviaCarouselAdapter = new TriviaRV(getApplicationContext(),questionList);
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(new FullScreenCarouselStrategy());
        carouselRV.setLayoutManager(carouselLayoutManager);
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(carouselRV);
        carouselRV.setAdapter(triviaCarouselAdapter);
    }
    private void createQuestions(){
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Executor executor = getApplicationContext().getMainExecutor();
                questionList = new QuestionList(words,n_questions_per_word,executor); //Generates everything inside the class
            } else{
                Toast.makeText(getApplicationContext(),getString(R.string.device_not_supported_old_android_version), Toast.LENGTH_LONG).show();
                finish();
            }
        } catch(Exception e){
            Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            finish();
            e.printStackTrace();
        }
    }
    private void stopLoadingAnimation(){
        loadingAnimationView = findViewById(R.id.trivia_loadingAnimationView);
        loadingAnimationView.pauseAnimation();
        loadingAnimationView.setVisibility(View.GONE);
    }
}