package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.pojo.PostImage;
import com.example.picturesharing.pojo.UserData;

import com.example.picturesharing.util.ResponseBody;
import com.google.gson.reflect.TypeToken;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;

public class DraftDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;
    private String message;
    private ArrayList<String> selected;
    private String strTitle;
    private String strContent;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private     AlertDialog dialog;
    private EditText tv_Title;
    private EditText tv_Content;
    private String imageCode;
    private ImageView imageSelector;
    private ImageView archive;
    private ImageView clearBtn;
    private Button releaseBtn;
    private ImageView delete;
    private  String mTitle;
    private  String mContent;
private  String id;
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
        imageCode = i.getStringExtra("imageCode");
        tv_Title = findViewById(R.id.draftArticleTitle);
        tv_Content = findViewById(R.id.draft_article_content);
        id = i.getStringExtra("id");
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

         delete = findViewById(R.id.deleteDraft);
         delete.setOnClickListener(this);
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
        mContent =  tv_Content.getText().toString();
        mTitle = tv_Title.getText().toString();
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
            case R.id.deleteDraft: {

                getDelet(id);
                System.out.println("删除");
                clearData();
                break;
            }
            case R.id.releaseBtn: {
                // 发布按钮
                postSaved();
                getDelet(id);
//               postPicture(0,mTitle,mContent);
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
        getDelet(id);
        postPicture(1,mTitle,mContent);

    }




    /**
     * 将草稿转为发布状态，
     */
    private void changeStateToReleased() {

        postSaved();
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

        //直接发布草稿
    private void postSaved(){

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
                ResponseBody data = JSON.parseObject(body,ResponseBody.class);
                Log.d("草稿相发布", body);
                if(data.getData() != null && data.getCode() == 200){
                    Log.d("草稿相发布成功  ————————》", data.getMsg());
                }


                // 解析json串到自己封装的状态

            }
        };
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/change";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("content", tv_Content.getText().toString());
            bodyMap.put("id", id);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", UserData.getUserid());
            bodyMap.put("title", mTitle);
            // 将Map转换为字符串类型加入请求体中
            String body = JSON.toJSONString(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
            Log.d("草稿相发布请求前", body);
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


    //n==0，直接发布图片到动态；
    //n == 1 ,保存到草稿相
    private void postPicture(int n,String title, String content) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<com.example.picturesharing.util.ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串
                //
                String jsonData = response.body().string();
                Log.d("图片标题内容", jsonData);
                // 解析json串到自己封装的状态
                PostImage data;
                data = JSON.parseObject(jsonData, PostImage.class);
                //上传图片分享，包括内容
                if(data.getCode() == 200){
                    if (n == 0) {
                        post(data.getData().getImageCode(),title,content);
                    } else {
                        savePost(data.getData().getImageCode(),title,content);
                    }
                }else {
                    System.out.println("先上传图片失败");
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

    //上传图片获取图片的imageCode和标题和内容
    private void post(String imageCode ,String title, String content) {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<com.example.picturesharing.util.ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("图片上传", body);
                // 解析json串到自己封装的状态

                com.example.picturesharing.util.ResponseBody<Object> dataResponseBody = JSON.parseObject(body, jsonType);
                Log.d("图片上传", dataResponseBody.toString());
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
            System.out.println("请求里面是否成功"+title+content);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", UserData.getUserid());
            bodyMap.put("title", title);
            bodyMap.put("content", content);
            // 将Map转换为字符串类型加入请求体中
            String body = JSON.toJSONString(bodyMap);
            System.out.println("图片内容"+body);
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
    private void savePost(String imageCode,String title, String content) {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<com.example.picturesharing.util.ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串

                String body = response.body().string();
                Log.d("保存的草稿内容", body);
                // 解析json串到自己封装的状态


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

//删除图片分享
    private void getDelet(String shareId){

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
                Log.d("删除图文", body);

                // 解析json串到自己封装的状态sha

            }
        };
        new Thread(() -> {

            String url = "http://47.107.52.7:88/member/photo/share/delete?shareId="+shareId+"&userId="+UserData.getUserid();
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
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