package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class WordColorRv extends RecyclerView.Adapter<WordColorRv.ViewHolder>{
    public ArrayList<String> available_colors = new ArrayList<>();
    private OnItemClickListener mListener;
    private int word_pos;
    private Context context;
    private boolean isArchiveActivity;

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_color_rv_item,parent,false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public int getItemCount() {
        return available_colors.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public WordColorRv(ArrayList<String> available_colors, int word_pos, Context context, boolean isArchiveActivity) {
        this.available_colors = available_colors;
        this.word_pos = word_pos;
        this.context = context;
        this.isArchiveActivity = isArchiveActivity;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position){
        int color_id = GetColor(basic_user_data.getWord_color_ids().get(position),holder.color_card.getContext());
        if(!available_colors.isEmpty() && color_id != -1){
            holder.color_card.setCardBackgroundColor(color_id);
        }
        ArrayList<Word> allWords;
        if(isArchiveActivity){
            allWords = basic_user_data.getArchiveWords();
        } else{
            allWords = basic_user_data.getAllWords();
        }
        if(allWords.get(word_pos).getColor_id().equals(available_colors.get(position))){
            holder.color_card.setVisibility(View.GONE);
        } else{
            holder.color_card.setVisibility(View.VISIBLE);
        }
        /*holder.color_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(basic_user_data.getAllWords().get(word_pos).getColor_id() != available_colors.get(position)){
                    Word currentWord = basic_user_data.getAllWords().get(word_pos);
                    Toast.makeText(holder)
                    currentWord.setColor_id(available_colors.get(position));
                    basic_user_data.changeWordColor(word_pos,available_colors.get(position));
                    notifyDataSetChanged();
                }
            }
        });*/
    }

    public int GetColor(String color, Context context){
        switch(color){
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        private MaterialCardView color_card;
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            color_card = itemView.findViewById(R.id.color_rv_item_card);
            color_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });
        }
    }
}
