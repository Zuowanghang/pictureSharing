package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.databinding.FragmentDashboardBinding;
import com.example.picturesharing.pojo.PostImage;
import com.example.picturesharing.pojo.ReleaseContent;
import com.example.picturesharing.pojo.SavePictureBean;
import com.example.picturesharing.pojo.UserData;
import com.example.picturesharing.ui.dashboard.DashboardViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DraftDetailsFragment extends Fragment implements View.OnClickListener {
    private Gson gson;
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
    private Button release;
    private String imageCode;
    private SavePictureBean.Data.Records data;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//进来给赋值








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

        release = binding.release;
        release.setOnClickListener(this);

        adapter = new ImageAdapter(getContext());
        adapter.setOnImageDeleteListener(this::removeData);
        recyclerView = root.findViewById(R.id.rlv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter);



        title.setText(UserData.getSavePictureData().getTitle());
        content.setText(UserData.getSavePictureData().getContent());
        String path = "/storage/emulated/0/Pictures/Screenshots/Screenshot_2022-10-16-19-40-07-12_e41039de8eaacf222a951c16e0560c66.jpg";
//        System.out.println("sssssssssssssssssssssssssssssssssssssssssssssssssaaa"+path[0]);
        ArrayList<String> UUrl = new ArrayList<>();

        UUrl.add(path);

//
//

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
        if (selected != null){
            postPicture(1);

            System.out.println("woshi 我是相册的地址ssssssssssssssssssssssssssssssssssssssss"+selected);
        }
    }

    // 发布
    private void release() {

        if (selected != null){
            postPicture(0);

            System.out.println("woshi 我是相册的地址ssssssssssssssssssssssssssssssssssssssss"+selected);
        }







    }


    private void postPicture(int n){  Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            //
            String jsonData = response.body().string();

            Log.d("上传图片除", jsonData);
            // 解析json串到自己封装的状态
            PostImage data;
            data = JSON.parseObject(jsonData, PostImage.class);
            //上传图片分享，包括内容

            if(n == 0){
                post(data.getData().getImageCode());
            }
            else {
                savePost(data.getData().getImageCode());
            }


//            String[] path = data.getData().getImageUrlList();
//            for (int i = 0 ; i < data.getData().getImageUrlList().length;i++){
//                System.out.println(path[i]);
//            }
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
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }
    private void post(String imageCode){

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态

                ResponseBody<Object> dataResponseBody = JSON.parseObject(body,jsonType);
                Log.d("info", dataResponseBody.toString());
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
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", UserData.getUserid());
            bodyMap.put("title", title.getText());
            bodyMap.put("content", content.getText());
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
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }


    //保存图片分享
    private void savePost(String imageCode){

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
                // 获取响应体的json串

                String body = response.body().string();
                Log.d("保存图片分享成功", body);
                // 解析json串到自己封装的状态
                ResponseBody<Object> dataResponseBody = JSON.parseObject(body,jsonType);
                Log.d("保存图片分享成功", dataResponseBody.toString());
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
            bodyMap.put("content", content.getText());
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", UserData.getUserid());
            bodyMap.put("title", title.getText());
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
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */


    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */

    /**
     * 回调
     */




    public static class ResponseBody <T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private T data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

}