package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class selectWordRvAdapter extends RecyclerView.Adapter<selectWordRvAdapter.ViewHolder>{
    private ArrayList<Word> wordList = new ArrayList<>();
    private boolean[] checkedWordList;
    private Context context;
    public selectWordRvAdapter(Context context, ArrayList<Word> wordList) {
        this.wordList = wordList;
        this.checkedWordList = new boolean[wordList.size()];
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectwordrvitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.wordNameTV.setText(wordList.get(position).getWord());
        if(!checkedWordList[position]){
            holder.wordCard.setCardBackgroundColor(context.getResources().getColor(R.color.main_blue));
        } else{
            holder.wordCard.setCardBackgroundColor(context.getResources().getColor(R.color.purple_500));
        }
        holder.wordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkedWordList[position]){
                    checkedWordList[position] = true;
                    holder.wordCard.setCardBackgroundColor(context.getResources().getColor(R.color.main_blue));
                    //Log.d("DixorAI","Word chip set to true");
                } else{
                    checkedWordList[position] = false;
                    holder.wordCard.setCardBackgroundColor(context.getResources().getColor(R.color.purple_500));
                    //Log.d("DixorAI","Word chip set to false");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public int[] getSelectedIds(){
        int c = 0;
        for(int i = 0 ; i < wordList.size() ; ++i){
            if(checkedWordList[i]){
                ++c;
            }
        }
        int[] result = new int[c];
        c = 0;
        for(int i = 0 ; i < wordList.size() ; ++i){
            if(checkedWordList[i]){
                result[c] = i;
                ++c;
            }
        }
        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView wordCard;
        private TextView wordNameTV;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            wordNameTV = itemView.findViewById(R.id.word_item_name_tv);
            wordCard = itemView.findViewById(R.id.word_item_card);
        }
    }
}
