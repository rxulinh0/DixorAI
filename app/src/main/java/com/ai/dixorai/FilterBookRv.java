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

public class FilterBookRv extends RecyclerView.Adapter<FilterBookRv.ViewHolder> {
    public ArrayList<BookTag> booktags = new ArrayList<>();
    public Context context;
    public boolean isFilterRV; // True if request is from homeFragment or false for editWord
    private FilterBookRv.OnItemClickListener listener;
    public int word_position;
    private ArrayList<String> filtered_tags = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setRv_tags(ArrayList<BookTag> booktags){
        this.booktags = booktags;
    }

    public FilterBookRv(Context context, ArrayList<BookTag> booktags, boolean isFilterRV) {
        this.booktags = booktags;
        this.context = context;
        this.isFilterRV = isFilterRV;
    }
    public FilterBookRv(Context context, ArrayList<BookTag> booktags, boolean isFilterRV, FilterBookRv.OnItemClickListener listener) {
        this.booktags = booktags;
        this.context = context;
        this.isFilterRV = isFilterRV;
        this.listener = listener;
    }

    public FilterBookRv.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_rv_item, parent, false);
        return new FilterBookRv.ViewHolder(view);
    }
    public void onBindViewHolder(@NonNull FilterBookRv.ViewHolder holder, @SuppressLint("RecyclerView") int position){
        holder.tag_name_tv.setText(booktags.get(position).getTag_name());
        if(isFilterRV){ // is from HomeFragment
            holder.tag_name_tv.setTextColor(context.getResources().getColor(R.color.white));
            if(filtered_tags.contains(booktags.get(position).getTag_name())){
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
        }
    }

    public void unfilterTag(String filter){
        if(filtered_tags.contains(filter)){
            filtered_tags.remove(filter);
        }
    }

    public void removeRvTag(int position){
        this.booktags.remove(position);
        notifyItemRemoved(position);
    }
    public void addAndSaveTag(BookTag tag,int position, View view){
        basic_user_data.addWordTag(tag,position);
        if(!booktags.contains(tag)){
            booktags.add(tag);
        } else{
            showSnackbar(context.getResources().getString(R.string.tag_already_added),view);
        }
        notifyDataSetChanged();
    }

    public ArrayList<BookTag> getRv_tags() {
        return booktags;
    }

    public void updateList(List<BookTag> newList) {
        booktags = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public int getItemCount(){
        return booktags.size();
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
//        public void bind(String tag_name, TagRvAdapter.OnItemClickListener listener, int position) {
//            tag_name_tv.setText(tag_name);
//            if(listener!=null){
//                itemView.setOnClickListener(v -> listener.onItemClick(position));
//            }
//        }
    }
}
