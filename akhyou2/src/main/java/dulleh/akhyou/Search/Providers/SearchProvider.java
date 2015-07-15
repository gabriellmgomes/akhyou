package dulleh.akhyou.Search.Providers;

import dulleh.akhyou.Models.Anime;
import rx.exceptions.OnErrorThrowable;

public interface SearchProvider {

    Anime[] searchFor (String searchTerm);

    String encodeURL (String s);

    String getResponse (String url) throws OnErrorThrowable;

}
