package com.example.picturesharing.pojo;

import java.util.ArrayList;

public class ReleaseContent {
    public static String savedData;
    private ArrayList<String> images;
    private String title;
    private String content;

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
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

    @Override
    public String toString() {
        return "ReleaseContent{" +
                "images=" + images +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
