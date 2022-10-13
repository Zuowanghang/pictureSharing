package com.example.picturesharing.pojo;

public class UserData {


    public static String appId = "9fa35fba78d94dc7a3c0c7da666b00b8";
    public  static String appSecret ="70941057f9dd513fa453585f6ca5832a6db78";
    private static String userid = "1579762298157928448";
    private static String appKey = "9fa35fba78d94dc7a3c0c7da666b00b8";

    public static String getAppKey() {
        return appKey;
    }

    public static void setAppKey(String appKey) {
        UserData.appKey = appKey;
    }

    public  UserData(){


    }
    //TODO 关注
    public static class Focus {
        public  Focus(){}

        private  static String appId = "006d846c73764779a25da7c1c1eae6db";
        private   static String appSecret ="46977249b3cc45fba493aa6d14f99ff77a47e";

        public static String getAppId() {
            return appId;
        }

        public static void setAppId(String appId) {
            Focus.appId = appId;
        }

        public static String getAppSecret() {
            return appSecret;
        }

        public static void setAppSecret(String appSecret) {
            Focus.appSecret = appSecret;
        }
    }

    // TODO 收藏
    public static  class  Collect{
        public  Collect(){}
        private  static String appId = "006d846c73764779a25da7c1c1eae6db";
        private   static String appSecret ="46977249b3cc45fba493aa6d14f99ff77a47e";

        public static String getAppId() {
            return appId;
        }

        public static void setAppId(String appId) {
            Focus.appId = appId;
        }

        public static String getAppSecret() {
            return appSecret;
        }

        public static void setAppSecret(String appSecret) {
            Collect.appSecret = appSecret;
        }

    }

    //TODO 点赞
    public static  class  Like{
        public  Like(){}
        private  static String appId = "006d846c73764779a25da7c1c1eae6db";
        private   static String appSecret ="46977249b3cc45fba493aa6d14f99ff77a47e";

        public static String getAppId() {
            return appId;
        }

        public static void setAppId(String appId) {
            Focus.appId = appId;
        }

        public static String getAppSecret() {
            return appSecret;
        }

        public static void setAppSecret(String appSecret) {
            Like.appSecret = appSecret;
        }

    }

    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        UserData.appId = appId;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public static void setAppSecret(String appSecret) {
        UserData.appSecret = appSecret;
    }

    public static String getUserid() {
        return userid;
    }

    public static void setUserid(String userid) {
        UserData.userid = userid;
    }
}
