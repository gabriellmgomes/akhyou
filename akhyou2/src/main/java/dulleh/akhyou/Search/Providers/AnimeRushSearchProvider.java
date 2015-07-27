package dulleh.akhyou.Search.Providers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeRushSearchProvider implements SearchProvider{
    private static final String BASE_URL = "http://www.animerush.tv/search.php?searchquery=";

    @Override
    public List<Anime> searchFor(String searchTerm){
        String url = BASE_URL + GeneralUtils.encodeForUtf8(searchTerm);

        String responseBody = GeneralUtils.getWebPage(url);

        Element searchResultsBox =  isolate(responseBody);

        if (!hasSearchResults(searchResultsBox)) {
            throw OnErrorThrowable.from(new Throwable("No search results."));
        }

        Elements searchResults = seperateResults(searchResultsBox);

        return parseResults(searchResults);
    }

    @Override
    public Element isolate (String document) {
        return Jsoup
                .parse(document, "http://www.animerush.tv/")
                .select("div#left-column > div.amin_box2 > div.amin_box_mid")
                .first();
    }

    private boolean hasSearchResults (Element element) throws OnErrorThrowable{
        if (element.select("div.success").isEmpty()) {
            return false;
        }
        return true;
    }

    private Elements seperateResults (Element searchResultsBox) {
        return searchResultsBox.select("div.search-page_in_box_mid_link");
    }

    private List<Anime> parseResults (Elements searchResults) {
        List<Anime> animes = new ArrayList<>(searchResults.size());
        for (Element searchResult : searchResults) {
            Anime anime = new Anime();

            anime.setTitle(searchResult.select("h3").text().trim())
                    .setUrl(searchResult.select("a.highlightit").attr("href").trim())
                    .setDesc(searchResult.select("p").text().trim())
                    .setImageUrl(searchResult.select("object.highlightz").attr("data").trim());

            animes.add(anime);
        }
        return animes;
    }

}
