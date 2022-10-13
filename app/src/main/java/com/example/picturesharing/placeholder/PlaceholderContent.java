package com.example.picturesharing.placeholder;

import java.util.List;

public class PlaceholderContent {

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
        private List<Records> records;
        private int total;
        private int size;
        private int current;



        public List<Records> getRecords() {
            return records;
        }

        public void setRecords(List<Records> records) {
            this.records = records;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public static class Records {
            private String id;
            private String pUserId;
            private String imageCode;
            private String title;
            private String content;
            private String createTime;
            private String [] imageUrlList;
            private Object likeId;
            private int likeNum;
            private boolean hasLike;
            private Object collectId;
            private int collectNum;
            private boolean hasCollect;
            private boolean hasFocus;
            private String username;



            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPUserId() {
                return pUserId;
            }

            public void setPUserId(String pUserId) {
                this.pUserId = pUserId;
            }

            public String getImageCode() {
                return imageCode;
            }

            public void setImageCode(String imageCode) {
                this.imageCode = imageCode;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String[] getImageUrlList() {
                return imageUrlList;
            }

            public void setImageUrlList(String[] imageUrlList) {
                this.imageUrlList = imageUrlList;
            }

            public Object getLikeId() {
                return likeId;
            }

            public void setLikeId(Object likeId) {
                this.likeId = likeId;
            }

            public int getLikeNum() {
                return likeNum;
            }

            public void setLikeNum(int likeNum) {
                this.likeNum = likeNum;
            }

            public boolean isHasLike() {
                return hasLike;
            }

            public void setHasLike(boolean hasLike) {
                this.hasLike = hasLike;
            }

            public Object getCollectId() {
                return collectId;
            }

            public void setCollectId(Object collectId) {
                this.collectId = collectId;
            }

            public int getCollectNum() {
                return collectNum;
            }

            public void setCollectNum(int collectNum) {
                this.collectNum = collectNum;
            }

            public boolean isHasCollect() {
                return hasCollect;
            }

            public void setHasCollect(boolean hasCollect) {
                this.hasCollect = hasCollect;
            }

            public boolean isHasFocus() {
                return hasFocus;
            }

            public void setHasFocus(boolean hasFocus) {
                this.hasFocus = hasFocus;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }
    }
}