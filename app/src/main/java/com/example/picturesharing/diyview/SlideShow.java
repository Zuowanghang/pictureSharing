package com.example.picturesharing.diyview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SlideShow {
    private SobLooperView mLooperView;
    private List<LooperItem> mData;
    private String [] imageUrlList;
    private  View view;

    public SlideShow(SobLooperView mLooperView,View view, String [] imageUrlList) {
        this.view = view;
        this.mLooperView = mLooperView;
        this.imageUrlList = imageUrlList;

        initTestData();
        initView();
    }



    private void initView() {
        mLooperView.setData(new SobLooperView.InnerPageAdapter() {
            @Override
            public int getDataSize() {
                return mData.size();
            }

            @Override
            protected View getItemView(ViewGroup container, int itemPosition) {
                ImageView imageView = new ImageView(container.getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                //设置图片
                view.post(new Runnable() {
                    @Override
                    public void run() {

                        Glide.with(view).load(mData.get(itemPosition).getTitle()).into(imageView);
                    }
                });

//                imageView.setImageResource(mData.get(itemPosition).getImgRsId());
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(layoutParams);
                return imageView;
            }
        },new SobLooperView.TitleBindListener() {
            @Override
            public String getTitle(int position) {
                return mData.get(position).getTitle();
            }
        });
    }

    private void initTestData() {
        mData = new ArrayList<>();
        for(int i = 0; i < imageUrlList.length; i++){
            mData.add(new LooperItem(imageUrlList[i], 0));
            Log.d("这里是路边宁波",imageUrlList[i]+"\n");

        }

    }



    public  class LooperItem{

        private int imgRsId;
        private String title;

        public LooperItem(String title, int imgRsId) {
            this.imgRsId = imgRsId;
            this.title = title;
        }

        public int getImgRsId() {
            return imgRsId;
        }

        public void setImgRsId(int imgRsId) {
            this.imgRsId = imgRsId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
