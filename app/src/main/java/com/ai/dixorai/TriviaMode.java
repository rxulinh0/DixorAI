package com.ai.dixorai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

public class TriviaMode extends AppCompatActivity {
    MaterialCardView all_words_card,book_mode_card,tags_mode_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trivia_mode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        all_words_card = findViewById(R.id.all_words_trivia_mode_card);
        book_mode_card = findViewById(R.id.books_trivia_mode_card);
        tags_mode_card = findViewById(R.id.tags_trivia_mode_card);
        all_words_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModalBottomSheet(R.layout.trivia_all_words_modal_bottom_sheet_content);
            }
        });
        book_mode_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModalBottomSheet(R.layout.trivia_books_modal_bottom_sheet_content);
            }
        });
        tags_mode_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModalBottomSheet(R.layout.trivia_tags_modal_bottom_sheet_content);
            }
        });
    }
    public void showModalBottomSheet(int modalContentSheetResourceID){
        ModalBottomSheetTrivia modalBottomSheetTrivia = new ModalBottomSheetTrivia(modalContentSheetResourceID,getApplicationContext());
        modalBottomSheetTrivia.show(getSupportFragmentManager(), ModalBottomSheetTrivia.TAG);
    }
}