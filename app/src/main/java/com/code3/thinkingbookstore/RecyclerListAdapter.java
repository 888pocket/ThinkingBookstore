package com.code3.thinkingbookstore;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>{
    public ArrayList<RecyclerListData> listData = new ArrayList<>();
    @NonNull
    @Override
    public RecyclerListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_item, parent, false);
        return new RecyclerListAdapter.ItemViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerListAdapter.ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(RecyclerListData RecyclerListData) {
        listData.add(RecyclerListData);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView contentImageView;

        ItemViewHolder(View itemView) {
            super(itemView);
            contentImageView = itemView.findViewById(R.id.content_book);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void onBind(RecyclerListData listData) {
            contentImageView.setImageResource(listData.getImageView());
            contentImageView.setClipToOutline(true);
        }
    }
}