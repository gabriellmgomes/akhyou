package dulleh.akhyou;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.Events.FavouriteEvent;
import dulleh.akhyou.Utils.Events.LastAnimeEvent;
import dulleh.akhyou.Utils.GeneralUtils;

public class MainModel {
    private SharedPreferences sharedPreferences;

    public MainModel (SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /*
    *
    * FAVOURITES
    *
    */
    private static final String FAVOURITES_PREF = "favourites_preference";

    public ArrayList<Anime> favouritesList;

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void refreshFavouritesList () {
        Set<String> favourites = new HashSet<>(sharedPreferences.getStringSet(FAVOURITES_PREF, new HashSet<>()));

        favouritesList = new ArrayList<>(favourites.size());
        for (String favourite : favourites) {
            Anime anime = GeneralUtils.deserializeFavourite(favourite);
            if (anime != null) {
                favouritesList.add(anime);
            }
        }

    }

    public void saveFavourites () {
        Set<String> favourites = new HashSet<>(favouritesList.size());
        for (Anime anime : favouritesList) {
            favourites.add(GeneralUtils.serializeFavourite(anime));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(FAVOURITES_PREF, favourites);
        editor.apply();
    }

    public boolean isInFavourites (String url)  throws Exception{
        if (favouritesList != null) {
            for (Anime favourite : favouritesList) {
                if (favourite != null && favourite.getUrl().equals(url)) {
                    return true;
                }
            }
        } else {
            throw new Exception("Failed to parse favourites.");
        }
        return false;
    }

    public void addOrRemoveFromFavourites (FavouriteEvent event) throws Exception{

        if (event.isInFavourites == null) {
            event = new FavouriteEvent(isInFavourites(event.anime.getUrl()), event.addToFavourites, event.anime);
        }

        if (!event.isInFavourites && event.addToFavourites) {
            favouritesList.add(event.anime);
        } else if (event.isInFavourites) {
            for (Anime favourite : favouritesList) {
                if (favourite.getUrl().equals(event.anime.getUrl())) {
                    favouritesList.remove(favourite);
                    break;
                }
            }
        }

    }


    /*
    *
    * LAST ANIME
    *
    */
    private static final String LAST_ANIME_TITLE_PREF = "last_anime_title_preference";
    private static final String LAST_ANIME_URL_PREF = "last_anime_url_preference";

    public String getLastAnimeTitle () {
        return sharedPreferences.getString(LAST_ANIME_TITLE_PREF, null);
    }

    public String getLastAnimeUrl () {
        return sharedPreferences.getString(LAST_ANIME_URL_PREF, null);
    }

    public void saveNewLastAnime (LastAnimeEvent event) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_ANIME_TITLE_PREF, event.anime.getTitle());
            editor.putString(LAST_ANIME_URL_PREF, event.anime.getUrl());
            editor.apply();
        }
    }

}
