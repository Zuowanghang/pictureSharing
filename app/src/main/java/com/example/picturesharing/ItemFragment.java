package com.example.picturesharing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.picturesharing.databinding.FragmentItemBinding;
import com.example.picturesharing.placeholder.PictureMoreBean;
import com.example.picturesharing.placeholder.PlaceholderContent;
import com.example.picturesharing.pojo.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ItemFragment extends Fragment {
    private String mLikeId;
    private String mCollectId;
    private SwipeRefreshLayout swipeRefreshLayout;
    String appid = UserData.appId;
    String appsecret = UserData.appSecret;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private OkHttpClient client;
    private List<PlaceholderContent.Data.Records> list;
    private View view;
    private RecyclerView recyclerView;
    private Gson gson;
    public static final String MESSAGE_STRING = "com.glriverside.xgqin.code04.MESSAGE";
    private static int current = 0;

    public ItemFragment() {
    }

    @SuppressWarnings("unused")
    public static ItemFragment newInstance(String param1) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLUMN_COUNT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_item_list, container, false);
        client = new OkHttpClient();
        // Set the adapter
        Context context = view.getContext();
        swipeRefreshLayout = (SwipeRefreshLayout) view;
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        get();


//


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //网络请求
    private void get() {

        new Thread(() -> {
            // url路径
            String userid = "0";
            String url = "http://47.107.52.7:88/member/photo/share?current=1&size=40&userId=" + UserData.getUserid();
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
                        Log.i("发现页", jsonData);
                        PlaceholderContent data;

                        data = JSON.parseObject(jsonData, PlaceholderContent.class);
// TODO 判断获取数据成功不成功
                        System.out.println("发现页"+data.getCode());
                        if (data.getCode() == 200) {
                            list = data.getData().getRecords();
//                       Log.i("ssssssssssssss",JSON.toJSONString(list));
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(new MyItemRecyclerViewAdapter(list));
                                    Log.i("ssssssssssssss", JSON.toJSONString(list));

                                }
                            });
                        } else {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }


                    }
                });

            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();

            }
        }).start();
    }

    /**
     * 回调
     */
    /**
     * http响应体的封装协议
     *
     * @param <T> 泛型
     */
    public static class ResponseBody<T> {

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

    //TODO 图片发现页适配器
    public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
        private Boolean LikeBoolean = false;
        private Boolean CollectBoolean = false;
        private Boolean FocusBoolean = false;
        private final List<PlaceholderContent.Data.Records> mValues;

        public MyItemRecyclerViewAdapter(List<PlaceholderContent.Data.Records> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mName.setText(holder.mItem.getUsername());
            view.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Glide.with(view).load(holder.mItem.getImageUrlList()[0]).into(holder.discoverImage);
                    } catch (Exception e) {
                        Glide.with(view).load("https://flashlight.nitecore.cn/Uploads/Album/20181112/original_img/201811121046183873.png").into(holder.discoverImage);
                    }
 //TODO 图片点击事件
                     holder.discoverImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserData.setPictureUserName(holder.mItem.getUsername());
                            UserData.setPictureId(holder.mItem.getId());
                            UserData.setImageUrlList(holder.mItem.getImageUrlList());
                            Intent intent = new Intent(getContext(), ShareDetails.class);
//                          intent.putExtra( MESSAGE_STRING, message);
                            startActivity(intent);

                        }
                    });

                }
            });
            holder.mTvTitle.setText((holder.mItem.getTitle()));
            //TODO  点赞关注收藏
          //关注初始化
                if (holder.mItem.isHasFocus()) {
                    holder.mSubscribe.setImageResource(R.drawable.subscribed);
                } else {
                    holder.mSubscribe.setImageResource(R.drawable.subscribe);
                }
                //点赞初始化
                if (holder.mItem.isHasLike()) {
                    holder.mFav.setImageResource(R.drawable.supported);
                } else {
                    holder.mFav.setImageResource(R.drawable.support);
                }
                //收藏初始化
                if (holder.mItem.isHasCollect()) {
                    holder.mCollect.setImageResource(R.drawable.collected);
                } else {
                    holder.mCollect.setImageResource(R.drawable.collect);
                }
//TODO 实现关注和取消关注
            holder.mSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mItem.isHasFocus()) {
                        //取消关注
                        goFcous(holder.mItem.getPUserId(), 2);
                        holder.mItem.setHasFocus(!holder.mItem.isHasFocus());
                        holder.mSubscribe.setImageResource(R.drawable.subscribe);
                    } else {
                        //关注
                        goFcous(holder.mItem.getPUserId(), 1);
                        holder.mItem.setHasFocus(!holder.mItem.isHasFocus());
                        holder.mSubscribe.setImageResource(R.drawable.subscribed);
                    }
//                    GoFcous(holder.mItem.getPUserId(),"1579386336664752128");
                }
            });
//TODO 收藏事件
            holder.mCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mItem.isHasCollect()) {

                        System.out.println("这里是取消收藏");
                        getPicture(holder.mItem.getId(),2);
                        //取消收藏
                        holder.mCollect.setImageResource(R.drawable.collect);
                        holder.mItem.setHasCollect(!holder.mItem.isHasCollect());

                    } else {
                        //收藏
                        goFcous(holder.mItem.getId(), 3);
                        holder.mCollect.setImageResource(R.drawable.collected);
                        holder.mItem.setHasCollect(!holder.mItem.isHasCollect());
                    }
                }
            });
 //TODO 实现点赞和取消点赞
            holder.mFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mItem.isHasLike()) {
                        //取消点赞
                         System.out.println("这里是取消点赞");

                        getPicture(holder.mItem.getId(),1);

                        holder.mFav.setImageResource(R.drawable.support);
                        holder.mItem.setHasLike(!holder.mItem.isHasLike());
                    } else {
                        //点赞

                        goFcous(holder.mItem.getId(), 4);
                        holder.mFav.setImageResource(R.drawable.supported);
                        holder.mItem.setHasLike(!holder.mItem.isHasLike());

                    }
//                    GoFcous(holder.mItem.getPUserId(),"1579386336664752128");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView discoverImage;
            public final TextView mTvTitle;
            public final CircleImageView mAccessImage;
            public final TextView mAccessName;
            public final ImageView mCollect;
            public final ImageView mSubscribe;
            public final ImageView mFav;
            public final TextView mName;
            public PlaceholderContent.Data.Records mItem;

            public ViewHolder(FragmentItemBinding binding) {
                super(binding.getRoot());
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.delete.setVisibility(View.GONE);
                    }
                });

                discoverImage = binding.discoverImageItem;
                mTvTitle = binding.tvTitle;
                mAccessImage = binding.accessImg;
                mAccessName = binding.accessName;
                mCollect = binding.collect;
                mSubscribe = binding.subscribe;
                mFav = binding.support;
                mName = binding.accessName;
            }

        }
    }

    //TODO 取消和关注接口
    private void goFcous(String key, int str) {
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
                    url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" + key ;
                    Log.d("取消点赞", url);

                    break;
                case 6:
                    url = "http://47.107.52.7:88/member/photo/collect/cancel?collectId=" + key ;
                    Log.d("取消收藏", url);

                    break;

                default:
            }

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret",UserData.appSecret)
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
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {

                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        System.out.println(response.body().string());

                    }
                });

            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();

            }
        }).start();
    }

// getPicture(String shareId,int i) 获取图片详情页面
    private void getPicture(String shareId,int i){
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
                PictureMoreBean data;
                data = JSON.parseObject(body, PictureMoreBean.class);
                switch (i){
                    case 1:      goFcous(data.getData().getLikeId(), 5);
                    //取消点赞
                        break;
                    case 2:   goFcous(data.getData().getCollectId(), 6);
                    //取消收藏
                    break;
                    default:break;
                }


                Log.d("点赞人数",""+ data.getData().getLikeNum());
            }
        };

        new Thread(() -> {

            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId="+shareId+"&userId="+UserData.getUserid();

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
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }


}