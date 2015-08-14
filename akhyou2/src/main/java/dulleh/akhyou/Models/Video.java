package dulleh.akhyou.Models;

import java.io.Serializable;

public class Video implements Serializable{
    private String title;
    private String url;

    public Video (String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public Video setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Video setUrl(String url) {
        this.url = url;
        return this;
    }

}
