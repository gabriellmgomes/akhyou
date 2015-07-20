package dulleh.akhyou.Search.Providers;

import org.jsoup.nodes.Element;

import java.util.List;

import dulleh.akhyou.Models.Anime;
import rx.exceptions.OnErrorThrowable;

public interface SearchProvider {

    List<Anime> searchFor (String searchTerm) throws OnErrorThrowable ;

    String encodeURL (String s);

    String getResponse (String url) throws OnErrorThrowable;

    Element isolate (String document);

}
