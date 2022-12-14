package com.example.picturesharing.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.picturesharing.R;
import com.example.picturesharing.ShareDetails;
import com.example.picturesharing.databinding.FragmentItemBinding;
import com.example.picturesharing.placeholder.PictureMoreBean;
import com.example.picturesharing.placeholder.PlaceholderContent;
import com.example.picturesharing.pojo.UserData;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CollectFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    String appid = UserData.appId;
    String appsecret = UserData.appSecret;
    private String str;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private OkHttpClient client;
    private List<PlaceholderContent.Data.Records> list;
    private View view;
    private ListFragment listFragment;
    private RecyclerView recyclerView;
    private Gson gson;
    public static final String MESSAGE_STRING = "com.glriverside.xgqin.code04.MESSAGE";

    public CollectFragment() {
    }

    @SuppressWarnings("unused")
    public static CollectFragment newInstance(String param1) {
        CollectFragment fragment = new CollectFragment();
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
            String url = "http://47.107.52.7:88/member/photo/collect?userId=" + UserData.getUserid();
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
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                PlaceholderContent data;
                                data = JSON.parseObject(jsonData, PlaceholderContent.class);
                                if (data.getData() != null && data.getCode() == 200) {
                                    list = data.getData().getRecords();
                                    recyclerView.setAdapter(new MyItemRecyclerViewAdapter(list));
                                    Log.i("ssssssssssssss", JSON.toJSONString(list));
                                } else {
                                    Toasty.success(view.getContext(), data.getMsg(), Toast.LENGTH_SHORT, true).show();

                                }
                            }
                        });


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

    //TODO 图片发现页适配器
    public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
        private final Boolean LikeBoolean = false;
        private final Boolean CollectBoolean = false;
        private final Boolean FocusBoolean = false;
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
            int tag = position;
            holder.mItem = mValues.get(position);
            holder.mAccessName.setText(holder.mItem.getUsername());

            holder.mFav.setVisibility(View.GONE);
            holder.mSubscribe.setVisibility(View.GONE);
            {  //关注初始化
//                if (!holder.mItem.isHasFocus())  {
//                    holder.mSubscribe.setImageResource(R.drawable.subscribed);
//                } else {
//                    holder.mSubscribe.setImageResource(R.drawable.subscribe);
//                }
                //点赞初始化
//                if(!holder.mItem.isHasLike()){
//                    holder.mFav.setImageResource(R.drawable.supported);
//                } else {
//                    holder.mFav.setImageResource(R.drawable.support);
//                }
                //收藏初始化

//                if(!holder.mItem.isHasCollect()){
//                    holder.mCollect.setVisibility(View.GONE);
//                } else {
//                    holder.mCollect.setImageResource(R.drawable.collect);
//                }
            }


            view.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Glide.with(view).load(holder.mItem.getImageUrlList()[0]).into(holder.discoverImage);
                    } catch (Exception e) {
                        Glide.with(view).load("https://flashlight.nitecore.cn/Uploads/Album/20181112/original_img/201811121046183873.png").into(holder.discoverImage);
                    }
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
            //TODO 收藏
            if (holder.mItem.isHasCollect()) {
                holder.mCollect.setImageResource(R.drawable.collected);
            } else {
                holder.mCollect.setImageResource(R.drawable.collect);
            }
            //监听点击事件
            holder.mCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    holder.mCardView.setVisibility(View.GONE);
                    System.out.println("这里是取消收藏");
                    AlertDialog alertDialog1 = new AlertDialog.Builder(view.getContext())
                            .setTitle("提示")//标题
                            .setMessage("确定取消收藏")//内容
                            .setIcon(R.drawable.collect)//图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getPicture(holder.mItem.getId(), 2, tag);
                                    //取消收藏
                                    holder.mCollect.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog1.show();

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
            public final CardView mCardView;
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
                mCardView = binding.farther;
            }

        }
    }

    //TODO 取消和关注接口
    private void goFcous(String key, int str, int tag) {
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
                if (str == 6 && data.getCode() == 200) {

                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.success(view.getContext(), "取消收藏成功，请刷新页面查看结果", Toast.LENGTH_SHORT, true).show();
                        }
                    });

                } else {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(view.getContext(), data.getMsg(), Toast.LENGTH_SHORT, true).show();
                        }
                    });
                }
            }
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
                    Log.d("取消点赞", url);

                    break;
                case 6:
                    url = "http://47.107.52.7:88/member/photo/collect/cancel?collectId=" + key;
                    Log.d("取消收藏", url);

                    break;

                default:
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


    private void getPicture(String shareId, int i, int tag) {
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
                switch (i) {
                    case 1:
                        goFcous(data.getData().getLikeId(), 5, tag);
                        //取消点赞
                        break;
                    case 2:
                        goFcous(data.getData().getCollectId(), 6, tag);
                        //取消收藏
                        break;
                    default:
                        break;
                }


                Log.d("点赞人数", "" + data.getData().getLikeNum());
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
}