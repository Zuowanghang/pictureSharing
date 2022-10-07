package com.example.picturesharing.util;

import androidx.annotation.NonNull;

public class ResponseBody <T> {

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