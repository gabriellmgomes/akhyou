package dulleh.akhyou.Utils.Events;

import dulleh.akhyou.Models.Anime;

public class OpenAnimeEvent {
    public final Anime anime;

    public OpenAnimeEvent(Anime anime) {
        this.anime = anime;
    }

}
