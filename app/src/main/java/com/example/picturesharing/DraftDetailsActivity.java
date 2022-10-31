package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.MyToast.FileUtils;
import com.example.picturesharing.MyToast.MyApplication;
import com.example.picturesharing.MyToast.ToastUtils;
import com.example.picturesharing.adapter.ImageAdapter;
import com.example.picturesharing.placeholder.PlaceholderContent;
import com.example.picturesharing.pojo.PostImage;
import com.example.picturesharing.pojo.ReleaseContent;
import com.example.picturesharing.pojo.UserData;

import com.example.picturesharing.util.ResponseBody;
import com.google.gson.reflect.TypeToken;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
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
    private ArrayList<String> selected = new ArrayList<>();
    private ArrayList<String> mySelected;
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
    private int mm;
    private LoadingDialog ld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_details);
        Intent i = getIntent();
        //获取传递的
        mySelected = i.getStringArrayListExtra("images");
        String[] strs = i.getStringArrayExtra("article");
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
//        for (int n = 0; n < mySelected.size();n++){
//            Log.i("dddddddddddddddddddddddddddddddddddddddd", mySelected.get(n));
//        }

        selected = saveImgToLocal(MyApplication.getContext(),mySelected);







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
        try {
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
                    android.app.AlertDialog alertDialog1 = new android.app.AlertDialog.Builder(getWindow().getContext())
                            .setTitle("提示")//标题
                            .setMessage("确定清空草稿？")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

//                                    clearData();


                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog1.show();
                    // 点击取消按钮将页面中所有数据全部清除

                    break;
                }
                case R.id.deleteDraft: {
                    AlertDialog alertDialog1 = new AlertDialog.Builder(getWindow().getContext())
                            .setTitle("提示")//标题
                            .setMessage("确定取消删除该草稿")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getDelet(id);

                                    clearData();
                                    System.out.println("删除");
                                    //取消收藏

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog1.show();

                    break;
                }
                case R.id.releaseBtn: {
                    // 发布按钮
                    if (isOk()) {

                        release();
                        getDelet(id);

                    } else {
                        Toast.makeText(MyApplication.getContext(), "信息项不能为空，请填写信息再发布", Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
                case R.id.archiveImage: {
                    // 将修改过的或者没有修改的图文分享草稿再次存为草稿
                    if (isOk()) {
                        saveAsDraft();
                        getDelet(id);
                        clearData();
                        ReleaseContent.savedData = null;
                    } else {
                        Toast.makeText(MyApplication.getContext(), "信息项不能为空，请填写信息再保存", Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
                default: break;
            }
        }catch (Exception e){
            System.out.println("出错了");
        }


    }
    private boolean isOk() {

        if ( !tv_Title.getText().toString().equals("") && !tv_Content.getText().toString().equals("") && selected != null) {
            return true;
        } else return false;
    }
    // 再次存档
    private void archiveAgain() {
        getDelet(id);
        postPicture(1,mTitle,mContent);

    }
    private void saveAsDraft() {
        System.out.println("保存点击事件");
        System.out.println(tv_Title.getText().toString());
        ld = new LoadingDialog(this);
        ld.setLoadingText("正在保存，请耐心等待。")
                .setSuccessText("保存成功")//显示加载成功时的文字
                .setInterceptBack(true)
                .show();
        postPicture(1, tv_Title.getText().toString(), tv_Title.getText().toString());


    }

    private void release() {

        ld = new LoadingDialog(getWindow().getContext());
        ld.setLoadingText("正在上传，请耐心等待。")
                .setSuccessText("上传成功")//显示加载成功时的文字
                .setInterceptBack(true)
                .show();
        postPicture(0, tv_Title.getText().toString(), tv_Content.getText().toString());



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
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("图片上传", body);
                // 解析json串到自己封装的状态
// TODO 判断获取数据成功不成功
                PlaceholderContent data;
                data = JSON.parseObject(body, PlaceholderContent.class);
                System.out.println("图片上传"+data.getCode());
                Log.d("图片上传", data.toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        if(data.getCode() == 200 ){
                            Toasty.success(MyApplication.getContext(), "图片上传成功!", Toast.LENGTH_SHORT, true).show();
                        }else {
                            Toasty.error(MyApplication.getContext(), "图片上传失败!", Toast.LENGTH_SHORT, true).show();
                        }
                        Looper.loop();
                    }
                }).start();



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
            System.out.println("id = " + id);
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


    //上传图片获取图片的imageCode和标题和内容


    //保存图片分享

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

    private ArrayList<String> saveImgToLocal(Context context, ArrayList<String> url) {
         ArrayList<String> selected2 = new ArrayList<>();
          mm = 0;
        Log.i("图文件",""+url.size());
        //如果是网络图片，抠图的结果，需要先保存到本地
        for (int i = 0; i < url.size(); i++){
            mm = i;
            Log.i("TAG", i+"   saveImgToLocal: ="+url.get(i));
            System.out.println("" + i+""+i  );
            Glide.with(context)
                    .downloadOnly()
                    .load(url.get(i))
                    .listener(new RequestListener<File>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                            selected2.add(saveToAlbum(context, resource.getAbsolutePath(), mm));
                            Log.i("图片下载成功，查看更改后的地址", saveToAlbum(context, resource.getAbsolutePath(),mm));
                            return false;
                        }
                    })
                    .preload();
        }
            return selected2;

    }

    private String saveToAlbum(Context context, String srcPath,int i) {
        File oldFile =  new File(srcPath);
        int n = srcPath.lastIndexOf(".");
        String str1 = srcPath.substring(0,n);
        str1 = str1+".jpg";
        File newFile = new File(str1);
        oldFile.renameTo(newFile);
            Log.i(i+"更改时候成功的地址", "saveToAlbum: "+newFile.getAbsolutePath());
            return  newFile.getAbsolutePath();

    }















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
                System.out.println();
                //上传图片分享，包括内容
                if(data.getCode() == 200){
                    if (n == 0) {
                        post(data.getData().getImageCode(), title, content);
                    } else {
                        savePost(data.getData().getImageCode(), title, content);
                    }
                }else {
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