package com.ai.dixorai;

import static android.app.PendingIntent.getActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

public class WordArchive extends AppCompatActivity {
    public RecyclerView word_archive_rv;
    public WordRvAdapter wordRvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_word_archive);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        word_archive_rv = findViewById(R.id.wordArchiveRv);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            manageRecyclerView();
        } else{
            showSnackbar(getResources().getString(R.string.device_not_supported_old_android_version));
        }
    }
    public void manageRecyclerView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wordRvAdapter = new WordRvAdapter(basic_user_data.getArchiveWords(),getApplicationContext(),WordArchive.this,getApplicationContext().getMainExecutor(),true,this.getContentResolver());
        } else{
            Log.d("DixorAI","Device not supported, API level is less than 28");
        }
        word_archive_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        word_archive_rv.setAdapter(wordRvAdapter);
        wordRvAdapter.setOnItemClickListener(new WordRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                basic_user_data.unarchiveWord(basic_user_data.getArchiveWords().get(position));
                wordRvAdapter.notifyItemChanged(position);
            }
        });
    }
    public void showSnackbar(String message){
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(viewGroup.getResources().getColor(R.color.main_blue));
        snackbar.setBackgroundTint(viewGroup.getResources().getColor(R.color.white));
        snackbar.setTextColor(viewGroup.getResources().getColor(R.color.black));
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