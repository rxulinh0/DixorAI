package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class EditWordActivity extends AppCompatActivity {
    public MaterialCardView new_tag_cardview,new_tag_root_cardview,redefine_cardbutton,question_card,make_question_cardbutton,question_ai_answer_card,next_cardbutton;
    public TextView main_title,add_existing_tag_title,question_ai_answer_tv; // Word Name
    public TextInputEditText new_tag_input,edit_word_def,make_question_it;
    public Intent intent;
    public int RV_POSITION;
    public RelativeLayout expand_rl_add_tag_card,question_title_rl,alt_question_rl;
    public RecyclerView word_tag_rv,existing_tags_rv;
    public TagRvAdapter word_tag_rv_adapter,existing_tags_rv_adapter;
    public ImageView expand_iv_new_tag_card;
    public String lang_prompt_tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_word);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intent = getIntent();
        main_title = findViewById(R.id.edit_word_activity_title_tv);
        edit_word_def = findViewById(R.id.edit_def_inputedittext);
        expand_rl_add_tag_card = findViewById(R.id.add_a_new_tag_rl_b);
        word_tag_rv = findViewById(R.id.tag_rv_edit_word_activity);
        new_tag_input = findViewById(R.id.add_new_tag_card_input_text);
        new_tag_root_cardview = findViewById(R.id.new_tag_root_card);
        new_tag_cardview = findViewById(R.id.new_tag_card_view_edit_word_activity);
        redefine_cardbutton = findViewById(R.id.redefine_card_edit_word_activity);
        existing_tags_rv = findViewById(R.id.existing_tag_rv_edit_word_activity);
        add_existing_tag_title = findViewById(R.id.add_existing_tag_rv_title);
        expand_iv_new_tag_card = findViewById(R.id.expand_card_iv_add_new_tag_edit_activity);
        question_card = findViewById(R.id.question_edit_word_card);
        question_title_rl = findViewById(R.id.question_card_edit_word_title_rl);
        question_ai_answer_card = findViewById(R.id.question_ai_answer_card_edit_word);
        question_ai_answer_tv = findViewById(R.id.question_ai_answer_tv_edit_word);
        alt_question_rl = findViewById(R.id.question_edit_word_alt_rl);
        make_question_cardbutton = findViewById(R.id.make_question_card_edit_word);
        make_question_it = findViewById(R.id.write_question_input_text_edit_word);
        next_cardbutton = findViewById(R.id.next_edit_word_activity_card_button);
        RV_POSITION = intent.getIntExtra("RV_POSITION",-1); // Word position in HomeFragment RV & basic_user_data's ArrayList<>
        if(RV_POSITION!=-1){
            bindUI();
        } else{
            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            Log.d("DixorAI_Exception","Word RV_POSITION value not received successfully...");
            finish();
        }
    }
    public void bindUI(){
        Word word = basic_user_data.getAllWords().get(RV_POSITION);
        main_title.setText(basic_user_data.getAllWords().get(RV_POSITION).getWord());
        edit_word_def.setText(word.getAi_generated_def());
        word_tag_rv_adapter = new TagRvAdapter(getApplicationContext(), word.getTags(),false);
        word_tag_rv.setAdapter(word_tag_rv_adapter);
        ArrayList<Tag> existing_tags = basic_user_data.getAllExistingTags(RV_POSITION);
        existing_tags_rv_adapter = new TagRvAdapter(getApplicationContext(), existing_tags, false, position -> {
            word_tag_rv_adapter.addAndSaveTag(existing_tags_rv_adapter.getRv_tags().get(position),RV_POSITION,expand_rl_add_tag_card.getRootView());
            existing_tags_rv_adapter.removeRvTag(position);
        });
        int font_id = basic_user_data.getPreferred_font();
        Typeface regular_typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.montserratbold);
        Typeface bold_typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.montserratblack);
        switch(font_id){
            case 0:
                break;
            case 1:
                regular_typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.librebaskervilleregular);
                bold_typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.librebaskervillebold);
                break;
            case 2:
                regular_typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.imfellenglishregular);
                bold_typeface = regular_typeface;
                break;
            case 3:
                regular_typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.fraunces);
                bold_typeface = regular_typeface;
                break;
            default:
                showSnackbar(getApplicationContext().getResources().getString(R.string.font_not_found_error), new_tag_root_cardview.getRootView());
                break;
        }
        main_title.setTypeface(bold_typeface);
        edit_word_def.setTypeface(regular_typeface);
        new_tag_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!new_tag_input.getText().toString().equals("") && !searchTag(new_tag_input.getText().toString())){
                    createAndSaveNewTag(new_tag_input.getText().toString());
                    word_tag_rv_adapter.setRv_tags(basic_user_data.getAllWords().get(RV_POSITION).getTags());
                    word_tag_rv_adapter.notifyDataSetChanged();
                    existing_tags_rv_adapter.setRv_tags(basic_user_data.getAllExistingTags(RV_POSITION));
                    existing_tags_rv_adapter.notifyDataSetChanged();
                    if(existing_tags_rv_adapter.getItemCount()==0){
                        expand_iv_new_tag_card.setVisibility(View.GONE);
                        expand_rl_add_tag_card.setVisibility(View.GONE);
                    }
                    expand_iv_new_tag_card.setVisibility(basic_user_data.getAllExistingTags(RV_POSITION).isEmpty() ? View.GONE : View.VISIBLE);
                    new_tag_input.setText("");
                }
            }
        });
        expand_iv_new_tag_card.setVisibility(basic_user_data.getAllExistingTags(RV_POSITION).isEmpty() ? View.GONE : View.VISIBLE);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL);
        existing_tags_rv.setLayoutManager(staggeredGridLayoutManager);
        existing_tags_rv.setAdapter(existing_tags_rv_adapter);
        expand_iv_new_tag_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(new_tag_root_cardview, new Slide());
                if(expand_rl_add_tag_card.getVisibility() == View.VISIBLE){
                    expand_rl_add_tag_card.setVisibility(View.GONE);
                    expand_iv_new_tag_card.setImageResource(R.drawable.expand_circle_down_main_blue);
                } else{
                    expand_rl_add_tag_card.setVisibility(View.VISIBLE);
                    expand_iv_new_tag_card.setImageResource(R.drawable.expand_circle_up_main_blue);
                }
            }
        });
        redefine_cardbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                        /* apiKey */ "AIzaSyBMwM3gqqDSofRaXY9gEnyQqY3xHBC1-IU");

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
                lang_prompt_tag = basic_user_data.getAll_lang().getAll_languages().get(basic_user_data.getInstance().getPref_lang_index()).getPrompt_tag();
                String word = basic_user_data.getInstance().getAllWords().get(RV_POSITION).getWord();
                GenerativeModelFutures model = GenerativeModelFutures.from(gm);
                Content content = new Content.Builder()
                        .addText(lang_prompt_tag + word)
                        .build();

                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        System.out.println(resultText);
                        basic_user_data.add_word_ai_generated_def(RV_POSITION,removeFirstLine(resultText));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, getMainExecutor());
                edit_word_def.setText(basic_user_data.getAllWords().get(RV_POSITION).getAi_generated_def());
            }
        });
        question_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(question_card, new Slide());
                question_title_rl.setVisibility(View.GONE);
                question_card.setRadius(314);
                alt_question_rl.setVisibility(View.VISIBLE);
                question_card.setClickable(false);
            }
        });
        make_question_cardbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                //TODO: Create Saved Questions List for each word --> For better UX
                TransitionManager.beginDelayedTransition(question_ai_answer_card,new Fade());
                question_ai_answer_card.setVisibility(View.VISIBLE);
                makeQuestionAI(make_question_it.getText().toString(),getMainExecutor());
            }
        });
        next_cardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!basic_user_data.getAllWords().get(RV_POSITION).getAi_generated_def().equals(edit_word_def.getText().toString())){
                    basic_user_data.add_word_ai_generated_def(RV_POSITION,edit_word_def.getText().toString());
                }
                finish();
            }
        });
    }
    public void makeQuestionAI(String question, Executor executor){
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ "AIzaSyBMwM3gqqDSofRaXY9gEnyQqY3xHBC1-IU");
// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
        lang_prompt_tag = getResources().getString(R.string.question_default_prompt);
        String word = basic_user_data.getInstance().getAllWords().get(RV_POSITION).getWord();
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(lang_prompt_tag + word + ": " + question)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                question_ai_answer_tv.setText(result.getText());
                System.out.println("DixorAI Gemini Prompt Result --> "+result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void createAndSaveNewTag(String tag_name){
        Tag new_tag = new Tag(tag_name);
        basic_user_data.addWordTag(new_tag,RV_POSITION);
        word_tag_rv_adapter.setRv_tags(basic_user_data.getAllWords().get(RV_POSITION).getTags());
        word_tag_rv_adapter.notifyDataSetChanged();
    }
    public static String removeFirstLine(String text) {
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

    public void showSnackbar(String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(view.getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(view.getResources().getColor(R.color.white));
        snackbar.setTextColor(view.getResources().getColor(R.color.black));
        snackbar.show();
        View snackbarView = snackbar.getView();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        int marginInPixels = 21; // Set your desired margin in pixels
        int marginBottom = 250;
        // Set the margins
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginBottom);
        snackbarView.setLayoutParams(params);
    }
    public boolean searchTag(String tag_name){ // True for found
        for(Tag tag_iterator : basic_user_data.getAllWords().get(RV_POSITION).getTags()){
            if(tag_iterator.getTag_name().equals(tag_name)){
                return true;
            }
        }
        return false;
    }
}