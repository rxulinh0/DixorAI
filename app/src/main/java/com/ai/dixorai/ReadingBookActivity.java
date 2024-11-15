package com.ai.dixorai;

import static android.app.PendingIntent.getActivity;
import static android.provider.UserDictionary.Words.addWord;
import static com.ai.dixorai.HomeFragment.removeFirstLine;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

public class ReadingBookActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 102;
    private String BOOK_NAME;
    private BookTag currentBook;
    private RecyclerView wordRV,readingBookStaggeredTagsRv;
    private SpeechRecognizer speechRecognizer;
    private WordRvAdapter wordRvAdapter;
    private TagRvAdapter readingBookStaggeredTagRv;
    private MaterialCardView add_word_speech_button,add_typed_word,removeAllButton,archiveAllButton;
    private RelativeLayout type_word_rl_card_b,empty_list_message_rl;
    private TextView title_book_name_tv;
    private TextInputEditText add_word_input_text;
    private Executor this_executor;
    private basic_user_data user_instance;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BOOK_NAME = getIntent().getStringExtra("BOOK_NAME");
        if(BOOK_NAME.isEmpty()){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_went_wrong),Toast.LENGTH_LONG).show();
            finish();
        }
        user_instance = basic_user_data.getInstance();
        final boolean[] type_word_rl_card_visible = {false};
        final RelativeLayout[] type_word_rl_card = {findViewById(R.id.type_word_rl_card_reading_book)};
        add_word_speech_button = findViewById(R.id.readingBookAddWordCard);
        add_typed_word = findViewById(R.id.add_typed_word_reading_book);
        add_word_input_text = findViewById(R.id.add_new_book_card_input_text_reading_book);
        type_word_rl_card_b = findViewById(R.id.type_word_rl_card_reading_book);
        empty_list_message_rl = findViewById(R.id.word_list_empty_message_rl_reading_book);
        currentBook = new BookTag(BOOK_NAME);
        wordRV = findViewById(R.id.readingBookWordRv);
        archiveAllButton = findViewById(R.id.archiveAllButtonReadingBook);
        removeAllButton = findViewById(R.id.removeAllButtonReadingBook);
        title_book_name_tv = findViewById(R.id.title_reading_book_name_tv);
        title_book_name_tv.setText(BOOK_NAME);
        AIGenWordDef aiGenWordDef = new AIGenWordDef();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wordRvAdapter = new WordRvAdapter(basic_user_data.getAllWords(currentBook),getApplicationContext(),getApplicationContext(),getMainExecutor(),false,this.getContentResolver());
            this_executor = getMainExecutor();
        } else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.device_not_supported_old_android_version),Toast.LENGTH_LONG).show();
        }
        wordRV.setAdapter(wordRvAdapter);
        refreshRvVisibility();
        wordRvAdapter.setOnItemClickListener(new WordRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView word_def_tv = view.findViewById(R.id.word_def_rv_tv);
                GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                        /* apiKey */ "AIzaSyBMwM3gqqDSofRaXY9gEnyQqY3xHBC1-IU");

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
                String lang_prompt_tag = basic_user_data.getAll_lang().getAll_languages().get(basic_user_data.getInstance().getPref_lang_index()).getPrompt_tag();
                String word = basic_user_data.getInstance().getAllWords().get(position).getWord();
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
                        basic_user_data.add_word_ai_generated_def(position,removeFirstLine(resultText));
                        // viewModel.addWord(basic_user_data.getAllWords().get(0));
                        //wordRvAdapter.notifyItemChanged(position);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, this_executor);
            }
        });
        add_word_speech_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognition();
            }
        });
        add_word_speech_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TransitionManager.beginDelayedTransition(add_word_speech_button, new Slide());
                if(type_word_rl_card_visible[0]){
                    type_word_rl_card_visible[0] = false;
                    type_word_rl_card[0].setVisibility(View.GONE);
                } else{
                    type_word_rl_card_visible[0] = true;
                    type_word_rl_card[0].setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        // speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        //speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Ready to listen
                showSnackbar(add_word_speech_button.getRootView(), getResources().getString(R.string.ready_to_listen));
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
                    type_word_rl_card_b.setVisibility(View.VISIBLE);
                    add_word_input_text.setText(recognizedText);
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
        // Code Section For Typing words with keyboard (TWK)
        add_typed_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!add_word_input_text.getText().toString().isEmpty()) {
                    if (basic_user_data.getAllWords(currentBook).isEmpty()) {
                        empty_list_message_rl.setVisibility(View.GONE);
                        wordRV.setVisibility(View.VISIBLE);
                    }
                    addWord(add_word_input_text.getText().toString());
                    wordRvAdapter.setAllWords(basic_user_data.getAllWords(currentBook));
                    wordRvAdapter.notifyDataSetChanged();
                    testWithToast();
                    TransitionManager.beginDelayedTransition(wordRV, new Slide());
                    add_word_input_text.setText("");
                    refreshRvVisibility();
                }
            }
        });
        archiveAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showArchiveAllDialog(getApplicationContext());
            }
        });
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAllDialog(getApplicationContext());
            }
        });
    }
    public void showRemoveAllDialog(Context context){
        new MaterialAlertDialogBuilder(context)
                .setTitle(context.getResources().getString(R.string.remove_all_words_dialog_title))
                .setMessage(context.getResources().getString(R.string.remove_all_words_dialog_message))
                .setNeutralButton(context.getResources().getString(R.string.cancel), (dialog, which) -> {

                })
                .setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> {
                    ArrayList<Word> undo = basic_user_data.getAllWords(currentBook);
                    basic_user_data.removeAllWords();
                    refreshRvVisibility();
                    showUndoRemoveSnackbar(undo);
                })
                .show();
    }
    public void showArchiveAllDialog(Context context){
        new MaterialAlertDialogBuilder(context)
                .setTitle(context.getResources().getString(R.string.archive_all_words_dialog_title))
                .setMessage(context.getResources().getString(R.string.archive_all_words_dialog_message))
                .setNeutralButton(context.getResources().getString(R.string.cancel), (dialog, which) -> {

                })
                .setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> {
                    ArrayList<Word> undo = basic_user_data.getAllWords(currentBook);
                    basic_user_data.archiveAllWords(currentBook);
                    refreshRvVisibility();
                    showUndoArchiveSnackbar(add_word_speech_button.getRootView(),undo);
                })
                .show();
    }
    public void testWithToast(){
        for(Word word : basic_user_data.getAllWords(currentBook)){
            Toast.makeText(getApplicationContext(),word.getWord(), Toast.LENGTH_SHORT).show();
        }
    }
    private void showUndoRemoveSnackbar(ArrayList<Word> allWordsUndo) {
        Snackbar snackbar = Snackbar.make(add_word_speech_button.getRootView(), R.string.all_words_removed_successfully, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Responds to click on the action
                basic_user_data.setAllWords(allWordsUndo);
                refreshRvVisibility();
                TransitionManager.beginDelayedTransition(wordRV, new Slide());
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(getResources().getColor(R.color.white));
        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.show();
        // Set margins for the Snackbar
        View snackbarView = snackbar.getView();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        int marginInPixels = 21; // Set your desired margin in pixels
        int marginBottom = 250;
        // Set the margins
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginBottom);
        snackbarView.setLayoutParams(params);
    }
    private void showUndoArchiveSnackbar(View view,ArrayList<Word> allWordsUndo){
        Snackbar snackbar = Snackbar.make(view, R.string.all_words_archived_successfully, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Responds to click on the action
                basic_user_data.undoArchiveAllWords(allWordsUndo);
                refreshRvVisibility();
                TransitionManager.beginDelayedTransition(wordRV, new Slide());
            }
        });
        snackbar.setActionTextColor(view.getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(view.getResources().getColor(R.color.white));
        snackbar.setTextColor(view.getResources().getColor(R.color.black));
        snackbar.show();
        snackbar.show();
        // Set margins for the Snackbar
        View snackbarView = snackbar.getView();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        int marginInPixels = 21; // Set your desired margin in pixels
        int marginBottom = 250;
        // Set the margins
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginBottom);
        snackbarView.setLayoutParams(params);
    }
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            showSnackbar(add_word_speech_button.getRootView(), getString(R.string.google_assistant_not_supported));
        }
    }
    public void refreshRvVisibility(){
        if(!basic_user_data.getAllWords(currentBook).isEmpty()) {
            wordRV.setVisibility(View.VISIBLE);
        } else{
            wordRV.setVisibility(View.GONE);
        }
        wordRvAdapter.setAllWords(basic_user_data.getAllWords(currentBook));
        wordRvAdapter.notifyDataSetChanged();
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
    public void addWord(String word){
        for(Word iterator : user_instance.getAllWords()){
            if(iterator.getWord().equals(word)){
                showSnackbar(add_word_speech_button.getRootView(), getString(R.string.word_repeated_message));
                return;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());
        Word new_word = new Word(word,currentDate,currentBook); // Creates word without tags & color
        user_instance.add_new_word(new_word);
    }
}