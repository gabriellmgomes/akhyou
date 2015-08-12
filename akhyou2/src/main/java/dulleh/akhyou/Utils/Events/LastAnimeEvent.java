package dulleh.akhyou.Utils.Events;

import dulleh.akhyou.Models.Anime;

public class LastAnimeEvent {
    public final Anime anime;

    public LastAnimeEvent (Anime anime) {
        this.anime = anime;
    }

}
