package com.example.picturesharing.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.picturesharing.R;
import com.example.picturesharing.placeholder.ImageTitleHolder;
import com.example.picturesharing.pojo.DataBean;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageTitleAdapter extends BannerAdapter<DataBean, ImageTitleHolder> {

    public ImageTitleAdapter(List<DataBean> mDatas) {
        super(mDatas);
    }

    @Override
    public ImageTitleHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ImageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_title, parent, false));
    }

    @Override
    public void onBindView(ImageTitleHolder holder, DataBean data, int position, int size) {
        holder.imageView.setImageResource(data.imageRes);
    }
}
