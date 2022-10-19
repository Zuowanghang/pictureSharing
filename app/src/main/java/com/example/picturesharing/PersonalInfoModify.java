package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.pojo.PostImage;
import com.example.picturesharing.pojo.UserData;
import com.example.picturesharing.pojo.UserInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalInfoModify extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;
    private ImageView cancelEditing;
    private CircleImageView avatar;
    private LinearLayout l1;
    private LinearLayout l2;
    private LinearLayout l3;

    private final UserInfo info = new UserInfo();
    private Button save;
    private TextView name;
    private TextView sex;
    private TextView introduction;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_personal_info);

        cancelEditing = findViewById(R.id.cancelEditing);
        avatar = findViewById(R.id.clickToChange);
        l1 = findViewById(R.id.modify_username);
        l2 = findViewById(R.id.modify_sex);
        l3 = findViewById(R.id.modify_sign);
        save = findViewById(R.id.save_change);

        // 获取权限
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //预加载手机图片。加载图片前，请确保app有读取储存卡权限
            ImageSelector.preload(this);
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
        }

        // 点击事件
        avatar.setOnClickListener(this);
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        save.setOnClickListener(this);

        name = findViewById(R.id.tv_user);
        sex = findViewById(R.id.tv_sex);
        introduction = findViewById(R.id.tv_sign);

        cancelEditing.setOnClickListener(v -> {
            // 如果个人主页不能刷新数据，可以选择在这里将 UserInfo 对象设置出去
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && data != null) {
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
//            Log.d("ImageSelector", "是否是拍照图片：" + isCameraImage);
                for (String str : images) {
                    Glide.with(this).load(str).into(this.avatar);
                    path = str;
                    System.out.println("进来了" + path);
                    info.setImage(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //预加载手机图片
                ImageSelector.preload(this);
            }  //拒绝权限。

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickToChange: {
                try {
                    //单选
                    ImageSelector.builder()
                            .useCamera(true) // 设置是否使用拍照
                            .setSingle(true)  //设置是否单选
                            .canPreview(true) //是否点击放大图片查看,，默认为true
                            .start(this, REQUEST_CODE); // 打开相册
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.modify_username: {
                final EditText editText = new EditText(this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(this);
                inputDialog.setTitle("请输入用户名").setView(editText);
                inputDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            info.setName(editText.getText().toString());
                            name.setText(info.getName());
                        }).show();
                break;
            }

            case R.id.modify_sex: {
                final String[] items = {"男", "女",};
                final int[] yourChoice = {-1};
                AlertDialog.Builder singleChoiceDialog =
                        new AlertDialog.Builder(PersonalInfoModify.this);
                singleChoiceDialog.setTitle("请选择性别");
                // 第二个参数是默认选项，此处设置为0
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        (dialog, which) -> yourChoice[0] = which);
                singleChoiceDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            if (yourChoice[0] != -1) {
                                info.setSex(items[yourChoice[0]]);
//                                    String str = items[yourChoice[0]];
                                sex.setText(items[yourChoice[0]]);
                            }
                        });
                singleChoiceDialog.show();
                break;
            }
            case R.id.modify_sign: {
                final EditText editText = new EditText(this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(this);
                inputDialog.setTitle("请输入个人介绍").setView(editText);
                inputDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            info.setSignature(editText.getText().toString());
                            introduction.setText(info.getSignature());
                        }).show();
                break;
            }
            case R.id.save_change: {
                // 保存资料，在上面已经将页面上的数据都封装到了 UserInfo 对象中
                System.out.println(path + "外面");
                if (path == null) {
                    Toast.makeText(PersonalInfoModify.this, "请选择头像", Toast.LENGTH_SHORT).show();
                } else {
                    postPicture(path, name.getText().toString(), sex.getText().toString(), introduction.getText().toString());
                }


                break;
            }
        }
    }


    private void post(String path, String mName, String mSex, String mIntroduction) {

        new Thread(() -> {
            int n = 0;
            switch (mSex) {
                case "男":
                    n = 0;
                    break;
                case "女":
                    n = 1;
                default:
                    break;
            }
            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/update";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
//            System.out.println("头像地址"+path+"id"+UserData.getUserid()+"个人介绍"+introduction.getText().toString()+"name"+UserData.getUserName());
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("avatar", path);
            bodyMap.put("id", "" + UserData.getUserid());
            bodyMap.put("introduce", "" + mIntroduction);
            bodyMap.put("sex", "" + n);
            bodyMap.put("username", "" + mName);

            // 将Map转换为字符串类型加入请求体中
            String body = JSON.toJSONString(bodyMap);
            System.out.println(body);
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

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
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
            Log.d("info", body);

        }


    };


    private void postPicture(String path, String mName, String mSex, String mIntroduction) {
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

                if (data.getCode() == 200) {
                    String url[] = data.getData().getImageUrlList();
                    post(url[0], mName, mSex, mIntroduction);
                }
                //上传图片分享，包括内容

            }
        };

        new Thread(() -> {


            // url路径
            String url = "http://47.107.52.7:88/member/photo/image/upload";
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType mediaType = MediaType.parse("multipart/form-data");//设置类型，类型为八位字节流
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //对文件进行遍历
            System.out.println(path + "里面");
            String fname = path;
            File tempfile = new File(fname);
            //根据文件的后缀名，获得文件类型
            builder.setType(MultipartBody.FORM)

                    .addFormDataPart( //给Builder添加上传的文件
                            "fileList",  //请求的名字
                            tempfile.getName(), //文件的文字，服务器端用来解析的
                            RequestBody.create(MediaType.parse("multipart/form-data"), tempfile)//创建RequestBody，把上传的文件放入
                    );

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


    public static class ResponseBody<T> {

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

        public ResponseBody() {
        }

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
