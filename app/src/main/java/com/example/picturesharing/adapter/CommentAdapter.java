package com.example.picturesharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.picturesharing.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<String> imagesToDisplay;
    private Context mContext;

    public CommentAdapter(List<String> imagesToDisplay) {
        this.imagesToDisplay = imagesToDisplay;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comments_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvComment;
//        private TextView tv

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
