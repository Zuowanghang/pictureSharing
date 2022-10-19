package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.adapter.ImageAdapter;

import java.util.ArrayList;

public class DraftDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;
    private String message;
    private ArrayList<String> selected;
    private String strTitle;
    private String strContent;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private TextView tv_Title;
    private TextView tv_Content;

    private ImageView imageSelector;
    private ImageView archive;
    private ImageView clearBtn;
    private Button releaseBtn;

    // 返回上一级的图片按钮
    private ImageView backToPersonal;

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

        imageSelector = findViewById(R.id.draftImageSelector);
        backToPersonal = findViewById(R.id.backToDraft);
        archive = findViewById(R.id.archiveImage);
        releaseBtn = findViewById(R.id.releaseBtn);
        clearBtn = findViewById(R.id.clearImage);

        backToPersonal.setOnClickListener(this);
        imageSelector.setOnClickListener(this);
        archive.setOnClickListener(this);
        releaseBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        recyclerView = findViewById(R.id.draftRlv);
        adapter = new ImageAdapter(DraftDetailsActivity.this);
        adapter.setOnImageDeleteListener(this::removeData);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + selected);
        adapter.setImages(selected);
        recyclerView.setLayoutManager(new GridLayoutManager(DraftDetailsActivity.this, 3));
        recyclerView.setAdapter(adapter);

        // 获取权限
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(DraftDetailsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //预加载手机图片。加载图片前，请确保app有读取储存卡权限
            ImageSelector.preload(DraftDetailsActivity.this);
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(DraftDetailsActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
        }
    }

    /**
     * 处理权限申请的回调函数
     *
     * @param requestCode  申请码
     * @param permissions  权限名，可多个
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //预加载手机图片
                ImageSelector.preload(DraftDetailsActivity.this);
            } else {
                //拒绝权限。
                System.out.println("拒绝赋权");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(data);

        if (requestCode == REQUEST_CODE && data != null) {
            if (selected == null) {
                selected = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            } else {
                selected.addAll(data.getStringArrayListExtra(ImageSelector.SELECT_RESULT));
            }

            boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
//            Log.d("ImageSelector", "是否是拍照图片：" + isCameraImage);
            adapter = new ImageAdapter(DraftDetailsActivity.this);
            adapter.setImages(selected);
            adapter.setOnImageDeleteListener(this::removeData);
            recyclerView.setAdapter(adapter);

            // 在Fragment中需要将数据传入静态类是因为点击底部导航栏重建生命周期，页面上的数据会丢失。
        } else {
            System.out.println(data);
        }
    }

    /**
     * 给 Adapter 添加点击事件，点击图片将其从列表中删除
     * @param id
     */
    private void removeData(int id) {
        selected.remove(id);
        adapter.refresh(selected);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToDraft: {
                finish();
                break;
            }
            case R.id.draftImageSelector: {
                ImageSelector.builder()
                        .useCamera(true) // 设置是否使用拍照
                        .setSingle(false)  //设置是否单选
                        .canPreview(true) //是否点击放大图片查看,，默认为true
                        .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
                        .start(this, REQUEST_CODE); // 打开相册
                break;
            }
            case R.id.clearImage: {
                // 点击取消按钮将页面中所有数据全部清除
                clearData();
                break;
            }
            case R.id.releaseBtn: {
                // 发布按钮
                changeStateToReleased();
                clearData();
                break;
            }
            case R.id.archiveImage: {
                // 将修改过的或者没有修改的图文分享草稿再次存为草稿
                archiveAgain();
                clearData();
                break;
            }
            default: break;
        }
    }

    // 再次存档
    private void archiveAgain() {
    }

    /**
     * 将草稿转为发布状态，
     */
    private void changeStateToReleased() {
    }

    /**
     * 清除页面中所有用户输入数据
     */
    private void clearData() {
        tv_Title.setText("");
        tv_Content.setText("");
        selected.clear();
        adapter.refresh(selected);
    }
}