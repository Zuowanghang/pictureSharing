package com.example.picturesharing.pojo;

public class UserInfo {
    private String image;
    private String name = "Umbrella";
    private String sex = "男";
    private String signature = "这个人很懒，什么都没留下";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", signature='" + signature + '\'' +
                '}';
    }
}
