package com.example.picturesharing.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.example.picturesharing.DraftDetailsActivity;
import com.example.picturesharing.MainActivity;
import com.example.picturesharing.MyToast.MyApplication;
import com.example.picturesharing.R;
import com.example.picturesharing.ShareDetails;
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.databinding.FragmentDashboardBinding;
import com.example.picturesharing.placeholder.PlaceholderContent;
import com.example.picturesharing.pojo.PostImage;
import com.example.picturesharing.pojo.ReleaseContent;
import com.example.picturesharing.pojo.UserData;
import com.example.picturesharing.util.ResponseBody;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardFragment extends Fragment implements View.OnClickListener {
    private Gson gson;
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;

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
    //    private Button release;
    private FloatingActionButton releaseBtn;
    private Button tag1;
    private Boolean state1 = true;
    private Boolean state2 = true;
    private Boolean state3 = true;
    private Boolean state4 = true;
    private Button tag2;
    private Button tag3;
    private Button tag4;
    private Button release;
    private String imageCode;
    private LoadingDialog ld;
    private String str;


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

        releaseBtn = binding.fab;
        releaseBtn.setOnClickListener(this);

        tag1 = binding.original;
        tag2 = binding.nonOriginal;
        tag3 = binding.nonCopy;
        tag4 = binding.nonCommerce;

        tag1.setOnClickListener(this);
        tag2.setOnClickListener(this);
        tag3.setOnClickListener(this);
        tag4.setOnClickListener(this);

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

            adapter = new ImageAdapter(requireContext());
            adapter.refresh(selected);
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
        // 本意是更新其中数据，但是实际运行过程中报错
//        releaseContent.setImages(selected);
        ReleaseContent.savedData = "";
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
            if (selected == null) {
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

    /**
     * 无论是存为草稿还是发布、又或者是取消编辑，都需要将页面内数据清除
     *
     * @param v 视图
     */
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
                // 需要提示用户“是否确认删除当前输入内容”
                clearData();
                break;
            }
            case R.id.fab: {
                if (isOk()) {

                    release();


                    ReleaseContent.savedData = null;
                } else {
                    Toast.makeText(MyApplication.getContext(), "信息项不能为空，请填写信息再发布", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.draft: {
                if (isOk()) {
                    saveAsDraft();
                    clearData();
                    ReleaseContent.savedData = null;
                } else {
                    Toast.makeText(MyApplication.getContext(), "信息项不能为空，请填写信息再保存", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.original: {
                // 改变按钮样式
                changeState(tag1, state1);
                state1 = !state1;
                break;
            }
            case R.id.non_original: {
                changeState(tag2, state2);
                state2 = !state2;
                break;
            }
            case R.id.non_copy: {
                changeState(tag3, state3);
                state3 = !state3;
                break;
            }
            case R.id.non_commerce: {
                changeState(tag4, state4);
                state4 = !state4;
                break;
            }
        }
    }

    private void changeState(Button tag, Boolean state) {
        if (state) {
            tag.setBackground(getResources().getDrawable(R.drawable.tag_selected));
            tag.setTextColor(getResources().getColor(R.color.white));
        } else {
            tag.setBackground(getResources().getDrawable(R.drawable.button_tag));
            tag.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ReleaseContent info = new ReleaseContent();
        info.setImages(selected);
        info.setTitle(title.getText().toString());
        info.setContent(content.getText().toString());
        ReleaseContent.savedData = JSON.toJSONString(info);
        System.out.println(JSON.toJSONString(info));
        System.out.println("On Pause");
    }

    // 清空内容
    private void clearData() {
        ReleaseContent.savedData = null;
        releaseContent = null;
        title.setText("");
        content.setText("");
        try {
            selected.clear();
            adapter = new ImageAdapter(requireContext());
            adapter.refresh(selected);
            adapter.setOnImageDeleteListener(this::removeData);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 存为草稿
    private void saveAsDraft() {
        System.out.println("保存点击事件");
        System.out.println(title.getText().toString());
        ld = new LoadingDialog(getActivity());
        ld.setLoadingText("正在上传，请耐心等待。")
                .setSuccessText("保存成功")//显示加载成功时的文字
                .setInterceptBack(true)
                .show();
        postPicture(1, title.getText().toString(), content.getText().toString());


        adapter.refresh(selected);
    }

    // 发布
    private void release() {

        ld = new LoadingDialog(getActivity());
        ld.setLoadingText("正在上传，请耐心等待。")
                .setSuccessText("上传成功")//显示加载成功时的文字
                .setInterceptBack(true)
                .show();
        postPicture(0, title.getText().toString(), content.getText().toString());


    }

    private boolean isOk() {

        if (!title.getText().toString().equals("") && !content.getText().toString().equals("") && selected != null) {
            return true;
        } else return false;
    }


    //n==0，直接发布图片到动态；
    //n == 1 ,保存到草稿相
    private void postPicture(int n, String title, String content) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理

                // 获取响应体的json串
                //
                String jsonData = response.body().string();
                Log.d("图片标题内容", jsonData);
                // 解析json串到自己封装的状态
                PostImage data;
                data = JSON.parseObject(jsonData, PostImage.class);
                //上传图片分享，包括内容
                if (data.getCode() == 200) {
                    if (n == 0) {
                        post(data.getData().getImageCode(), title, content);
                    } else {
                        savePost(data.getData().getImageCode(), title, content);
                    }
                } else {
                    imageSelector.post(new Runnable() {
                        @Override
                        public void run() {
                            ld.setFailedText("图片超过上传文件大小");
                            ld.loadFailed();
                        }
                    });
                }
            }
        };

        new Thread(() -> {
            List<String> filePaths = selected;
            // url路径
            String url = "http://47.107.52.7:88/member/photo/image/upload";
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType mediaType = MediaType.parse("multipart/form-data");//设置类型，类型为八位字节流
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; i < filePaths.size(); i++) { //对文件进行遍历
                String fname = filePaths.get(i);
                File tempfile = new File(fname);
                //根据文件的后缀名，获得文件类型
                builder.setType(MultipartBody.FORM)

                        .addFormDataPart( //给Builder添加上传的文件
                                "fileList",  //请求的名字
                                tempfile.getName(), //文件的文字，服务器端用来解析的
                                RequestBody.create(MediaType.parse("multipart/form-data"), tempfile)//创建RequestBody，把上传的文件放入
                        );
            }
            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .headers(headers)
                    .url(url)
                    .post(requestBody)
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    //上传图片获取图片的imageCode （新增图片分享）
    private void post(String imageCode, String title, String content) {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理

                // 获取响应体的json串
                String body = response.body().string();
                Log.d("图片上传", body);
                // 解析json串到自己封装的状态

// TODO 判断获取数据成功不成功
                PlaceholderContent data;
                data = JSON.parseObject(body, PlaceholderContent.class);
                System.out.println("图片上传" + data.getCode());
                Log.d("图片上传", data.toString());
                imageSelector.post(new Runnable() {
                    @Override
                    public void run() {
                        if (data.getCode() == 200) {
                            ld.loadSuccess();
                            clearData();
                        } else {
                            ld.setFailedText("上传失败");
                            ld.loadFailed();
                        }
                    }
                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//
//                        Looper.loop();
//                    }
//                }).start();


            }
        };

        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/add";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Content-Type", "application/json")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            System.out.println("请求里面是否成功" + title + content);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", UserData.getUserid());
            bodyMap.put("title", title);
            bodyMap.put("content", content);
            // 将Map转换为字符串类型加入请求体中
            String body = JSON.toJSONString(bodyMap);
            Log.i("ttttttttttttttttttttttttttttttttttttttttttttttttttttt", body);
            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //保存图片分享
    private void savePost(String imageCode, String title, String content) {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理

                String body = response.body().string();
                Log.d("保存的草稿内容", body);
                PlaceholderContent data;
                data = JSON.parseObject(body, PlaceholderContent.class);
                imageSelector.post(new Runnable() {
                    @Override
                    public void run() {
                        if (data.getCode() == 200) {
                            ld.loadSuccess();

                        } else {
                            ld.setFailedText("保存失败,请图片大小超过承受范围或网络有问题");
                            ld.loadFailed();
                        }
                    }
                });


            }
        };

        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/save";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("content", content);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", UserData.getUserid());
            bodyMap.put("title", title);
            // 将Map转换为字符串类型加入请求体中
            String body = JSON.toJSONString(bodyMap);
            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


}