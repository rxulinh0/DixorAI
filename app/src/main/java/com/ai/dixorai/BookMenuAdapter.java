package com.ai.dixorai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class BookMenuAdapter extends RecyclerView.Adapter<BookMenuAdapter.ViewHolder>{
    private ArrayList<BookTag> allBookTags = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public BookMenuAdapter(ArrayList<BookTag> allBookTags, Context context) {
        this.allBookTags = allBookTags;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_rv_item, parent, false);
        return new ViewHolder(view, listener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull BookMenuAdapter.ViewHolder holder, int position) {
        holder.book_name_tv.setText(allBookTags.get(position).getTag_name());
        holder.itemView.setOnClickListener(view -> listener.onItemClick(view, position));
        if(bookHasDate(position)){
            holder.date_tv.setVisibility(View.VISIBLE);
            holder.date_tv.setText(basic_user_data.getEarliestBookDate(allBookTags.get(position).getTag_name()));
        }
    }

    public boolean bookHasDate(int pos){
        if(basic_user_data.getAllExistingBookTags().contains(new BookTag(allBookTags.get(pos).getTag_name()))){
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return allBookTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView book_name_tv,date_tv;
        private MaterialCardView book_item_card;
        public ViewHolder(@NonNull View itemView,final OnItemClickListener listener){
            super(itemView);
            book_item_card = itemView.findViewById(R.id.bookItemCard);
            book_name_tv = itemView.findViewById(R.id.bookRvItemName);
            date_tv = itemView.findViewById(R.id.bookRvItemDate);
        }
    }

    public void addBookTag(BookTag bookTag){
        if(!allBookTags.contains(bookTag)){
            allBookTags.add(0, bookTag);
            notifyItemInserted(0);
        }
    }
    public BookTag getBookByAdapterId(int position){
        return allBookTags.get(position);
    }
}
