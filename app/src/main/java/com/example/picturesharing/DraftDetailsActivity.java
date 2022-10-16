package com.example.picturesharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.pojo.SavePictureBean;

import java.util.ArrayList;

public class DraftDetailsActivity extends AppCompatActivity {
    private String message;
    private ArrayList<String> selected;
    private String strTitle;
    private String strContent;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private TextView tv_Title;
    private TextView tv_Content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_details);
        Intent i = getIntent();
        //获取传递的
        selected = i.getStringArrayListExtra("images");
        String[] strs = i.getStringArrayExtra("article");
        tv_Title = findViewById(R.id.draftArticleTitle);
        tv_Content = findViewById(R.id.draft_article_content);

        // 接收到的信息可能为空
        try {
            strTitle = strs[0];
            strContent = strs[1];

            tv_Title.setText(strTitle);
            tv_Content.setText(strContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.draftRlv);
        adapter = new ImageAdapter(DraftDetailsActivity.this);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + selected);
        adapter.setImages(selected);
        recyclerView.setLayoutManager(new GridLayoutManager(DraftDetailsActivity.this, 3));
        recyclerView.setAdapter(adapter);
    }
}