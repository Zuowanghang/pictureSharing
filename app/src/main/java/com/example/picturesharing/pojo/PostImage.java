package com.example.picturesharing.pojo;

public class PostImage {

    private int code;
    private String msg;
    private Data data;



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String imageCode;
        private String[] imageUrlList;


        public String getImageCode() {
            return imageCode;
        }

        public void setImageCode(String imageCode) {
            this.imageCode = imageCode;
        }

        public String[] getImageUrlList() {
            return imageUrlList;
        }

        public void setImageUrlList(String[] imageUrlList) {
            this.imageUrlList = imageUrlList;
        }
    }
}
