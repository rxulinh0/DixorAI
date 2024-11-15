package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class LangCarouselRvAdapter extends RecyclerView.Adapter<LangCarouselRvAdapter.ViewHolder> {
    all_languages availableLangs;
    int selected_lang_id;
    Context context;
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public LangCarouselRvAdapter(all_languages availableLangs, Context context) {
        this.availableLangs = availableLangs;
        this.context = context;
    }

    public int getSelected_lang_id() {
        return selected_lang_id;
    }
    @Override
    public int getItemCount() {
        return all_languages.getAll_languages().size();
    }
    public void setSelected_lang_id(int selected_lang_id) {
        this.selected_lang_id = selected_lang_id;
    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lang_carousel_item,parent,false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.flag.setImageResource(availableLangs.getAll_languages().get(position).getFlagResourceId());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(position));
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView flag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flag = itemView.findViewById(R.id.lang_flag_carousel_item);
        }
    }
}
