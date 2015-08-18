package dulleh.akhyou.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

public class Anime implements Parcelable{

    // has to be here cos conflicts with V
    public Anime () {}

    private Anime(Parcel in) {
        title = in.readString();
        desc = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        status = in.readString();
        alternateTitle = in.readString();
        date = in.readString();
        genres = in.createStringArray();
        genresString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeString(url);
        parcel.writeString(imageUrl);
        parcel.writeString(status);
        parcel.writeString(alternateTitle);
        parcel.writeString(date);
        parcel.writeStringArray(genres);
        parcel.writeString(genresString);
        parcel.writeList(episodes);
    }

    public static final Creator<Anime> CREATOR = new Creator<Anime>() {
        @Override
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

    // ---------------------------------------------------------------------------------- //

    private String title;
    private String desc;
    private String url;
    private String imageUrl;
    private String status;
    private String alternateTitle;
    private String date;
    private String[] genres;
    private String genresString;
    private List<Episode> episodes;
    private int majorColour;

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

    public String getGenresString() {
        return genresString;
    }

    public Anime setGenresString(String genresString) {
        this.genresString = genresString;
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

    public int getMajorColour() {
        return majorColour;
    }

    public Anime setMajorColour(int majorColour) {
        this.majorColour = majorColour;
        return this;
    }

    public void inheritWatchedFrom (List<Episode> oldEpisodes) {
        if (episodes != null) {
            List<String> episodeTitles = new LinkedList<>();

            for (Episode oldEpisode : oldEpisodes) {
                episodeTitles.add(oldEpisode.getTitle());
            }

            for (int i = 0; i < episodes.size(); i++) {
                Episode episode = episodes.get(i);
                if (episodeTitles.contains(episode.getTitle())) {
                    episodes.set(i, episode.setWatched(oldEpisodes.get(episodeTitles.indexOf(episode.getTitle())).isWatched()));
                }
            }
        }
    }

}
