package com.example.picturesharing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.UriUtils;
import com.donkingliang.imageselector.utils.VersionUtils;
import com.example.picturesharing.R;
import com.example.picturesharing.databinding.FragmentDashboardBinding;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> images;
    private LayoutInflater mInflater;
    private boolean isAndroidQ = VersionUtils.isAndroidQ();
    private OnImageListener listener = null;

    private FragmentDashboardBinding binding;

    public ImageAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public FragmentDashboardBinding getBinding() {
        return binding;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String image = images.get(position);
        boolean isCutImage = ImageUtil.isCutImage(mContext, image);
        if (isAndroidQ && !isCutImage) {
            Glide.with(mContext).load(UriUtils.getImageContentUri(mContext, image)).into(holder.imageView);
        } else {
            Glide.with(mContext).load(image).into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(position);
                }
            }
        });
    }

    public void refresh(ArrayList<String> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void setImages(ArrayList<String> images) {
        System.out.println(images);
        this.images = images;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    @Override
    public int getItemCount() {
        return images == null? 0 : images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageHolder);
        }
    }

    public interface OnImageListener {
        public void onDelete(int id);
    }

    public void setOnImageDeleteListener(OnImageListener listener) {
        this.listener = listener;
    }
}
