package com.ai.dixorai;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.InsetDrawable;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import android.content.ContentResolver;
import android.content.ContentValues;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordRvAdapter extends RecyclerView.Adapter<WordRvAdapter.ViewHolder> {
    public ArrayList<Word> allWords = new ArrayList<>();
    public Context context;
    public Executor ai_api_call_executor;
    private OnItemClickListener mListener;
    private WordColorRv wordColorRv;
    private int expandedPosition = -1;
    private boolean isArchiveActivity;
    private static boolean started;
    private Context packageContext;
    private ArrayList<String> filter = new ArrayList<>();
    private ArrayList<String> booktagfilter = new ArrayList<>();
    public ArrayList<Word> updatedList = new ArrayList<>();
    private Handler handler = new Handler();
    private static final long DELAY = 100;
    private ContentResolver resolver;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public WordRvAdapter(ArrayList<Word> allWords, Context context, Context packageContext, Executor ai_api_call_executor, boolean isArchiveActivity,ContentResolver resolver) {
        this.allWords = allWords;
        this.context = context;
        this.packageContext = packageContext;
        this.ai_api_call_executor = ai_api_call_executor;
        this.isArchiveActivity = isArchiveActivity;
        this.resolver = resolver;
        started = true;
    }

    public static boolean isStarted() {
        return started;
    }

    public void setAllWords(ArrayList<Word> allWords) {
        this.allWords = allWords;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_recyclerview_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(!filter.isEmpty() && !basic_user_data.getAllWords().get(position).getTags().isEmpty()){
            filter.forEach(filter_a -> {
                basic_user_data.getAllWords().get(position).getTags().stream().filter(filter_b -> filter_a.equals(filter_b.getTag_name()))
                        .forEach(filter_b -> holder.word_item_card.setVisibility(View.GONE));
            });
        }
        else if(!booktagfilter.isEmpty() && !basic_user_data.getAllWords().get(position).getTags().isEmpty()){
            filter.forEach(filter_a -> {
                basic_user_data.getAllWords().get(position).getBookTags().stream().filter(filter_b -> filter_a.equals(filter_b.getTag_name()))
                        .forEach(filter_b -> holder.word_item_card.setVisibility(View.GONE));
            });
        }
        // Book Filter
        int font_id = basic_user_data.getPreferred_font();
        Typeface regular_typeface = ResourcesCompat.getFont(context, R.font.montserratbold);
        Typeface bold_typeface = ResourcesCompat.getFont(context, R.font.montserratblack);
        switch(font_id){
            case 0:
                break;
            case 1:
                regular_typeface = ResourcesCompat.getFont(context, R.font.librebaskervilleregular);
                bold_typeface = ResourcesCompat.getFont(context, R.font.librebaskervillebold);
                break;
                case 2:
                    regular_typeface = ResourcesCompat.getFont(context, R.font.imfellenglishregular);
                    bold_typeface = regular_typeface;
                    break;
                case 3:
                    regular_typeface = ResourcesCompat.getFont(context, R.font.fraunces);
                    bold_typeface = regular_typeface;
                    break;
                default:
                    showSnackbar(context.getResources().getString(R.string.font_not_found_error), holder.word_item_card.getRootView());
                    break;
            }
            holder.word_tv.setTypeface(bold_typeface);
            holder.word_def_tv.setTypeface(regular_typeface);
        if (isArchiveActivity) updatedList = basic_user_data.getArchiveWords();
        else updatedList = basic_user_data.getAllWords();
        holder.word_tv.setText(allWords.get(position).getWord());
        holder.word_date_tv.setText(allWords.get(position).getDate());
        int color_id = GetColor(updatedList.get(position).getColor_id());
        if (color_id == context.getResources().getColor(R.color.main_blue)) {
            holder.word_date_tv.setTextColor(Color.WHITE);
        } else {
            holder.word_date_tv.setTextColor(context.getResources().getColor(R.color.main_blue_semitransparent));
        }
        holder.word_item_card.setCardBackgroundColor(color_id); // Get Updated Color
        if (!(allWords.get(position).getAi_generated_def() == null)) {
            holder.word_def_tv.setText(getBoldSpannableString(allWords.get(position).getAi_generated_def()));
            holder.define_button_iv.setImageResource(R.drawable.redefine_icon_white);
        } else {
            holder.define_button_iv.setImageResource(R.drawable.define_icon_white);
        }
        /*holder.word_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandView(holder.word_item_card);
                switch(holder.word_def_tv.getVisibility()){
                    case View.GONE:            holder.expandView(holder.def_ll);
                        holder.word_def_tv.setVisibility(View.VISIBLE);
                        break;
                    case View.VISIBLE:
                        holder.word_def_tv.setVisibility(View.GONE);
                        break;
                    default:
                        showSnackbar(context.getString(R.string.something_went_wrong),view);
                        break;
                }
            }
        });*/
        final boolean isExpanded = position == expandedPosition;
        if (!updatedList.get(position).isExpanded_once() && updatedList.get(position).getAi_generated_def() != null) {
            basic_user_data.setExpandedOnceTrue(position);
            holder.def_ll.setVisibility(View.VISIBLE);
            holder.def_ll.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(null);
        } else {
            holder.def_ll.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.itemView.setActivated(isExpanded);
        }

        holder.word_item_card.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(position);
        });
        holder.word_item_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showWordMoreVertMenu(view, R.menu.word_vert_menu, position,holder.word_recyclerview_item);
                return false;
            }
        });
        wordColorRv = new WordColorRv(basic_user_data.getWord_color_ids(), position, context,isArchiveActivity);
        holder.colorRv.setAdapter(wordColorRv);
        holder.colorRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        wordColorRv.setOnItemClickListener(new WordColorRv.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Word currentWord = updatedList.get(position);
                currentWord.setColor_id(basic_user_data.getWord_color_ids().get(pos));
                basic_user_data.replaceWordData(position, currentWord);
                wordColorRv.notifyDataSetChanged();
                notifyItemChanged(position);
            }
        });
        if (isArchiveActivity) {
            //holder.word_item_card.setVisibility(View.VISIBLE);
            holder.define_button_iv.setImageResource(R.drawable.unarchive_icon_white);
            /*int width_height = 40,margin = 6;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width_height, width_height);
            // Set margins
            params.setMargins(margin, margin, margin, margin);
            // Apply the new LayoutParams to the ImageView
            holder.define_button_iv.setLayoutParams(params);*/
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public int GetColor(String color) {
        switch (color) {
            case "white":
                return context.getResources().getColor(R.color.white);
            case "main_blue":
                return context.getResources().getColor(R.color.main_blue);
            case "main_red_remove":
                return context.getResources().getColor(R.color.main_red_remove);
            case "purple_500":
                return context.getResources().getColor(R.color.purple_500);
            default:
                return -1;
        }
    }

    public int getItemCount() {
        int size = 0;
        if(isArchiveActivity){
            size = basic_user_data.getArchiveWords().size();
        } else{
            size = basic_user_data.getAllWords().size();
        }
        return size;
    }

    public ArrayList<String> getFilter() {
        return filter;
    }

    public void setFilter(ArrayList<String> filter) {
        this.filter = filter;
    }

    public void addBookFilter(String new_filter){
        if(!booktagfilter.contains(new_filter)){
            booktagfilter.add(new_filter);
            notifyDataSetChanged();
        }
    }

    public void removeBookFilter(String filterToRemove){
        if(!booktagfilter.contains(filterToRemove)){
            booktagfilter.add(filterToRemove);
            notifyDataSetChanged();
        }
    }

    public void addFilter(String new_filter){
        if(!filter.contains(new_filter)){
            filter.add(new_filter);
            notifyDataSetChanged();
        }
    }

    public void removeFilter(String filterToRemove){
        if(filter.contains(filterToRemove)) {
            filter.remove(filterToRemove);
            notifyDataSetChanged();
        }
    }

    public void removeAllFilters(){
        filter = new ArrayList<String>();
        notifyDataSetChanged();
    }

    public void removeAllBookFilters(){
        booktagfilter = new ArrayList<String>();
        notifyDataSetChanged();
    }

    public void updateList(List<Word> newList){
        allWords = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
    public boolean containsFilter(String filter_x){
        return filter.contains(filter_x);
    }
    public boolean containsBookFilter(String filter_x){
        return booktagfilter.contains(filter_x);
    }

    private SpannableString applyBoldWeight(Context context, String text) {
        SpannableString spannableString = new SpannableString(text);

        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Apply custom font weight with TypefaceSpan
            spannableString.setSpan(new CustomDefTypefaceSpan(context, "fonts/montserratextrabold.ttf"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
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

    private void takeScreenshot(RelativeLayout relativeLayout,ContentResolver resolver, View view) {
        // Step 1: Capture RelativeLayout as Bitmap
        Bitmap bitmap = Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        relativeLayout.draw(canvas);

        // Step 2: Save the Bitmap to Gallery
        saveBitmapToGallery(bitmap,resolver,view);
    }

    private void saveBitmapToGallery(Bitmap bitmap,ContentResolver resolver,View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "screenshot_" + System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyScreenshots");

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            showSnackbar(context.getString(R.string.screenshot_word_definition),view);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(context.getString(R.string.screenshot_error_message),view);
        }
    }

    private SpannableString getBoldSpannableString(String text) {
        SpannableString spannableString = new SpannableString(text);
        int start = -1;
        int end;

        while ((start = text.indexOf("**", start + 1)) != -1) {
            end = text.indexOf("**", start + 2);

            if (end != -1) {
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end + 1;
            }
        }

        // Remove the asterisks from the text
        String plainText = spannableString.toString().replace("**", "");
        SpannableString finalSpannableString = new SpannableString(plainText);

        // Reapply the StyleSpan to correct positions
        int plainTextIndex = 0;
        for (int i = 0; i < spannableString.length(); i++) {
            if (spannableString.charAt(i) != '*') {
                if (spannableString.getSpans(i, i + 1, StyleSpan.class).length > 0) {
                    finalSpannableString.setSpan(new StyleSpan(Typeface.BOLD), plainTextIndex, plainTextIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                plainTextIndex++;
            }
        }

        return finalSpannableString;
    }

    @SuppressLint("RestrictedApi")
    private void showWordMoreVertMenu(View v, @MenuRes int menuRes, int position,RelativeLayout screenshot_rl) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenuInflater().inflate(menuRes, popup.getMenu());
        if (popup.getMenu() instanceof MenuBuilder) { // To be tested
            MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
            for (MenuItem item : menuBuilder.getVisibleItems()) {
                int iconMarginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics()); // TODO --> CHANGE ICON_MARGIN value hardcoded to value 0
                if (item.getIcon() != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(new InsetDrawable(item.getIcon(), iconMarginPx, 0, iconMarginPx, 0));
                    } else {
                        item.setIcon(new InsetDrawable(item.getIcon(), iconMarginPx, 0, iconMarginPx, 0) {
                            @Override
                            public int getIntrinsicWidth() {
                                return getIntrinsicHeight() + iconMarginPx + iconMarginPx;
                            }
                        });
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        final int a = R.id.word_vert_option_1;
        final int b = R.id.word_vert_option_2;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.word_vert_option_1) {
                    // TODO: Edit Word Activity
                    Intent new_intent = new Intent(packageContext, EditWordActivity.class);
                    new_intent.putExtra("RV_POSITION",position);
                    packageContext.startActivity(new_intent);
                } else if (menuItem.getItemId() == R.id.word_vert_option_2) {
                    // =---- Delete Word ----=
                    Word undo_word = allWords.get(position);
                    int undo_pos = position;
                    // --
                    basic_user_data.remove_word(position);
                    notifyDataSetChanged();
                    // Show Snackbar with undo button
                    showUndoSnackbar(v, undo_pos, undo_word);
                } else if (menuItem.getItemId() == R.id.word_vert_option_3) {
                    // =---- Archive Word ----=
                    basic_user_data.archiveWord(updatedList.get(position));
                    notifyDataSetChanged();
                    //notifyItemChanged(position);
                } else if(menuItem.getItemId() == R.id.word_vert_option_4){
                    takeScreenshot(screenshot_rl,resolver, v); // Takes screenshot with takeScreenshot fun() and then saves into gallery (using Bitmaps & MediaStore)
                }
                /*switch(menuItem.getItemId()){
                    case a:
                        break;
                    case b:
                        // For undo
                        Word undo_word = allWords.get(position);
                        int undo_pos = position;
                        // --
                        basic_user_data.remove_word(position);
                        notifyDataSetChanged();
                        // Show Snackbar with undo button
                        showUndoSnackbar(v,undo_pos,undo_word);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }*/
                return false;
            }
        });
        popup.show();
    }

    private void showUndoSnackbar(@NonNull View view, int undo_pos, Word undo_word) {
        Snackbar snackbar = Snackbar.make(view, R.string.word_removed_successfully, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Responds to click on the action
                basic_user_data.add_new_word(undo_pos, undo_word);
                notifyDataSetChanged();
            }
        });
        snackbar.setActionTextColor(view.getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(view.getResources().getColor(R.color.white));
        snackbar.setTextColor(view.getResources().getColor(R.color.black));
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView word_tv, word_date_tv, word_def_tv;
        private MaterialCardView word_item_card, def_card_button, unarchive_word_card;
        private RelativeLayout word_rl_rv_item,word_recyclerview_item;
        private LinearLayout def_ll;
        private ImageView define_button_iv;
        private RecyclerView colorRv;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            word_tv = itemView.findViewById(R.id.word_rv_item_tv);
            word_date_tv = itemView.findViewById(R.id.word_date_rv_item_tv);
            word_item_card = itemView.findViewById(R.id.word_item_card);
            word_rl_rv_item = itemView.findViewById(R.id.word_rl_rv_item);
            word_recyclerview_item = itemView.findViewById(R.id.word_recyclerview_item);
            word_def_tv = itemView.findViewById(R.id.word_def_rv_tv);
            define_button_iv = itemView.findViewById(R.id.define_icon_iv);
            def_card_button = itemView.findViewById(R.id.def_cardbutton);
            colorRv = itemView.findViewById(R.id.wordColorsRv);
            def_ll = itemView.findViewById(R.id.def_ll);
            def_card_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });
            word_item_card.setOnClickListener(v -> {
                if (def_ll.getVisibility() == View.GONE) {
                    expandView(def_ll);
                } else {
                    collapseView(def_ll);
                }
            });
        }

        private void expandView(View view) {
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(null);
        }

        private void collapseView(View view) {
            view.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
