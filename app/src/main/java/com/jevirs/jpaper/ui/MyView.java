package com.jevirs.jpaper.ui;

public class MyView {
    private String id;
    private String title;
    private String thumbUrl;
    private String url;

    public MyView(String id,String title,String thumbUrl,String url) {
        this.id = id;
        this.title = title;
        this.thumbUrl = thumbUrl;
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
