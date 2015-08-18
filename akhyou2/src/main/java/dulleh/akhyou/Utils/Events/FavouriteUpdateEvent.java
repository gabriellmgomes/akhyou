package dulleh.akhyou.Utils.Events;

import dulleh.akhyou.Models.Anime;

public class FavouriteUpdateEvent {
    public Anime favourite;

    public FavouriteUpdateEvent (Anime favourite) {
        this.favourite = favourite;
    }

}
