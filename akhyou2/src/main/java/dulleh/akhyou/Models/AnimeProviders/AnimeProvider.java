package dulleh.akhyou.Models.AnimeProviders;

import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Source;
import rx.exceptions.OnErrorThrowable;

public interface AnimeProvider {

    Anime fetchAnime(String url) throws OnErrorThrowable;

    Anime updateCachedAnime (Anime cachedAnime) throws OnErrorThrowable;

    List<Source> fetchSources (String url) throws OnErrorThrowable;

    Source fetchVideo (Source source) throws OnErrorThrowable;

}
