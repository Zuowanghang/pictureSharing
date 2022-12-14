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
import com.donkingliang.imageselector.utils.VersionUtils;
import com.example.picturesharing.R;
import com.example.picturesharing.databinding.FragmentDashboardBinding;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context mContext;
    private View adapterView;
    private ArrayList<String> images;
    private final LayoutInflater mInflater;
    private final boolean isAndroidQ = VersionUtils.isAndroidQ();
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
        this.adapterView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String image = images.get(position);
        System.out.println("这里是设配器"+image);
        boolean isCutImage = ImageUtil.isCutImage(mContext, image);
        if (isAndroidQ && !isCutImage) {
//            System.out.println("我进来了这里我进来了这里我进来了这里我进来了这里我进来了这里我进来了这里我进来了这里我进来了这里");
            Glide.with(this.adapterView).load(image).into(holder.imageView);
        } else {
//            System.out.println("我在那里我在那里我在那里我在那里我在那里我在那里我在那里我在那里我在那里");
            Glide.with(this.adapterView).load(image).into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && images != null) {
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
        void onDelete(int id);
    }

    public void setOnImageDeleteListener(OnImageListener listener) {
        this.listener = listener;
    }
}
