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


public class MyPostPictureFragment extends Fragment {
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
    public static final String MESSAGE_STRING = "com." +
            "glriverside.xgqin.code04.MESSAGE";
    private Gson gson;

    public MyPostPictureFragment() {
    }

    @SuppressWarnings("unused")
    public static MyPostPictureFragment newInstance(String param1) {
        MyPostPictureFragment fragment = new MyPostPictureFragment();
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

    //????????????
    private void get() {

        new Thread(() -> {
            // url??????
            String url = "http://47.107.52.7:88/member/photo/share/myself?userId=" + UserData.getUserid();
            // ?????????
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.getAppId())
                    .add("appSecret", UserData.getAppSecret())
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
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        //TODO ??????????????????
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        //TODO ??????????????????
                        // ??????????????????json???

                        String jsonData = response.body().string();
                        Log.i("????????????", jsonData);
                        // ??????json???????????????????????????
                        PlaceholderContent data;
                        data = JSON.parseObject(jsonData, PlaceholderContent.class);
                        if (data.getData() != null) {
                            list = data.getData().getRecords();
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(new MyItemRecyclerViewAdapter(list));
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
     * ??????
     */
    /**
     * http????????????????????????
     *
     * @param <T> ??????
     */
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

    //TODO ????????????????????????
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
            holder.mAccessName.setText(holder.mItem.getUsername());
            holder.mCollect.setVisibility(View.GONE);
            holder.mFav.setVisibility(View.GONE);
            holder.mSubscribe.setVisibility(View.GONE);


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
            //TODO  ??????????????????
            //TODO ??????

            holder.mDele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    holder.mCardView.setVisibility(View.GONE);
                    System.out.println("?????????????????????");
                    AlertDialog alertDialog1 = new AlertDialog.Builder(view.getContext())
                            .setTitle("??????")//??????
                            .setMessage("??????????????????")//??????
                            .setIcon(R.mipmap.ic_launcher)//??????
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        getDelet(holder.mItem.getId());
                                    //????????????
                                    holder.mDele.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog1.show();

                }
            });
            //??????????????????


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
            private final ImageView mDele;
            public PlaceholderContent.Data.Records mItem;

            public ViewHolder(FragmentItemBinding binding) {
                super(binding.getRoot());
                mDele = binding.delete;
                discoverImage = binding.discoverImageItem;
                mTvTitle = binding.tvTitle;
                mAccessImage = binding.accessImg;
                mAccessName = binding.accessName;
                mCollect = binding.collect;
                mSubscribe = binding.subscribe;
                mFav = binding.support;
            }

        }
    }



    private void getDelet(String shareId){

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
                String body;
                System.out.println(body = response.body().string());

                PictureMoreBean data;
                data = JSON.parseObject(body, PictureMoreBean.class);
                if (data.getCode() == 200) {

                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.success(view.getContext(), "????????????????????????????????????????????????", Toast.LENGTH_SHORT, true).show();
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

                // ??????json???????????????????????????sha

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
            //??????????????????
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
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }
    //TODO ?????????????????????
}