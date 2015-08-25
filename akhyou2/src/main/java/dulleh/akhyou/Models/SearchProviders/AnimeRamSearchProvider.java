package dulleh.akhyou.Models.SearchProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeRamSearchProvider implements SearchProvider{
    private static final String BASE_URL = "http://www.animeram.me/anime-list/search/";

    @Override
    public List<Anime> searchFor(String searchTerm) throws OnErrorThrowable {

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw OnErrorThrowable.from(new Throwable("Please enter a search term."));
        }

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
    public Element isolate(String document) {
        return Jsoup.parse(document).select("div.overeverything > table > tbody > tr > td >table > tbody > tr > td > div > table > tbody > tr > td").first();
    }

    @Override
    public boolean hasSearchResults(Element element) throws OnErrorThrowable {
        return !element.select("td > div").text().contains("Nothing here");
    }

    private Elements seperateResults (Element searchResultsBox) {
        return searchResultsBox.children();
    }
    private List<Anime> parseResults (Elements searchResults) {
        List<Anime> animes = new ArrayList<>(searchResults.size());
        for (Element searchResult : searchResults) {
            Anime anime = new Anime().setProviderType(Anime.ANIME_RAM);

            Element titleAndUrl = searchResult.select("h3 > a[href]").first();
            anime.setUrl(titleAndUrl.attr("href"));
            anime.setTitle(titleAndUrl.text());

            // image is low quality, so use large image they use on the stand-alone anime page
            anime.setImageUrl(searchResult.select("img").first().attr("src").replace("94", "180"));
            anime.setDesc(searchResult.select("div.popinfo").text());

            animes.add(anime);
        }
        return animes;
    }

}
