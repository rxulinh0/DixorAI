package com.ai.dixorai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class getting_started_b extends AppCompatActivity {
    private TextInputEditText name_edt;
    private MaterialCardView next_button;
    private TextView selected_language;
    private RecyclerView languages_carousel_rv;
    private int carousel_selected_position = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_getting_started_b);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });
        name_edt = findViewById(R.id.name_question_edt);
        next_button = findViewById(R.id.next_gs_b_card_button);
        // Getting languages resources ids
        int[] resourcesId = {R.drawable.gb,R.drawable.es};
        all_languages all_lang = new all_languages(resourcesId);
        basic_user_data.setAll_lang(all_lang);
        // Setting languages carousel
        languages_carousel_rv = findViewById(R.id.language_carousel_gs_b);
        LangCarouselRvAdapter lang_carousel_adapter = new LangCarouselRvAdapter(all_lang,getApplicationContext());
        CarouselLayoutManager langCarouselLayoutManager = new CarouselLayoutManager(new HeroCarouselStrategy());
        languages_carousel_rv.setAdapter(lang_carousel_adapter);
        languages_carousel_rv.setLayoutManager(langCarouselLayoutManager);
        SnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(languages_carousel_rv);
        selected_language = findViewById(R.id.selected_language_gs_b_tv);
        lang_carousel_adapter.setOnItemClickListener(new LangCarouselRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                carousel_selected_position = position;
                if(position!=-1){
                    selected_language.setVisibility(View.VISIBLE);
                }
                String lang_name = all_lang.getAll_languages().get(position).getLang_common_name();
                selected_language.setText(lang_name);
            }
        });
        //..
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name_edt.toString().isEmpty() && carousel_selected_position!=-1){
                    saveData(name_edt.getText().toString(),all_lang.getAll_languages().get(carousel_selected_position).getLang_short_name(),carousel_selected_position);
                    Intent new_intent = new Intent(getting_started_b.this,MainActivity.class);
                    new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(new_intent);
                    finishAffinity();
                }
            }
        });
    }
    public void saveData(String name,String pref_lang_short_name,int pref_lang_index){
        basic_user_data userInstance = basic_user_data.getInstance();
        userInstance.setUser_name(name);
        userInstance.setPref_lang_short_name(pref_lang_short_name);
        userInstance.setPref_lang_index(pref_lang_index);
        if(userInstance.getWord_color_ids().isEmpty()){
            basic_user_data.addWordColor("white");
            basic_user_data.addWordColor("main_blue");
            basic_user_data.addWordColor("main_red_remove");
            basic_user_data.addWordColor("purple_500");
        }
    }
}