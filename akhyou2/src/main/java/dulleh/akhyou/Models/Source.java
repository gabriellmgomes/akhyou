package dulleh.akhyou.Models;

import java.util.List;

public class Source {
    private String title;
    private String pageUrl;
    private String embedUrl;
    private List<Video> videos;

    public String getTitle() {
        return title;
    }

    public Source setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public Source setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public Source setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
        return this;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public Source setVideos(List<Video> videos) {
        this.videos = videos;
        return this;
    }
}
