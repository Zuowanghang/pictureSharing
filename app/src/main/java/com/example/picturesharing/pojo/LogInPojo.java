package com.example.picturesharing.pojo;

import java.math.BigInteger;

public class LogInPojo {

    private String msg;
    private int code;
    private Data data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String appKey;
        private String avatar;
        private BigInteger createTime;
        private BigInteger id;
        private String introduce;
        private BigInteger lastUpdateTime;
        private String password;
        private Integer sex;
        private String username;



        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public BigInteger getCreateTime() {
            return createTime;
        }

        public void setCreateTime(BigInteger createTime) {
            this.createTime = createTime;
        }

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public BigInteger getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(BigInteger lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}

