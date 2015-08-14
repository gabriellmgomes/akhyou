package dulleh.akhyou.Utils.Events;

import dulleh.akhyou.Models.Anime;

public class FavouriteEvent {
    public Boolean isInFavourites;
    public boolean addToFavourites;
    public Anime anime;

    public FavouriteEvent (boolean addToFavourites, Anime anime) {
        this.addToFavourites = addToFavourites;
        this.anime = anime;
    }

    public FavouriteEvent (boolean isInFavourites, boolean addToFavourites, Anime anime) {
        this.isInFavourites = isInFavourites;
        this.addToFavourites = addToFavourites;
        this.anime = anime;
    }

}
