package dulleh.akhyou;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.Events.FavouriteEvent;
import dulleh.akhyou.Utils.Events.LastAnimeEvent;
import dulleh.akhyou.Utils.GeneralUtils;

public class MainModel {
    private SharedPreferences sharedPreferences;

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public MainModel (SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /*
    *
    * FAVOURITES
    *
    */
    private static final String FAVOURITES_PREF = "favourites_preference";

    // The key is the anime url.
    private HashMap<String, Anime> favouritesMap;

    public void setFavourites (ArrayList<Anime> favourites) {
        for (Anime favourite : favourites) {
            favouritesMap.put(favourite.getUrl(), favourite);
        }
    }

    public ArrayList<Anime> getFavourites () {
        if (favouritesMap != null) {
            ArrayList<Anime> favourites = new ArrayList<Anime>();
            favourites.addAll(favouritesMap.values());
            return favourites;
        }
        return null;
    }

    public void refreshFavouritesList () {
        Set<String> favourites = new HashSet<>(sharedPreferences.getStringSet(FAVOURITES_PREF, new HashSet<>()));

        favouritesMap = new HashMap<>(favourites.size());
        for (String favourite : favourites) {
            Anime anime = GeneralUtils.deserializeAnime(favourite);
            if (anime != null) {
                favouritesMap.put(anime.getUrl(), anime);
            }
        }
    }

    public void saveFavourites () {
        Set<String> favourites = new HashSet<>(favouritesMap.size());
        for (Anime anime : favouritesMap.values()) {
            favourites.add(GeneralUtils.serializeAnime(anime));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(FAVOURITES_PREF, favourites);
        editor.apply();
    }

    public boolean isInFavourites (String url)  throws Exception{
        if (favouritesMap != null) {
            if (favouritesMap.containsKey(url)) return true;
        } else {
            throw new Exception("Failed to parse favourites.");
        }
        return false;
    }

    public void addOrRemoveFromFavourites (FavouriteEvent event) throws Exception{

        if (event.isInFavourites == null) {
            event = new FavouriteEvent(isInFavourites(event.anime.getUrl()), event.addToFavourites, event.anime);
        }

        if (favouritesMap != null) {
            if (!event.isInFavourites && event.addToFavourites) {
                favouritesMap.put(event.anime.getUrl(), event.anime);
            } else if (event.isInFavourites) {
                favouritesMap.remove(event.anime.getUrl());
            }
        }

    }

    // Anime must be in favouritesList for this
    public void updateFavourite (Anime favourite) {
        if (favouritesMap.keySet().contains(favourite.getUrl())) {
            favouritesMap.put(favourite.getUrl(), favourite);
        }
    }


    /*
    *
    * LAST ANIME
    *
    */
    private static final String LAST_ANIME_PREF = "last_anime_preference";

    public Anime getLastAnime () {
        // need to check for null or else deserialize will throw null pointer exception
        String serializedAnime = sharedPreferences.getString(LAST_ANIME_PREF, null);
        if (serializedAnime != null) {
            return GeneralUtils.deserializeAnime(serializedAnime);
        }
        return null;
    }

    public void saveNewLastAnime (LastAnimeEvent event) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_ANIME_PREF, GeneralUtils.serializeAnime(event.anime));
            editor.apply();
        }
    }

}
