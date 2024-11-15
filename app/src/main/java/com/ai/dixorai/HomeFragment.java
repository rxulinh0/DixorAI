package com.ai.dixorai;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButtonToggleGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //private SpeechRecognizer speechRecognizer;
    //private static final int REQUEST_CODE = 1234;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE = 100, REQUEST_CODE_B = 101;
    private SpeechRecognizer speechRecognizer;
    private WordRvAdapter wordRvAdapter;
    private TagRvAdapter filterByExistingTagsRvAdapter;
    private FilterBookRv filterByBookTagsRvAdapter;
    private RecyclerView wordRV,existingTagsRV,bookTagsRV;
    private basic_user_data user_instance = basic_user_data.getInstance();
    public TextInputEditText add_word_input_text;
    public RelativeLayout wordRVRL,type_word_rl_card_b,empty_list_message_rl;
    private MaterialCardView add_word_speech_button,add_typed_word,removeAllButton,archiveAllButton;
    private MaterialButtonToggleGroup toggleButton;
    public String lang_prompt_tag;
    private boolean isVisibleToUser;
    private Chip reading_mode,trivia_mode,poet_mode;
    private Handler handler = new Handler();
    private final static int DELAY = 100;
    // ViewModel
    private WordTagViewModel viewModel;
    //----
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        reading_mode = view.findViewById(R.id.reading_mode_chip);
        trivia_mode = view.findViewById(R.id.trivia_mode_chip);
        poet_mode = view.findViewById(R.id.poet_mode_chip);
        add_word_speech_button = view.findViewById(R.id.home_add_new_word_card);
        add_typed_word = view.findViewById(R.id.add_typed_word_home_fragment);
        add_word_input_text = view.findViewById(R.id.add_new_word_card_input_text);
        empty_list_message_rl = view.findViewById(R.id.word_list_empty_message_rl);
        final RelativeLayout[] type_word_rl_card = {view.findViewById(R.id.type_word_rl_card_home_fragment)};
        type_word_rl_card_b = view.findViewById(R.id.type_word_rl_card_home_fragment);
        wordRVRL = view.findViewById(R.id.homeFragmentWordsRVRL);
        removeAllButton = view.findViewById(R.id.removeAllButton);
        archiveAllButton = view.findViewById(R.id.archiveAllButton);
        existingTagsRV = view.findViewById(R.id.staggeredRvExistingTagsHome);
        toggleButton = view.findViewById(R.id.filterByBookOrTagSegmentedButton);
        final boolean[] type_word_rl_card_visible = {false};
        wordRV = view.findViewById(R.id.homeFragmentWordsRV);
        AIGenWordDef aiGenWordDef = new AIGenWordDef();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wordRvAdapter = new WordRvAdapter(user_instance.getAllWords(), getActivity().getApplicationContext(),getActivity(), getActivity().getMainExecutor(),false,this.getContext().getContentResolver());
        } else{
            Toast.makeText(getContext(),getResources().getString(R.string.device_not_supported_old_android_version), Toast.LENGTH_LONG).show();
        }
        // Viewmodel ---> to test
        viewModel = new ViewModelProvider(this).get(WordTagViewModel.class);
        //
        wordRV.setAdapter(wordRvAdapter);
        wordRV.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshRvVisibility();
        wordRvAdapter.setOnItemClickListener(new WordRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView word_def_tv = view.findViewById(R.id.word_def_rv_tv);
                GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                            /* apiKey */ BuildConfig.API_KEY);

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
                lang_prompt_tag = basic_user_data.getAll_lang().getAll_languages().get(basic_user_data.getInstance().getPref_lang_index()).getPrompt_tag();
                String word = basic_user_data.getInstance().getAllWords().get(position).getWord();
                GenerativeModelFutures model = GenerativeModelFutures.from(gm);
                Content content = new Content.Builder()
                        .addText(lang_prompt_tag + String.valueOf(basic_user_data.getDef_word_limit()) + "): " + word)
                        .build();

                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = removeFirstLine(result.getText());
                        System.out.println(resultText);
                        basic_user_data.add_word_ai_generated_def(position,resultText);
                        viewModel.addWord(basic_user_data.getAllWords().get(0));
                        //startTypewriterEffect(word_def_tv,resultText);
                        //wordRvAdapter.notifyItemChanged(position);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        }
                    }, getActivity().getMainExecutor());
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
        //speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        //speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Ready to listen
                Toast.makeText(getContext(), "Ready To Listen", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    Toast.makeText(getContext(), recognizedText, Toast.LENGTH_SHORT).show();
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
                if(!add_word_input_text.getText().toString().isEmpty()){
                    if(user_instance.getAllWords().isEmpty()){
                        empty_list_message_rl.setVisibility(View.GONE);
                        wordRV.setVisibility(View.VISIBLE);
                    }
                    if(basic_user_data.isAuto_define_words()){
                        addWord(add_word_input_text.getText().toString(), aiGenWordDef.generate_ai_word_def(add_word_input_text.getText().toString(), basic_user_data.getDef_word_limit(), getActivity().getMainExecutor()));
                    } else{
                        addWord(add_word_input_text.getText().toString());
                    }
                    wordRvAdapter.setAllWords(user_instance.getAllWords());
                    if(wordRvAdapter.getItemCount() > basic_user_data.getMax_allWords_elements()){
                        basic_user_data.archiveLastAllWords();
                    }
                    wordRvAdapter.notifyDataSetChanged();
                    TransitionManager.beginDelayedTransition(wordRV, new Slide());
                    add_word_input_text.setText("");
                    refreshRvVisibility();
                }
            }
        });
        TextView tv_ai_test = view.findViewById(R.id.empty_list_text_home_fragment_message);
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ BuildConfig.API_KEY);

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(getString(R.string.write_joke)+" "+user_instance.getUser_name())
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                System.out.println(resultText);
                tv_ai_test.setText(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, getActivity().getMainExecutor());

        MaterialCardView word_archive_card = view.findViewById(R.id.word_archive_card_home_fragment);
        word_archive_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_intent = new Intent(getActivity(),WordArchive.class);
                startActivityForResult(new_intent,REQUEST_CODE_B);
            }
        });
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAllDialog(getContext());
            }
        });
        archiveAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showArchiveAllDialog(getContext());
            }
        });
        filterByExistingTagsRvAdapter = new TagRvAdapter(getContext(), basic_user_data.getAllExistingTags(), true, position -> {
            ArrayList<Tag> existing_tags = basic_user_data.getAllExistingTags();
            if(!wordRvAdapter.containsFilter(existing_tags.get(position).getTag_name())){
                wordRvAdapter.addFilter(existing_tags.get(position).getTag_name());
                filterByExistingTagsRvAdapter.filterTag(existing_tags.get(position).getTag_name());
            } else if(wordRvAdapter.containsFilter(existing_tags.get(position).getTag_name())){
                wordRvAdapter.removeFilter(existing_tags.get(position).getTag_name());
                filterByExistingTagsRvAdapter.unfilterTag(existing_tags.get(position).getTag_name());
            } else{
                Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                Log.d("DixorAI_Error","Something went wrong while setting filters to HomeFragment RecyclerView");
            }
            filterByExistingTagsRvAdapter.notifyItemChanged(position);
        });
        filterByBookTagsRvAdapter = new FilterBookRv(getContext(), basic_user_data.getAllExistingBookTags(), true, position-> {
            ArrayList<BookTag> existing_tags = basic_user_data.getAllExistingBookTags();
            if(!wordRvAdapter.containsBookFilter(existing_tags.get(position).getTag_name())){
                wordRvAdapter.addBookFilter(existing_tags.get(position).getTag_name());
                filterByBookTagsRvAdapter.filterTag(existing_tags.get(position).getTag_name());
            } else if(wordRvAdapter.containsBookFilter(existing_tags.get(position).getTag_name())){
                wordRvAdapter.removeBookFilter(existing_tags.get(position).getTag_name());
                filterByExistingTagsRvAdapter.unfilterTag(existing_tags.get(position).getTag_name());
            } else{
                Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                Log.d("DixorAI_Error","Something went wrong while setting filters to HomeFragment RecyclerView");
            }
            filterByBookTagsRvAdapter.notifyItemChanged(position);
        });
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        existingTagsRV.setLayoutManager(staggeredGridLayoutManager);
        existingTagsRV.setAdapter(filterByExistingTagsRvAdapter);
        // ViewModel --> to test
        viewModel.getAllWords().observe(getViewLifecycleOwner(), new Observer<List<Word>>(){
            @Override
            public void onChanged(List<Word> wordList){
                wordRvAdapter.updateList(wordList);
            }
        });
        viewModel.getExisting_tags().observe(getViewLifecycleOwner(), new Observer<List<Tag>>(){
            @Override
            public void onChanged(List<Tag> existingTagList){
                filterByExistingTagsRvAdapter.updateList(existingTagList);
            }
        });
        viewModel.getExisting_books().observe(getViewLifecycleOwner(), new Observer<List<BookTag>>() {
            @Override
            public void onChanged(List<BookTag> existingBooksList){
                filterByBookTagsRvAdapter.updateList(existingBooksList);
            }
        });
        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                // Respond to button selection
                if(isChecked){
                    if(checkedId==R.id.filterByBookButton){
                        toggleButton.uncheck(R.id.filterByTagButton);
                        existingTagsRV.setVisibility(View.GONE);
                        bookTagsRV.setVisibility(View.VISIBLE);
                    } else if(checkedId==R.id.filterByTagButton){
                        toggleButton.uncheck(R.id.filterByBookButton);
                        existingTagsRV.setVisibility(View.VISIBLE);
                        bookTagsRV.setVisibility(View.GONE);
                    }
                }
            }
        });
        reading_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_intent = new Intent(getActivity(),ReadingMode.class);
                startActivity(new_intent);
            }
        });
        trivia_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_intent = new Intent(getActivity(),TriviaMode.class);
                startActivity(new_intent);
            }
        });
        return view;
    }

    public void refreshRvVisibility(){
        if(!basic_user_data.getAllWords().isEmpty()) {
            wordRVRL.setVisibility(View.VISIBLE);
        } else{
            wordRVRL.setVisibility(View.GONE);
        }
        wordRvAdapter.setAllWords(basic_user_data.getAllWords());
        wordRvAdapter.notifyDataSetChanged();
    }

    public void showRemoveAllDialog(Context context){
        new MaterialAlertDialogBuilder(context)
                .setTitle(context.getResources().getString(R.string.remove_all_words_dialog_title))
                .setMessage(context.getResources().getString(R.string.remove_all_words_dialog_message))
                .setNeutralButton(context.getResources().getString(R.string.cancel), (dialog, which) -> {

                })
                .setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) -> {
                    ArrayList<Word> undo = basic_user_data.getAllWords();
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
                    ArrayList<Word> undo = basic_user_data.getAllWords();
                    basic_user_data.archiveAllWords();
                    refreshRvVisibility();
                    showUndoArchiveSnackbar(undo);
                })
                .show();
    }

    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getView().getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(getView().getResources().getColor(R.color.white));
        snackbar.setTextColor(getView().getResources().getColor(R.color.black));
        snackbar.show();
        View snackbarView = snackbar.getView();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        int marginInPixels = 21; // Set your desired margin in pixels
        int marginBottom = 250;
        // Set the margins
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginBottom);
        snackbarView.setLayoutParams(params);
    }

    private void showUndoRemoveSnackbar(ArrayList<Word> allWordsUndo) {
        Snackbar snackbar = Snackbar.make(getView(), R.string.all_words_removed_successfully, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Responds to click on the action
                basic_user_data.setAllWords(allWordsUndo);
                refreshRvVisibility();
                TransitionManager.beginDelayedTransition(wordRV, new Slide());
            }
        });
        snackbar.setActionTextColor(getView().getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(getView().getResources().getColor(R.color.white));
        snackbar.setTextColor(getView().getResources().getColor(R.color.black));
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

    private void showUndoArchiveSnackbar(ArrayList<Word> allWordsUndo){
        Snackbar snackbar = Snackbar.make(getView(), R.string.all_words_archived_successfully, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Responds to click on the action
                basic_user_data.undoArchiveAllWords(allWordsUndo);
                refreshRvVisibility();
                TransitionManager.beginDelayedTransition(wordRV, new Slide());
            }
        });
        snackbar.setActionTextColor(getView().getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(getView().getResources().getColor(R.color.white));
        snackbar.setTextColor(getView().getResources().getColor(R.color.black));
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

    @Override
    public void onResume() {
        super.onResume();
        isVisibleToUser = true; // Fragment is visible
        // You can perform actions when the fragment becomes visible
        Log.d("DixorAI_HomeFragment", "Home Fragment is visible");
        if(WordRvAdapter.isStarted() && wordRvAdapter.getItemCount() > 0){
            wordRvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleToUser = false; // Fragment is not visible
        // You can perform actions when the fragment is no longer visible
        Log.d("DixorAI_HomeFragment", "Fragment is not visible");
    }

    public boolean isFragmentVisible() {
        return isVisibleToUser; // Method to check visibility
    }

    /*private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                Toast.makeText(getActivity(), spokenText, Toast.LENGTH_LONG).show();
            }
        }
    }*/
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
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            showSnackbar(getString(R.string.google_assistant_not_supported));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("resultKey");
                wordRvAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addWord(String word){
        for(Word iterator : user_instance.getAllWords()){
            if(iterator.getWord().equals(word)){
                Snackbar.make(getView(), getString(R.string.word_repeated_message), Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());
        Word new_word = new Word(word,currentDate); // Creates word without tags & color
        basic_user_data new_instance = basic_user_data.getInstance();
        user_instance.add_new_word(new_word);
    }

    public void addWord(String word,String word_ai_gen_def){
        for(Word iterator : user_instance.getAllWords()){
            if(iterator.getWord().equals(word)){
                Snackbar.make(getView(), getString(R.string.word_repeated_message), Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());
        Word new_word = new Word(word,currentDate,word_ai_gen_def); // Creates word without tags & color
        basic_user_data new_instance = basic_user_data.getInstance();
        user_instance.add_new_word(new_word);
    }
}