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
        // ??????????????????????????????
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //???????????????
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
        // ?????? Adapter
        banner.setAdapter(new ImageTitleAdapter(DataBean.getTestData3()));
        // ?????????????????????
        banner.setIndicator(new CircleIndicator(this));
        // ?????????????????????????????????
        banner.setIndicatorSelectedColorRes(R.color.personalData);
        // ???????????????
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
//                postComment(editText.getText().toString());//????????????

                editText.setText("");
            }
        });


    }


    //  ????????????????????????
    private void getPicture() {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO ??????????????????
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO ??????????????????

                // ??????????????????json???
                String body = response.body().string();
                Log.d("?????????????????????", body);
                // ??????json??????????????????????????? JSON.parseObject(jsonData, PlaceholderContent.class);

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
                            support.setText("?????????");
                        } else {
                            support.setText("+ ??????");
                        }
                        Long timeGetTime = Long.parseLong(data.getData().getCreateTime());//??????????????????????????????
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String time = sdf.format(timeGetTime);//?????????????????????
                        textView = findViewById(R.id.tv_time);
                        textView.setText(time);


//                Log.d("test", timeGetTime +"  ???????????????2-->:" + time2);


                    });
                }


            }
        };

        new Thread(() -> {
            // url??????

            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId=" + UserData.getPictureId() + "&userId=" + UserData.getUserid();

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .get()
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    //post????????????
    public void postComment1(String comment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://47.107.52.7:88/member/photo/comment/first";
                OkHttpClient okHttpClient = new OkHttpClient();
                //post????????????,??????????????????????????????,???form??????????????????????????????????????????
                // ?????????
                // PS.??????????????????????????????????????????????????????????????????fastjson???????????????json???
                Map<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("shareId", UserData.getPictureId());
                bodyMap.put("userName", UserData.getUserName());
                bodyMap.put("userId", UserData.getUserid());
                bodyMap.put("content", comment);
                // ???Map??????????????????????????????????????????
                String body = JSON.toJSONString(bodyMap);
                MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                //??????????????????
                Headers headers = new Headers.Builder()
                        .add("Accept", "application/json, text/plain, */*")
                        .add("appId", UserData.appId)
                        .add("appSecret", UserData.appSecret)
                        .add("Content-Type", "application/json")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        // ???????????????????????????
                        .headers(headers)
                        .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String body1;
                    Log.i("TAG", "postSync:" + (body1 = response.body().string()));

                    Log.d("??????????????????", body1);
                    PlaceholderContent data = JSON.parseObject(body1, PlaceholderContent.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (data.getCode() == 200) {
                                Toasty.success(ShareDetails.this, "????????????!", Toast.LENGTH_SHORT, true).show();
                                getComment();//????????????
                            } else {
                                Toast.makeText(ShareDetails.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //????????????
    private void postComment(String comment) {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO ??????????????????
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO ??????????????????
                // ??????????????????json???
                String body = response.body().string();
                Log.d("??????????????????", body);
                PlaceholderContent data = JSON.parseObject(body, PlaceholderContent.class);
                Log.d("??????????????????2", "" + data.getCode());
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (data.getCode() == 200) {
                            Toasty.success(ShareDetails.this, "????????????!", Toast.LENGTH_SHORT, true).show();
                        } else {
                            Toast.makeText(ShareDetails.this, "????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // ??????json??????????????????????????? JSON.parseObject(jsonData, PlaceholderContent.class);


            }
        };

        new Thread(() -> {

            // url??????
            String url = "http://47.107.52.7:88/member/photo/comment/first";

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Content-Type", "application/json")
                    .build();

            // ?????????
            // PS.??????????????????????????????????????????????????????????????????fastjson???????????????json???
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("shareId", UserData.getPictureId());
            bodyMap.put("userName", UserData.getUserName());
            bodyMap.put("userId", UserData.getUserid());
            bodyMap.put("content", comment);
            // ???Map??????????????????????????????????????????
            String body = JSON.toJSONString(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //????????????
    private void getComment() {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO ??????????????????
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO ??????????????????
                Type jsonType = new TypeToken<ResponseBody<Object>>() {
                }.getType();
                // ??????????????????json???
                String body = response.body().string();

                Log.d("????????????", body);
                // ??????json??????????????????????????? JSON.parseObject(jsonData, PlaceholderContent.class);
                Conmment1Bean data;
                data = JSON.parseObject(body, Conmment1Bean.class);

                if (data.getCode() == 200) {

                    ShareDetails.this.runOnUiThread(() -> {

                        List<Conmment1Bean.Data.Records> list = data.getData().getRecords();
                        recyclerView.setAdapter(new CommentAdapter(list, ShareDetails.this));
                        recyclerView.setLayoutManager(new LinearLayoutManager(ShareDetails.this));
                        //??????????????????????????????????????????????????????

                    });
                }
            }//????????????
        };
        new Thread(() -> {

            // url??????
            String url = "http://47.107.52.7:88/member/photo/comment/first?shareId=" + UserData.getPictureId();

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .get()
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //??????????????????
    @Override
    public void onClick(View v) {
        System.out.println("??????data" + JSON.toJSONString(data));
        ShareDetails.this.runOnUiThread(() -> {
            switch (v.getId()) {
                //??????
                case R.id.itemFav: {
                    textView = findViewById(R.id.FavNum);
                    if (data.getData().isHasCollect()) {

                        System.out.println("?????????????????????");
                        getPicture(UserData.getPictureId(), 2);
                        data.getData().setCollectNum(data.getData().getCollectNum() - 1);//?????????????????????

                        textView.setText("" + data.getData().getCollectNum());
                        //????????????
                        data.getData().setHasCollect(!data.getData().isHasCollect());

                    } else {
                        //??????
                        System.out.println("????????????");
                        goFcous(UserData.getPictureId(), 3);
                        data.getData().setCollectNum((data.getData().getCollectNum() + 1));//?????????????????????

                        textView.setText("" + data.getData().getCollectNum());
                        //????????????
                        data.getData().setHasCollect(!data.getData().isHasCollect());
                    }
                }
                break;
                //??????
                case R.id.itemSupported: {
                    textView = findViewById(R.id.supportNum);
                    if (data.getData().isHasLike()) {

                        System.out.println("?????????????????????");
                        getPicture(UserData.getPictureId(), 1);
                        data.getData().setLikeNum(data.getData().getLikeNum() - 1);//?????????????????????

                        textView.setText("" + data.getData().getLikeNum());
                        //????????????????????????
                        data.getData().setHasLike(!data.getData().isHasLike());

                    } else {
                        //??????
                        System.out.println("????????????");
                        goFcous(UserData.getPictureId(), 4);
                        data.getData().setLikeNum(data.getData().getLikeNum() + 1);//?????????????????????

                        textView.setText("" + data.getData().getLikeNum());
                        //????????????
                        data.getData().setHasLike(!data.getData().isHasLike());
                    }
                }
                break;
//TODO ??????
                case R.id.support2: {

                    if (data.getData().isHasFocus()) {

                        System.out.println("?????????????????????");
                        support.setText("+ ??????");
                        goFcous(data.getData().getPUserId(), 2);
                        data.getData().setHasFocus(!data.getData().isHasFocus());

                    } else {
                        System.out.println("????????????");
                        support.setText("?????????");
                        goFcous(data.getData().getPUserId(), 1);

                        //????????????
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
                //TODO ??????????????????
                System.out.println("????????????");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO ??????????????????
                Type jsonType = new TypeToken<ResponseBody<Object>>() {
                }.getType();
                // ??????????????????json???
                String body = response.body().string();

                Log.d("??????????????????????????????", body);


            }//????????????
        };

        new Thread(() -> {
            String url = null;
            switch (str) {
                case 1:
                    url = "http://47.107.52.7:88/member/photo/focus?focusUserId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("??????", url);
                    break;
                case 2:
                    url = "http://47.107.52.7:88/member/photo/focus/cancel?focusUserId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("????????????", url);

                    break;
                case 3:
                    url = "http://47.107.52.7:88/member/photo/collect?shareId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("??????", url);
                    break;
                case 4:
                    url = "http://47.107.52.7:88/member/photo/like?shareId=" + key + "&userId=" + UserData.getUserid();
                    Log.d("??????", url);

                    break;
                case 5:
                    url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" + key;
                    System.out.println("????????????url???");
                    Log.d("????????????", url);

                    break;
                case 6:
                    url = "http://47.107.52.7:88/member/photo/collect/cancel?collectId=" + key;
                    Log.d("????????????", url);

                    break;

                default:
                    break;
            }

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();
            //??????????????????
            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {

                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // getPicture(String shareId,int i) ??????????????????????????????????????????????????????????????????
    private void getPicture(String shareId, int i) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO ??????????????????
                System.out.println("????????????????????????");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO ??????????????????
                // ??????????????????json???
                System.out.println("????????????????????????");
                String body = response.body().string();
                Log.i("??????????????????", body);

                PictureMoreBean data1;
                data1 = JSON.parseObject(body, PictureMoreBean.class);
                switch (i) {
                    case 1:
                        goFcous(data1.getData().getLikeId(), 5);
                        //????????????
                        break;
                    case 2:
                        goFcous(data1.getData().getCollectId(), 6);
                        //????????????
                        break;
                    default:
                        break;
                }


                Log.d("????????????", "" + data1.getData().getLikeNum());
            }
        };

        new Thread(() -> {

            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId=" + shareId + "&userId=" + UserData.getUserid();

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .get()
                    .build();
            try {

                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static class ResponseBody<T> {

        /**
         * ???????????????
         */
        private int code;
        /**
         * ??????????????????
         */
        private String msg;
        /**
         * ????????????
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