package com.example.picturesharing.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.R;
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.databinding.FragmentDashboardBinding;
import com.example.picturesharing.pojo.ReleaseContent;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;
    private ArrayList<String> selected;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;
    private FragmentDashboardBinding binding;
    private EditText title;
    private EditText content;
    private ImageView draft;
    private ImageView cancel;
    private ImageView imageSelector;
    private ReleaseContent releaseContent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        System.out.println("On Create DashBoard");
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        title = root.findViewById(R.id.article_title);
        content = root.findViewById(R.id.article_content);
        imageSelector = root.findViewById(R.id.imageSelector);
        imageSelector.setOnClickListener(this);

        draft = root.findViewById(R.id.draft);
        draft.setOnClickListener(this);

        cancel = root.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        adapter = new ImageAdapter(getContext());
        adapter.setOnImageDeleteListener(this::removeData);
        recyclerView = root.findViewById(R.id.rlv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter);

        // 获取权限
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //预加载手机图片。加载图片前，请确保app有读取储存卡权限
            ImageSelector.preload(requireActivity());
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        System.out.println("On View Created");
        super.onViewCreated(view, savedInstanceState);
        System.out.println("On View Created");

        if (releaseContent != null) {
            selected = releaseContent.getImages();
            title.setText(releaseContent.getTitle());
            content.setText(releaseContent.getContent());

            adapter = new ImageAdapter(getContext());
            adapter.setOnImageDeleteListener(this::removeData);
            recyclerView = view.findViewById(R.id.rlv);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.setAdapter(adapter);
        } else {
            System.out.println("Test failed");
        }
    }

    private void removeData(int id) {
        selected.remove(id);
        adapter.refresh(selected);
    }

    /**
     * 处理权限申请的回调函数
     * @param requestCode 申请码
     * @param permissions 权限名，可多个
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //预加载手机图片
                ImageSelector.preload(requireContext());
            } else {
                //拒绝权限。
                System.out.println("拒绝赋权");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(ReleaseContent.savedData);
        releaseContent = JSON.parseObject(ReleaseContent.savedData, ReleaseContent.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(data);

        if (requestCode == REQUEST_CODE && data != null) {
            if (selected == null ) {
                selected = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            } else {
                selected.addAll(data.getStringArrayListExtra(ImageSelector.SELECT_RESULT));
            }

            boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
//            Log.d("ImageSelector", "是否是拍照图片：" + isCameraImage);
            adapter = new ImageAdapter(getActivity());
            adapter.setImages(selected);
            adapter.setOnImageDeleteListener(this::removeData);
            recyclerView.setAdapter(adapter);


            // 将现有数据存入savedData
            ReleaseContent info = new ReleaseContent();
            info.setImages(selected);
            info.setTitle(title.getText().toString());
            info.setContent(content.getText().toString());
            ReleaseContent.savedData = JSON.toJSONString(info);
            System.out.println(JSON.toJSONString(info));
        } else {
            System.out.println(data);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageSelector: {
                ImageSelector.builder()
                        .useCamera(true) // 设置是否使用拍照
                        .setSingle(false)  //设置是否单选
                        .canPreview(true) //是否点击放大图片查看,，默认为true
                        .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
                        .start(this, REQUEST_CODE); // 打开相册
                break;
            }
            case R.id.cancel: {
                title.setText("");
                content.setText("");
                selected.clear();
                break;
            }
            case R.id.release: {
                release();
                ReleaseContent.savedData = null;
                break;
            }
            case R.id.draft: {
                saveAsDraft();
                ReleaseContent.savedData = null;
                break;
            }
        }
    }

    // 存为草稿
    private void saveAsDraft() {

    }

    // 发布
    private void release() {

    }
}