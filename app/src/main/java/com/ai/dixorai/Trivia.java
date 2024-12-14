package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class Trivia extends AppCompatActivity {
    private RecyclerView carouselRV;
    private TriviaRV triviaCarouselAdapter;
    public LottieAnimationView loadingAnimationView;
    public TextView loadingMessageTV,timerTextView,answeredQuestionsTV;
    private QuestionList generatedQuestionList;
    private int[] ids;
    private int n_words,n_questions_per_word,N_TOTAL_QUESTIONS;
    private String WORD_ORIGIN_MODE,BOOK_TAG_NAME,TAG_NAME;
    private ArrayList<String> words = new ArrayList<>();
    private QuestionList questionList;
    private Context context;
    //private MutableLiveData<TriviaQuestion> triviaQuestionsMutableLiveData;
    private TriviaViewModel triviaViewModel;
    private CountDownTimer countDownTimer;
    private MaterialCardView notchCard;
    private int answeredQuestions = 0;
    private long timeInMillis = 60000; // Default value for time

    private static String removeFirstLine(String text) {
        if(!(text.charAt(0)=='#')){
            return text;
        }
        // Find the index of the first newline character
        int indexOfFirstNewline = text.indexOf('\n');

        // If there's no newline, return an empty string
        if (indexOfFirstNewline == -1) {
            return "";
        }

        // Return the substring from the character after the first newline to the end
        return text.substring(indexOfFirstNewline + 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.activity_trivia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        decorView.setOnApplyWindowInsetsListener((v, insets) -> {
            int bottomInset = insets.getSystemWindowInsetBottom();
            v.setPadding(0, 0, 0, bottomInset); // Adjust padding as needed
            return insets.consumeSystemWindowInsets();
        });
        loadingMessageTV = findViewById(R.id.trivia_loadingMessageTV);
        getIntentExtras();
        getWords();
        checkingNetwork();
        //createQuestions();
        createQuestionList();
        bind();
    }
    private void allocateQuestionList(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Executor executor = getMainExecutor();
            questionList = new QuestionList(words,n_questions_per_word,executor,context);
        } else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.device_not_supported_old_android_version), Toast.LENGTH_LONG);
            finish();
        }
    }

    private void getIntentExtras(){
        Intent intent = getIntent();
        ids = intent.getIntArrayExtra("WORD_ID_ARRAY");
        n_words = intent.getIntExtra("N_WORDS",-1);
        n_questions_per_word = intent.getIntExtra("N_QUESTIONS_PER_WORD",-1);
        N_TOTAL_QUESTIONS = n_words * n_questions_per_word;
        timeInMillis  = intent.getIntExtra("TIME_IN_MILLIS",-1);
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
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(new FullScreenCarouselStrategy(), RecyclerView.VERTICAL);
        carouselRV.setLayoutManager(carouselLayoutManager);
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(carouselRV);
        carouselRV.setAdapter(triviaCarouselAdapter);
        notchCard = findViewById(R.id.trivia_notchCard);
        timerTextView = findViewById(R.id.trivia_timerTV);
        answeredQuestionsTV = findViewById(R.id.trivia_completedQuestionsTV);
    }
//    private void createQuestions(){
//        try{
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                Executor executor = getMainExecutor();
//                questionList = new QuestionList(words,n_questions_per_word,executor,getApplicationContext()); //Generates everything inside the class
//                questionList.aiGenQuestionList();
//                //monitorArrayList();
//                int N_QUESTIONS = n_words * n_questions_per_word;
//                basic_user_data.getAux_counter_trivia_questions_generated().observe(this, new Observer<Integer>() {
//                    @Override
//                    public void onChanged(Integer integer) {
//                        Toast.makeText(getApplicationContext(), integer.toString(), Toast.LENGTH_LONG).show();
//                        if (basic_user_data.getAux_counter_trivia_questions_generated().getValue()==N_QUESTIONS) {
//                            System.out.println("Value reached: " + basic_user_data.getAux_counter_trivia_questions_generated());
//                            stopLoadingAnimation();
//                            basic_user_data.resetAux_counter_trivia_questions_generated();
//                        }
//                    }
//                });
//                for(String word : words){
//                    Toast.makeText(getApplicationContext(),word,Toast.LENGTH_LONG).show();
//                }
//            } else{
//                Toast.makeText(getApplicationContext(),getString(R.string.device_not_supported_old_android_version), Toast.LENGTH_LONG).show();
//                finish();
//            }
//        } catch(Exception e){
//            Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//            finish();
//            e.printStackTrace();
//        }
//    }

//    private void createNewQuestionList(){
//        try{
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P){
//
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    private void createQuestionList(){
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                questionList = new QuestionList(words,n_questions_per_word,getMainExecutor(),getApplicationContext());
                triviaViewModel = new ViewModelProvider(this).get(TriviaViewModel.class);
                triviaViewModel.getItemList().observe(this, new Observer<ArrayList<TriviaQuestion>>(){
                    @Override
                    public void onChanged(ArrayList<TriviaQuestion> list){
                        if(list.size() > 0){
                            questionList.addQuestion(list.get(0));
                        }
                        int progress = (list.size()*100)/N_TOTAL_QUESTIONS;
                        if(progress >= 50){
                            loadingMessageTV.setText(R.string.half_there);
                        } else if(progress >= 75){
                            loadingMessageTV.setText(R.string.wont_be_long);
                        } else if(progress >= 90){
                            loadingMessageTV.setText(R.string.almost_done);
                        }
                        if(list.size() == N_TOTAL_QUESTIONS){
                            stopLoadingAnimation();
                        }
                    }
                });
                Executor executor = getMainExecutor();
                for(String word_it : words){
                    //AIGenQuestion generateQuestions = new AIGenQuestion(executor, context, word_it);
                    if(n_questions_per_word > 1){
                        //ArrayList<TriviaQuestion> new_questions = generateQuestions.gen_word_question_list(n_questions_per_word);
                        String prompt = "";
                        final String[] prompt_q_list = {"("};
                        for(int i = 0 ; i < n_questions_per_word ; ++i){
                            if(i==0){
                                prompt = getApplicationContext().getResources().getString(R.string.trivia_prompt_make_random_question_a) + word_it;
                                try{
                                    //result.add(0,new TriviaQuestion(genQuestionPrompt(prompt),word));
                                    final String[] ai_generated_response = new String[1];
                                    String API_KEY = BuildConfig.API_KEY;
                                    GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
                                            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                                            /* apiKey */ API_KEY);
                                    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
                                    Content content = new Content.Builder()
                                            .addText(prompt)
                                            .build();

                                    ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                                    Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                                        @Override
                                        public void onSuccess(GenerateContentResponse result) {
                                            ai_generated_response[0] = removeFirstLine(result.getText());
                                            System.out.println(ai_generated_response[0]);
                                            //basic_user_data.addCounterTriviaQuestionsGenerated();
                                            prompt_q_list[0] +=(result.getText() + ", "); //result.getText gives trivia question string here
                                            triviaViewModel.addItem(new TriviaQuestion(result.getText(), word_it));
                                            //prompt_q_list.append(result.get(0)).append(", ");
                                            //viewModel.addWord(basic_user_data.getAllWords().get(0));
                                            //startTypewriterEffect(word_def_tv,resultText);
                                            //wordRvAdapter.notifyItemChanged(position);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }, executor);

                                } catch(Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG);
                                    finish();
                                }
                            } else{
                                prompt = getString(R.string.trivia_prompt_make_random_question_list) + word_it + prompt_q_list[0];
                                try{
                                    //result.add(0,new TriviaQuestion(genQuestionPrompt(prompt),word));
                                    final String[] ai_generated_response = new String[1];
                                    String API_KEY = BuildConfig.API_KEY;
                                    GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
                                            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                                            /* apiKey */ API_KEY);
                                    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
                                    Content content = new Content.Builder()
                                            .addText(prompt)
                                            .build();

                                    ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                                    int finalI = i;
                                    Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                                        @Override
                                        public void onSuccess(GenerateContentResponse result) {
                                            ai_generated_response[0] = removeFirstLine(result.getText());
                                            System.out.println(ai_generated_response[0]);
                                            basic_user_data.addCounterTriviaQuestionsGenerated();
                                            if((n_questions_per_word-1)- finalI ==1 && finalI !=n_questions_per_word-1){
                                                //prompt_q_list.append(result.get(0)).append(")");
                                                prompt_q_list[0] +=(result.getText()+")");
                                            } else{
                                                prompt_q_list[0] += (result.getText() + ", ");
                                                //prompt_q_list[0].append(result.get(0)).append(", ");
                                            }
                                            prompt_q_list[0] +=(result.getText() + ", "); //result.getText gives trivia question string here
                                            triviaViewModel.addItem(new TriviaQuestion(result.getText(), word_it));
                                            //prompt_q_list.append(result.get(0)).append(", ");
                                            //viewModel.addWord(basic_user_data.getAllWords().get(0));
                                            //startTypewriterEffect(word_def_tv,resultText);
                                            //wordRvAdapter.notifyItemChanged(position);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }, executor);

                                } catch(Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG);
                                    finish();
                                }
                            }
                        }
                    } else{
//                        TriviaQuestion new_question = generateQuestions.gen_question();
//                        questions.add(new_question);
                        String prompt = getString(R.string.trivia_prompt_make_random_question_list) + word_it;
                        try{
                            //result.add(0,new TriviaQuestion(genQuestionPrompt(prompt),word));
                            final String[] ai_generated_response = new String[1];
                            String API_KEY = BuildConfig.API_KEY;
                            GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
                                    // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                                    /* apiKey */ API_KEY);
                            GenerativeModelFutures model = GenerativeModelFutures.from(gm);
                            Content content = new Content.Builder()
                                    .addText(prompt)
                                    .build();

                            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                                @Override
                                public void onSuccess(GenerateContentResponse result) {
                                    ai_generated_response[0] = removeFirstLine(result.getText());
                                    System.out.println(ai_generated_response[0]);
                                    //basic_user_data.addCounterTriviaQuestionsGenerated();
                                    triviaViewModel.addItem(new TriviaQuestion(result.getText(), word_it));
                                    //prompt_q_list.append(result.get(0)).append(", ");
                                    //viewModel.addWord(basic_user_data.getAllWords().get(0));
                                    //startTypewriterEffect(word_def_tv,resultText);
                                    //wordRvAdapter.notifyItemChanged(position);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    t.printStackTrace();
                                }
                            }, executor);
                    } catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG);
                            finish();
                        }
                }
            }
        }
            else{
                Toast.makeText(getApplicationContext(),getString(R.string.device_not_supported_old_android_version),Toast.LENGTH_LONG).show();
                finish();
            }
    } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG);
            finish();
        }
    }
    private void stopLoadingAnimation(){
        loadingAnimationView = findViewById(R.id.trivia_loadingAnimationView);
        loadingAnimationView.pauseAnimation();
        loadingAnimationView.setVisibility(View.GONE);
        carouselRV.setVisibility(View.VISIBLE);
        triviaCarouselAdapter.setQuestionList(questionList);
        triviaCarouselAdapter.notifyDataSetChanged();
        startStatusNotchCard();
    }
    private void startStatusNotchCard(){
        notchCard.setVisibility(View.VISIBLE);
        startTimer();
    }
    private void startTimer(){
        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }
    public void updateNumAnswers(){
        ++answeredQuestions;
        @SuppressLint("DefaultLocale") String numanswers = String.format("%02d / %02d", answeredQuestions, N_TOTAL_QUESTIONS);
        answeredQuestionsTV.setText(numanswers);
    }
    private void updateTimer() {
        int seconds = (int) (timeInMillis / 1000) % 60;
        int minutes = (int) ((timeInMillis / (1000 * 60)) % 60);
        String timeLeft;
        if(minutes > 0){
            timeLeft= String.format("%02d:%02d", minutes, seconds);
        } else{
            timeLeft = String.format("%02ds",seconds);
        }
        timerTextView.setText(timeLeft);
    }
}