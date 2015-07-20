package dulleh.akhyou.Episodes.Providers;

import dulleh.akhyou.Models.Anime;
import rx.exceptions.OnErrorThrowable;

public interface EpisodesProvider {

    Anime fetchAnime(String url) throws OnErrorThrowable;

    String getResponse (String url) throws OnErrorThrowable;

}
