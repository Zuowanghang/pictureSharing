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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.R;
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.databinding.FragmentDashboardBinding;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;

    private ArrayList<String> selected;

    private RecyclerView recyclerView;
    private FragmentDashboardBinding binding;
    private ImageAdapter adapter;
    private EditText title;
    private EditText content;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

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
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.article_title);
        content = view.findViewById(R.id.article_content);

        recyclerView = view.findViewById(R.id.rlv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        ImageAdapter adapter = new ImageAdapter(getActivity());
        recyclerView.setAdapter(adapter);
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
            recyclerView.setAdapter(adapter);

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
                break;
            }
            case R.id.draft: {
                saveAsDraft();
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