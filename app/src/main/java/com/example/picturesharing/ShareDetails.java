package com.example.picturesharing;

import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.adapter.ImageTitleAdapter;
import com.example.picturesharing.placeholder.PictureMoreBean;
import com.example.picturesharing.pojo.DataBean;
import com.example.picturesharing.pojo.User;
import com.example.picturesharing.pojo.UserData;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.util.BannerUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ShareDetails extends AppCompatActivity {
    private ImageView back;
    private Banner banner;
    private OkHttpClient client;
    private PictureMoreBean.Data list;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_details);
        // 将状态栏颜色设为透明
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //设置点赞数
//        textView = findViewById(R.id.FavNum);
//        textView.setText("" + UserData.getCollectNum());
//
//        textView = findViewById(R.id.supportNum);
//        textView.setText(""+UserData.getLikeNum());
        back = findViewById(R.id.exitDetail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        banner = findViewById(R.id.banner);
        // 设置 Adapter
        banner.setAdapter(new ImageTitleAdapter(DataBean.getTestData3()));
        // 图片轮播指示器
        banner.setIndicator(new CircleIndicator(this));
        // 设置指示器选中后的颜色
        banner.setIndicatorSelectedColorRes(R.color.personalData);
        // 指示器位置
        banner.setIndicatorGravity(IndicatorConfig.Direction.CENTER);
        banner.setIndicatorMargins(new IndicatorConfig.Margins(0, 0,
                BannerConfig.INDICATOR_MARGIN, (int) BannerUtils.dp2px(12)));
        banner.addBannerLifecycleObserver(this);






get();










    }

    private void get(){
        new Thread(() -> {
            System.out.println("sssssssssssssssssssssssssssssssssssssssssssssss"+UserData.getPictureId());
            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId="+UserData.getPictureId()+"&userId="+UserData.getUserid();

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "006d846c73764779a25da7c1c1eae6db")
                    .add("appSecret", "46977249b3cc45fba493aa6d14f99ff77a47e")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .get()
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
    private final Callback callback = new Callback() {
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
            // 解析json串到自己封装的状态 JSON.parseObject(jsonData, PlaceholderContent.class);
            PictureMoreBean data;
            data = JSON.parseObject(body, PictureMoreBean.class);


            ShareDetails.this.runOnUiThread(() -> {
                textView = findViewById(R.id.tv_content);
              textView.setText(data.getData().getContent());
                textView = findViewById(R.id.tv_title);
                textView.setText(data.getData().getTitle());
                textView = findViewById(R.id.userName);
                textView.setText(UserData.getPictureUserName());
                textView = findViewById(R.id.FavNum);
                textView.setText("" + data.getData().getCollectNum());
                textView = findViewById(R.id.supportNum);
                textView.setText(""+data.getData().getLikeNum());
                Long timeGetTime = Long.parseLong(data.getData().getCreateTime());
                Log.d("test", "  当前时间戳2->:"+timeGetTime);

                SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


                String time2 = sdf.format(timeGetTime);
                textView = findViewById(R.id.tv_time);
                textView.setText(time2);
//                Log.d("test", timeGetTime +"  现在的时间2-->:" + time2);


            });

        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
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