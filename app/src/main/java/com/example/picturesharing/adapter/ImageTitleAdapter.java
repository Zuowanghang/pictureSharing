package com.example.picturesharing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.picturesharing.R;
import com.example.picturesharing.placeholder.ImageTitleHolder;
import com.example.picturesharing.pojo.DataBean;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageTitleAdapter extends BannerAdapter<DataBean, ImageTitleHolder> {
    private View view;

    public ImageTitleAdapter(List<DataBean> mDatas) {
        super(mDatas);
    }

    @Override
    public ImageTitleHolder onCreateHolder(ViewGroup parent, int viewType) {
        this.view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_title, parent, false);
        return new ImageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_title, parent, false));
    }

    @Override
    public void onBindView(ImageTitleHolder holder, DataBean data, int position, int size) {
        Glide.with(view).load(data.imageUrl).into(holder.imageView);

//        holder.imageView.setImageResource(data.imageUrl);
    }
}
