package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialElevationScale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MaterialCardView definitions_settings_card, change_language,change_font,archive_settings,account,apply_change_language,about_this_software_card;
    private MaterialCardView sans_serif_card,serif_card,old_books_font_card,aesthetic_serif_font_card;
    private TextInputLayout language_menu_textinputlayout;
    private MaterialAutoCompleteTextView language_menu;
    private RecyclerView lang_carousel_rv;
    private LangCarouselRvAdapter lang_carousel_adapter;
    private RelativeLayout word_definitions_settings_expandable_rl,archive_settings_expandable_rl,fonts_rl,change_language_title_rl,change_language_rl_b;
    private Slider archive_settings_max_slider,word_def_limit_slider;
    private TextView archive_settings_max_slider_counter_tv,word_def_limit_counter_tv;
    private MaterialSwitch auto_def_word_switch;
    public SettingsFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        definitions_settings_card = view.findViewById(R.id.definitions_settings_card);
        change_language = view.findViewById(R.id.change_language_card_settings);
        change_font = view.findViewById(R.id.change_font_card_settings);
        archive_settings = view.findViewById(R.id.archive_settings_card);
        archive_settings_max_slider_counter_tv = view.findViewById(R.id.counter_archive_max_word_slider_settings_tv);
        archive_settings_expandable_rl = view.findViewById(R.id.archive_settings_card_b_rl);
        account = view.findViewById(R.id.manage_account_card_settings);
        about_this_software_card = view.findViewById(R.id.about_us_card_settings);
        auto_def_word_switch = view.findViewById(R.id.auto_def_word_settings_switch);
        word_def_limit_counter_tv = view.findViewById(R.id.counter_max_word_definition_slider_settings_tv);
        word_definitions_settings_expandable_rl = view.findViewById(R.id.word_definition_settings_card_b_rl);
        definitions_settings_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(word_definitions_settings_expandable_rl, new Slide());
                word_definitions_settings_expandable_rl.setVisibility(word_definitions_settings_expandable_rl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        archive_settings_max_slider = view.findViewById(R.id.archive_max_word_slider_settings);
        archive_settings_max_slider.setValue((float)basic_user_data.getMax_allWords_elements());
        archive_settings_max_slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if(archive_settings_max_slider.getValue()!=basic_user_data.getMax_allWords_elements()){
                    basic_user_data.setMax_allWords_elements((int)archive_settings_max_slider.getValue());
                    archive_settings_max_slider_counter_tv.setText(maxAllWordsString(archive_settings_max_slider.getValue()));
                }
            }
        });
        archive_settings_max_slider_counter_tv.setText(maxAllWordsString(archive_settings_max_slider.getValue()));
        archive_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(archive_settings, new Slide());
                archive_settings_expandable_rl.setVisibility(archive_settings_expandable_rl.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        fonts_rl = view.findViewById(R.id.change_font_card_rl_b);
        sans_serif_card = view.findViewById(R.id.sans_serif_card_fonts);
        serif_card = view.findViewById(R.id.serif_card_fonts);
        old_books_font_card = view.findViewById(R.id.old_book_fonts_card);
        aesthetic_serif_font_card = view.findViewById(R.id.aesthetic_serif_fonts_card);
        sans_serif_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(basic_user_data.getPreferred_font()!=0){
                    removeStrokeSelectedCardFont(basic_user_data.getPreferred_font());
                    basic_user_data.setPreferred_font(0);
                    setStrokeSelectedCardFont();
                }
            }
        });
        serif_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (basic_user_data.getPreferred_font()!=1) {
                    removeStrokeSelectedCardFont(basic_user_data.getPreferred_font());
                    basic_user_data.setPreferred_font(1);
                    setStrokeSelectedCardFont();
                }
            }
        });
        old_books_font_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(basic_user_data.getPreferred_font()!=2){
                    removeStrokeSelectedCardFont(basic_user_data.getPreferred_font());
                    basic_user_data.setPreferred_font(2);
                    setStrokeSelectedCardFont();
                }
            }
        });
        aesthetic_serif_font_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(basic_user_data.getPreferred_font()!=3){
                    removeStrokeSelectedCardFont(basic_user_data.getPreferred_font());
                    basic_user_data.setPreferred_font(3);
                    setStrokeSelectedCardFont();
                }
            }
        });
        change_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(change_font,new Slide());
                fonts_rl.setVisibility(fonts_rl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                switch(basic_user_data.getPreferred_font()){
                    case 0:
                        changeFontCardUI(view,R.id.sans_serif_card_fonts);
                        break;
                    case 1:
                        changeFontCardUI(view,R.id.serif_card_fonts);
                        break;
                    case 2:
                        changeFontCardUI(view,R.id.old_book_fonts_card);
                        break;
                    case 3:
                        changeFontCardUI(view,R.id.aesthetic_serif_fonts_card);
                        break;
                    default:
                        showSnackbar(getString(R.string.font_not_found_error));
                        break;
                }
            }
        });
        lang_carousel_rv = view.findViewById(R.id.language_carousel_settings_fragment);
        all_languages all_lang = basic_user_data.getAll_lang();
        lang_carousel_adapter = new LangCarouselRvAdapter(all_lang,getContext());
        lang_carousel_adapter.setOnItemClickListener(new LangCarouselRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
            }
        });
        lang_carousel_rv.setAdapter(lang_carousel_adapter);
        SnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(lang_carousel_rv);
        language_menu_textinputlayout = view.findViewById(R.id.language_menu);
        language_menu_textinputlayout.setVisibility(View.VISIBLE);
        language_menu = view.findViewById(R.id.language_menu_autocompletetv);
        change_language_title_rl = view.findViewById(R.id.change_language_title_rl);
        change_language_rl_b = view.findViewById(R.id.change_language_rl_b);
        change_language_title_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(change_language,new Slide());
                change_language_rl_b.setVisibility(change_language_rl_b.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        apply_change_language = view.findViewById(R.id.apply_change_language_settings_card);
//        if(basic_user_data.getPref_lang_short_name() != null){
//            if(basic_user_data.getPref_lang_short_name().equals(getResources().getString(R.string.en))){
//                language_menu.setText(getString(R.string.en_name));
//            } else if(basic_user_data.getPref_lang_short_name().equals(getString(R.string.es))){
//                language_menu.setText(getString(R.string.es_name));
//            }
//        }
        if(basic_user_data.getInstance().isHasPrefLangIndex() && lang_carousel_rv != null){
            lang_carousel_rv.scrollToPosition(basic_user_data.getPref_lang_index());
        }
        word_def_limit_slider = view.findViewById(R.id.archive_max_word_definition_slider_settings);
        word_def_limit_slider.setValue((float)basic_user_data.getDef_word_limit());
        word_def_limit_slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if(value != (float)basic_user_data.getDef_word_limit()){
                    basic_user_data.setDef_word_limit((int)value);
                    word_def_limit_counter_tv.setText(maxAllWordsString(word_def_limit_slider.getValue()));
                }
            }
        });
        word_def_limit_counter_tv.setText(maxAllWordsString(word_def_limit_slider.getValue()));
        auto_def_word_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                basic_user_data.setAuto_define_words(b);
                //TODO Set this to the ViewModel
            }
        });
        apply_change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resultText = language_menu.getText().toString();
                int lang_pos = -1;
                if(resultText.equals(getResources().getString(R.string.en_name))){
                    lang_pos = 0;
                } else if(resultText.equals(getResources().getString(R.string.es_name))){
                    lang_pos = 1;
                } if(lang_pos!=-1){
                    TransitionManager.beginDelayedTransition(lang_carousel_rv,new MaterialElevationScale(false));
                    lang_carousel_rv.scrollToPosition(lang_pos);
                    basic_user_data.emptyDefs();
                    basic_user_data.setPref_lang_index(lang_pos);
                    basic_user_data.setPref_lang_short_name(all_lang.getAll_languages().get(lang_pos).getLang_short_name());
                }
            }
        });
        about_this_software_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaterialAboutSoftwareDialog();
            }
        });
        //language_menu = view.findViewById(R.id.language_menu);
        //language_menu.addO
//        language_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Get the selected item
////                String selectedItem = (String) parent.getItemAtPosition(position);
////                if(selectedItem.equals(getContext().getResources().getString(R.string.en_name))){
////                    lang_carousel_rv.scrollToPosition(0);
////                } else if(selectedItem.equals(getContext().getResources().getString(R.string.es_name))){
////                    lang_carousel_rv.scrollToPosition(1);
////                } // TODO: Add German and French for MVP version
//                lang_carousel_rv.scrollToPosition(position);
//                if(basic_user_data.getPref_lang_index()!=position) {
//                    basic_user_data.emptyDefs();
//                    basic_user_data.setPref_lang_index(position);
//                    basic_user_data.setPref_lang_short_name(all_lang.getAll_languages().get(position).getLang_short_name());
//                }
//            }
//        });
        return view;
    }

    public void setStrokeSelectedCardFont(){
        switch(basic_user_data.getPreferred_font()){
            case 0:
                sans_serif_card.setStrokeWidth(3);
                break;
            case 1:
                serif_card.setStrokeWidth(3);
                break;
            case 2:
                old_books_font_card.setStrokeWidth(3);
                break;
            case 3:
                aesthetic_serif_font_card.setStrokeWidth(3);
                break;
            default:
                showSnackbar(getString(R.string.font_not_found_error));
                break;
        }
    }
    public void removeStrokeSelectedCardFont(int font_id){
        switch(font_id){
            case 0:
                sans_serif_card.setStrokeWidth(0);
                break;
            case 1:
                serif_card.setStrokeWidth(0);
                break;
            case 2:
                old_books_font_card.setStrokeWidth(0);
                break;
            case 3:
                aesthetic_serif_font_card.setStrokeWidth(0);
                break;
            default:
                showSnackbar(getString(R.string.font_not_found_error));
                break;
        }
    }

    public int getResourceIdFontCard(View view,int font_id){
        switch(font_id){
            case 0:
                return R.id.sans_serif_card_fonts;
            case 1:
                return R.id.serif_card_fonts;
            case 2:
                return R.id.old_book_fonts_card;
            case 3:
                return R.id.aesthetic_serif_fonts_card;
            default:
                return -1;
        }
    }

    public void showSnackbar(String message){
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

    public void showMaterialAboutSoftwareDialog(){
        // Inflate the custom layout for the dialog
        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setIcon(R.mipmap.ic_launcher_round)
                .setTitle(getContext().getResources().getString(R.string.app_name))
                .setMessage(getContext().getResources().getString(R.string.about_us_dialog_text))
                .setNeutralButton(getContext().getResources().getString(R.string.close), (dialog, which) -> {
                    // Respond to neutral button press --> Just closes the popup
                })
                .show();
    }

    public void changeFontCardUI(View view,int resource_id){
        MaterialCardView font_card = view.findViewById(resource_id);
        font_card.setStrokeWidth(3);
    }

    public String maxAllWordsString(float value){
        return String.valueOf(value) + " " + getResources().getString(R.string.words);
    }
}