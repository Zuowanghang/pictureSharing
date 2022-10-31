package com.example.picturesharing;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.adapter.CommentAdapter;
import com.example.picturesharing.adapter.ImageTitleAdapter;
import com.example.picturesharing.placeholder.PictureMoreBean;
import com.example.picturesharing.placeholder.PlaceholderContent;
import com.example.picturesharing.pojo.Conmment1Bean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ShareDetails extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private Banner banner;
    private OkHttpClient client;
    private PictureMoreBean.Data list;
    private TextView textView;
    private ImageView itemSupported;
    private ImageView itemFav;
    private Button support;
    private RecyclerView recyclerView;

    private EditText editText;
    private Button button;
    private static PictureMoreBean data;

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
        itemSupported = findViewById(R.id.itemSupported);
        itemFav = findViewById(R.id.itemFav);
        support = findViewById(R.id.support2);
        itemFav.setOnClickListener(this);
        support.setOnClickListener(this);
        itemSupported.setOnClickListener(this);

        button = findViewById(R.id.sendComment);
        banner = findViewById(R.id.banner);
        editText = findViewById(R.id.edit_comment);
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
        recyclerView = findViewById(R.id.comment_RecyView);
        getPicture();
        getComment();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment1(editText.getText().toString());
//                postComment(editText.getText().toString());//发表品论

                editText.setText("");
            }
        });


    }


    //  获取图片详情接口
    private void getPicture() {
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
                Log.d("获取轮播图图片", body);
                // 解析json串到自己封装的状态 JSON.parseObject(jsonData, PlaceholderContent.class);

                data = JSON.parseObject(body, PictureMoreBean.class);

                if (data.getCode() == 200) {
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
                        textView.setText("" + data.getData().getLikeNum());
                        if (data.getData().isHasFocus()) {
                            support.setText("已关注");
                        } else {
                            support.setText("+ 关注");
                        }
                        Long timeGetTime = Long.parseLong(data.getData().getCreateTime());//获取事件用户的时间错
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String time = sdf.format(timeGetTime);//显示正确的信息
                        textView = findViewById(R.id.tv_time);
                        textView.setText(time);


//                Log.d("test", timeGetTime +"  现在的时间2-->:" + time2);


                    });
                }


            }
        };

        new Thread(() -> {
            // url路径

            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId=" + UserData.getPictureId() + "&userId=" + UserData.getUserid();

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
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
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    //post同步请求
    public void postComment1(String comment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://47.107.52.7:88/member/photo/comment/first";
                OkHttpClient okHttpClient = new OkHttpClient();
                //post请求规定,把参数放在请求体里面,用form表单的方式来添加我们的请求体
                // 请求体
                // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
                Map<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("shareId", UserData.getPictureId());
                bodyMap.put("userName", UserData.getUserName());
                bodyMap.put("userId", UserData.getUserid());
                bodyMap.put("content", comment);
                // 将Map转换为字符串类型加入请求体中
                String body = JSON.toJSONString(bodyMap);
                MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                //请求组合创建
                Headers headers = new Headers.Builder()
                        .add("Accept", "application/json, text/plain, */*")
                        .add("appId", UserData.appId)
                        .add("appSecret", UserData.appSecret)
                        .add("Content-Type", "application/json")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        // 将请求头加至请求中
                        .headers(headers)
                        .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String body1;
                    Log.i("TAG", "postSync:" + (body1 = response.body().string()));

                    Log.d("发表评论成功", body1);
                    PlaceholderContent data = JSON.parseObject(body1, PlaceholderContent.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (data.getCode() == 200) {
                                Toasty.success(ShareDetails.this, "发布成功!", Toast.LENGTH_SHORT, true).show();
                                getComment();//获取评论
                            } else {
                                Toast.makeText(ShareDetails.this, "发布失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //发表品论
    private void postComment(String comment) {

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
                Log.d("发表评论成功", body);
                PlaceholderContent data = JSON.parseObject(body, PlaceholderContent.class);
                Log.d("发表评论成功2", "" + data.getCode());
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (data.getCode() == 200) {
                            Toasty.success(ShareDetails.this, "发布成功!", Toast.LENGTH_SHORT, true).show();
                        } else {
                            Toast.makeText(ShareDetails.this, "发布失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // 解析json串到自己封装的状态 JSON.parseObject(jsonData, PlaceholderContent.class);


            }
        };

        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/comment/first";

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
            bodyMap.put("shareId", UserData.getPictureId());
            bodyMap.put("userName", UserData.getUserName());
            bodyMap.put("userId", UserData.getUserid());
            bodyMap.put("content", comment);
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

    //获取评论
    private void getComment() {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();

                Log.d("获取评论", body);
                // 解析json串到自己封装的状态 JSON.parseObject(jsonData, PlaceholderContent.class);
                Conmment1Bean data;
                data = JSON.parseObject(body, Conmment1Bean.class);

                if (data.getCode() == 200) {

                    ShareDetails.this.runOnUiThread(() -> {

                        List<Conmment1Bean.Data.Records> list = data.getData().getRecords();
                        recyclerView.setAdapter(new CommentAdapter(list, ShareDetails.this));
                        recyclerView.setLayoutManager(new LinearLayoutManager(ShareDetails.this));
                        //上面是布局管理器，没有就显示不出来。

                    });
                }
            }//处理数据
        };
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/comment/first?shareId=" + UserData.getPictureId();

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
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
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //点击按钮事件
    @Override
    public void onClick(View v) {
        System.out.println("检测data" + JSON.toJSONString(data));
        ShareDetails.this.runOnUiThread(() -> {
            switch (v.getId()) {
                //收藏
                case R.id.itemFav: {
                    textView = findViewById(R.id.FavNum);
                    if (data.getData().isHasCollect()) {

                        System.out.println("这里是取消收藏");
                        getPicture(UserData.getPictureId(), 2);
                        data.getData().setCollectNum(data.getData().getCollectNum() - 1);//设置收藏数减一

                        textView.setText("" + data.getData().getCollectNum());
                        //取消收藏
                        data.getData().setHasCollect(!data.getData().isHasCollect());

                    } else {
                        //收藏
                        System.out.println("这里收藏");
                        goFcous(UserData.getPictureId(), 3);
                        data.getData().setCollectNum((data.getData().getCollectNum() + 1));//设置收藏数加一

                        textView.setText("" + data.getData().getCollectNum());
                        //取消收藏
                        data.getData().setHasCollect(!data.getData().isHasCollect());
                    }
                }
                break;
                //点赞
                case R.id.itemSupported: {
                    textView = findViewById(R.id.supportNum);
                    if (data.getData().isHasLike()) {

                        System.out.println("这里是取消点赞");
                        getPicture(UserData.getPictureId(), 1);
                        data.getData().setLikeNum(data.getData().getLikeNum() - 1);//设置收藏数减一

                        textView.setText("" + data.getData().getLikeNum());
                        //设置取消点赞标志
                        data.getData().setHasLike(!data.getData().isHasLike());

                    } else {
                        //收藏
                        System.out.println("这里点赞");
                        goFcous(UserData.getPictureId(), 4);
                        data.getData().setLikeNum(data.getData().getLikeNum() + 1);//设置收藏数加一

                        textView.setText("" + data.getData().getLikeNum());
                        //取消收藏
                        data.getData().setHasLike(!data.getData().isHasLike());
                    }
                }
                break;
//TODO 关注
                case R.id.support2: {

                    if (data.getData().isHasFocus()) {

                        System.out.println("这里是取消关注");
                        support.setText("+ 关注");
                        goFcous(data.getData().getPUserId(), 2);
                        data.getData().setHasFocus(!data.getData().isHasFocus());

                    } else {
                        System.out.println("这里关注");
                        support.setText("已关注");
                        goFcous(data.getData().getPUserId(), 1);

                        //取消收藏
                        data.getData().setHasFocus(!data.getData().isHasFocus());
                    }
                }
                break;
                default:
                    break;
            }
        });


    }

    private void goFcous(String key, int str) {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                System.out.println("请求失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();

                Log.d("点赞关注收藏处理结果", body);


            }//处理数据
        };

        new Thread(() -> {
            String url = null;
            switch (str) {
                case 1:
                    url = "http://47.107.52.7:88/member/photo/focus?focusUserId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("关注", url);
                    break;
                case 2:
                    url = "http://47.107.52.7:88/member/photo/focus/cancel?focusUserId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("取消关注", url);

                    break;
                case 3:
                    url = "http://47.107.52.7:88/member/photo/collect?shareId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("收藏", url);
                    break;
                case 4:
                    url = "http://47.107.52.7:88/member/photo/like?shareId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("点赞", url);

                    break;
                case 5:
                    url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" + key;
                    System.out.println("取消点赞url处");
                    Log.d("取消点赞", url);

                    break;
                case 6:
                    url = "http://47.107.52.7:88/member/photo/collect/cancel?collectId=" + key;
                    Log.d("取消收藏", url);

                    break;

                default:
                    break;
            }

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();
            //请求组合创建
            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
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
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // getPicture(String shareId,int i) 获取图片详情页面，用来实现点赞取消和收藏取消
    private void getPicture(String shareId, int i) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO 请求失败处理
                System.out.println("获取详情页面失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO 请求成功处理
                // 获取响应体的json串
                System.out.println("获取详情页面成功");
                String body = response.body().string();
                Log.i("获取详情页面", body);

                PictureMoreBean data1;
                data1 = JSON.parseObject(body, PictureMoreBean.class);
                switch (i) {
                    case 1:
                        goFcous(data1.getData().getLikeId(), 5);
                        //取消点赞
                        break;
                    case 2:
                        goFcous(data1.getData().getCollectId(), 6);
                        //取消收藏
                        break;
                    default:
                        break;
                }


                Log.d("点赞人数", "" + data1.getData().getLikeNum());
            }
        };

        new Thread(() -> {

            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId=" + shareId + "&userId=" + UserData.getUserid();

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
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