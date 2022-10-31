package com.example.picturesharing.MyToast;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
public class ToastUtils{
    private static String oldMsg;
    protected static Toast toast   = null;
    private static long oneTime=0;
    private static long twoTime=0;

    //显示时间长
    public static void showLong(Context context, String text){
        show(context,text,Toast.LENGTH_LONG);
    }

    //显示时间短
    public static void showShort(Context context, String text){
        show(context,text,Toast.LENGTH_SHORT);
    }

    private static void show(Context mContext,String msg,int mode){
        if(toast==null){
            //这样的话，不管传递什么content进来，都只会引用全局唯一的Content，不会产生内存泄露
            toast =Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
            oneTime=System.currentTimeMillis();
        }else{
            twoTime=System.currentTimeMillis();
            if(msg.equals(oldMsg)){
                if(twoTime-oneTime>mode){
                    toast.show();
                }
            }else{
                oldMsg = msg;
                toast.setText(msg);
                toast.show();
            }
        }
        oneTime=twoTime;

    }

}