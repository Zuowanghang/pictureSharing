package com.example.picturesharing.diyview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class SobViewPager extends ViewPager {


    private Handler mHandler;

    public SobViewPager(@NonNull Context context) {
        this(context,null);
        setPageTransformer(true,new PageTransformer() {
            @Override
            public void transformPage(@NonNull View page,float position) {

            }
        });
    }

    public SobViewPager(@NonNull Context context,@Nullable AttributeSet attrs) {
        super(context,attrs);
        mHandler = new Handler(Looper.getMainLooper());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v,MotionEvent event) {
                //不处理事件
                int action = event.getAction();
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        pauseLooper();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        resumeLooper();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.resumeLooper();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.pauseLooper();
    }

    private void resumeLooper() {
        //继续轮播
        mHandler.postDelayed(mTask,1000);
    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            mHandler.postDelayed(this,1000);
        }
    };

    private void pauseLooper() {
        //暂停轮播
        mHandler.removeCallbacks(mTask);
    }
}