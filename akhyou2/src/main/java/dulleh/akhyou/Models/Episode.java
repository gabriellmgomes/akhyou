package dulleh.akhyou.Models;

import java.util.List;

public class Episode {
    private String title;
    private String url;
    private List<Source> sources;

    public String getTitle() {
        return title;
    }

    public Episode setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Episode setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<Source> getSources() {
        return sources;
    }

    public Episode setSources(List<Source> sources) {
        this.sources = sources;
        return this;
    }
}
