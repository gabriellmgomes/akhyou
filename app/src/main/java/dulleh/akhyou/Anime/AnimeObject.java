package dulleh.akhyou.Anime;

import java.util.ArrayList;

import dulleh.akhyou.Episodes.EpisodeObject;

/**
 * Created by Khwaja on 22/03/2015.
 */
public class AnimeObject {
    private String title;
    private String link;
    private String imageLink;
    private String desc;
    private ArrayList<EpisodeObject> episodesList;
    private ArrayList<String> genreList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void createEpisodeList() {
        this.episodesList = new ArrayList<>();
    }

    public ArrayList<EpisodeObject> getEpisodesList() {
        return episodesList;
    }

    public void createGenreList() {
       this.genreList = new ArrayList<>();
    }

    public ArrayList<String> getGenreList() {
        return genreList;
    }

    public void appendGenres() {
        try {
            this.desc = this.desc + this.getGenreList().toString();
        }catch (Exception e) {e.printStackTrace();}
    }

}