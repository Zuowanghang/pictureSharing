package com.example.picturesharing;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.diyview.SobLooperView;
import com.example.picturesharing.placeholder.PictureMoreBean;
import com.example.picturesharing.pojo.UserData;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PictureActivity extends AppCompatActivity {
 private SobLooperView sobLooperView;
    private View view ;
    private  OkHttpClient client;

    private  String shareId;
    private PictureMoreBean.Data list;

    private String message;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        intent = getIntent();

        message= intent.getStringExtra(ItemFragment.MESSAGE_STRING);



    }



    public void  get(){
        new Thread(() -> {
            // url路径
            String userid = "0";
            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId="+shareId+"&userId=0"+ UserData.getUserid();
            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.getAppId())
                    .add("appSecret", UserData.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();
            Log.i("ssssssssssssss", "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .get()
                    .build();
            try {
                Log.i("ssssssssssssss", "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        //TODO 请求失败处理
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        //TODO 请求成功处理
                        // 获取响应体的json串
                        String jsonData = response.body().string();
                        // 解析json串到自己封装的状态
                        Log.i("ssssssssssssss", "4444444444444444444444444444444444444444444444444444444444444444444444444");
                        PictureMoreBean data;
                        data = JSON.parseObject(jsonData, PictureMoreBean.class);
                        list = data.getData();
//                       Log.i("ssssssssssssss",gson.toJson(list));
                        view.post(new Runnable() {
                            @Override
                            public void run() {

                                Log.i("ssssssssssssss", JSON.toJSONString(list));
                            }
                        });
                    }
                });
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}
