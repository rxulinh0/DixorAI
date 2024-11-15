package com.ai.dixorai;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Locale;

public class ReadingMode extends AppCompatActivity {
    public RecyclerView bookMenuRv;
    public BookMenuAdapter bookMenuAdapter;
    public RelativeLayout expandAddBookCardRL;
    public TextInputEditText add_book_it;
    public MaterialCardView add_book_card, add_book_button;
    public SpeechRecognizer speechRecognizer;
    private static int REQUEST_CODE = 103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading_mode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        add_book_card = findViewById(R.id.readingModeAddBookCard);
        add_book_it = findViewById(R.id.add_new_book_card_input_text_reading_mode);
        add_book_button = findViewById(R.id.add_typed_word_reading_mode);
        expandAddBookCardRL = findViewById(R.id.type_book_rl_card_reading_mode);
        add_book_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TransitionManager.beginDelayedTransition(add_book_card,new Slide());
                expandAddBookCardRL.setVisibility(expandAddBookCardRL.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                return false;
            }
        });
        add_book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookMenuAdapter.addBookTag(new BookTag(add_book_it.getText().toString()));
            }
        });
        bookMenuRv = findViewById(R.id.readingModeBookRV);
        bookMenuAdapter = new BookMenuAdapter(basic_user_data.getAllExistingBookTags(),getApplicationContext());
        bookMenuRv.setAdapter(bookMenuAdapter);
        bookMenuAdapter.setOnItemClickListener(new BookMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position){
                Intent new_intent = new Intent(getApplicationContext(), ReadingBookActivity.class);
                new_intent.putExtra("BOOK_NAME", bookMenuAdapter.getBookByAdapterId(position).getTag_name());
                startActivity(new_intent);
            }
        });
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Ready to listen
                showSnackbar(add_book_card.getRootView(), getResources().getString(R.string.ready_to_listen));
            }

            @Override
            public void onBeginningOfSpeech() {
                // User has started to speak
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // The sound level in the audio stream has changed
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // More sound has been received
            }

            @Override
            public void onEndOfSpeech() {
                // User has stopped speaking
            }

            @Override
            public void onError(int error) {
                // Handle error
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    Toast.makeText(getApplicationContext(), recognizedText, Toast.LENGTH_SHORT).show();
                    expandAddBookCardRL.setVisibility(View.VISIBLE);
                    add_book_it.setText(recognizedText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Partial results received
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // A segment of the audio stream has been recognized
            }
        });
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            showSnackbar(add_book_card.getRootView(),getString(R.string.google_assistant_not_supported));
        }
    }

    public void showSnackbar(View view, String message) {
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
}