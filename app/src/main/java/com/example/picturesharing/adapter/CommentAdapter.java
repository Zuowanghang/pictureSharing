package com.example.picturesharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.databinding.FragmentItemBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.picturesharing.R;
import com.example.picturesharing.pojo.Conmment1Bean;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Conmment1Bean.Data.Records> data;
    private Context mContext;

    public CommentAdapter(List<Conmment1Bean.Data.Records> data, Context mContext) {
        this.data = data; System.out.println("品论品论品论品论品论品论品论品论品论品论品论品论品论1");
        System.out.println(JSON.toJSONString(this.data));
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("品论品论品论品论品论品论品论品论品论品论品论品论品论8");

        View v = LayoutInflater.from(mContext).inflate(R.layout.comments_list_item, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.tvComment.setText(data.get(position).getContent());
        holder.tvTime.setText(data.get(position).getCreateTime());
        holder.tvName.setText(data.get(position).getUserName());
        System.out.println(""+data.get(position).getContent()+data.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvComment;
        private TextView tvTime;
//        private Conmment1Bean.Data.Records mVlue;
//        private TextView tv

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            System.out.println("品论品论品论品论品论品论品论品论品论品论品论品论品论1");
            tvName = itemView.findViewById(R.id.commentUserName);
            tvComment = itemView.findViewById(R.id.comment);
            tvTime = itemView.findViewById(R.id.commentReleaseTime);
        }
    }
}
