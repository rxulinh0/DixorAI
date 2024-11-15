package com.ai.dixorai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class TagRvAdapter extends RecyclerView.Adapter<TagRvAdapter.ViewHolder>{
    public ArrayList<Tag> rv_tags;
    public Context context;
    public boolean isFilterRV; // True if request is from homeFragment or false for editWord
    private OnItemClickListener listener;
    public int word_position;
    private ArrayList<String> filtered_tags = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setRv_tags(ArrayList<Tag> rv_tags) {
        this.rv_tags = rv_tags;
    }

    public TagRvAdapter(Context context, ArrayList<Tag> rv_tags, boolean isFilterRV) {
        this.rv_tags = rv_tags;
        this.context = context;
        this.isFilterRV = isFilterRV;
    }
    public TagRvAdapter(Context context, ArrayList<Tag> rv_tags, boolean isFilterRV, OnItemClickListener listener) {
        this.rv_tags = rv_tags;
        this.context = context;
        this.isFilterRV = isFilterRV;
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_rv_item, parent, false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position){
        holder.tag_name_tv.setText(rv_tags.get(position).getTag_name());
        holder.bind(rv_tags.get(position).getTag_name(), listener, position);
        if(isFilterRV){ // is from HomeFragment
            holder.tag_name_tv.setTextColor(context.getResources().getColor(R.color.white));
            if(filtered_tags.contains(rv_tags.get(position).getTag_name())){
                holder.tag_item_card.setCardBackgroundColor(context.getResources().getColor(R.color.black));
                holder.tag_item_card.setStrokeWidth(3);

            } else{
                holder.tag_item_card.setCardBackgroundColor(context.getResources().getColor(androidx.cardview.R.color.cardview_light_background));
                holder.tag_item_card.setStrokeWidth(0);
            }
        }
    }

    public ArrayList<String> getFiltered_tags() {
        return filtered_tags;
    }

    public void setFiltered_tags(ArrayList<String> filtered_tags) {
        this.filtered_tags = filtered_tags;
    }

    public void filterTag(String filter){
        if(!filtered_tags.contains(filter)){
            filtered_tags.add(filter);
            notifyDataSetChanged();
        }
    }

    public void unfilterTag(String filter){
        if(filtered_tags.contains(filter)){
            filtered_tags.remove(filter);
            notifyDataSetChanged();
        }
    }

    public void removeRvTag(int position){
        this.rv_tags.remove(position);
        notifyItemRemoved(position);
    }
    public void addAndSaveTag(Tag tag,int position, View view){
        basic_user_data.addWordTag(tag,position);
        if(!rv_tags.contains(tag)){
            rv_tags.add(tag);
        } else{
            showSnackbar(context.getResources().getString(R.string.tag_already_added),view);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Tag> getRv_tags() {
        return rv_tags;
    }

    public void updateList(List<Tag> newList) {
        rv_tags = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return rv_tags.size();
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tag_name_tv;
        private MaterialCardView tag_item_card;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tag_name_tv = itemView.findViewById(R.id.tag_item_name_tv);
            tag_item_card = itemView.findViewById(R.id.tag_item_card);
        }
        public void bind(String tag_name, OnItemClickListener listener, int position) {
            tag_name_tv.setText(tag_name);
            if(listener!=null){
                itemView.setOnClickListener(v -> listener.onItemClick(position));
            }
        }
    }
}
