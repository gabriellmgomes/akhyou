package dulleh.akhyou.Models;

import java.util.List;

public class Anime {
    private String title;
    private String desc;
    private String url;
    private String imageUrl;
    private String status;
    private String alternateTitle;
    private String date;
    private String[] genres;
    private List<Episode> episodes;

    public String getTitle() {
        return title;
    }

    public Anime setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public Anime setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Anime setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Anime setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String[] getGenres() {
        return genres;
    }

    public Anime setGenres(String[] genres) {
        this.genres = genres;
        return this;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public Anime setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Anime setDate(String date) {
        this.date = date;
        return this;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public Anime setAlternateTitle(String alternateTitle) {
        this.alternateTitle = alternateTitle;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Anime setStatus(String status) {
        this.status = status;
        return this;
    }
}
