package dulleh.akhyou.Models;

public class Anime {
    private String title;
    private String desc;
    private String url;
    private String imageUrl;
    private String[] genres;
    private Episode[] episodes;

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

    public Episode[] getEpisodes() {
        return episodes;
    }

    public Anime setEpisodes(Episode[] episodes) {
        this.episodes = episodes;
        return this;
    }

}
