package com.example.picturesharing.placeholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.picturesharing.R;

public class ImageTitleHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public ImageTitleHolder(@NonNull View view) {
        super(view);
        imageView = view.findViewById(R.id.image);
    }
}
