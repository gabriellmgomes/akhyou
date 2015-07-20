package dulleh.akhyou.Episodes.Providers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import rx.exceptions.OnErrorThrowable;

public class AnimeRushEpisodesProvider extends BaseEpisodesProvider{

    @Override
    public Anime fetchAnime(String url) {

        String responseBody = getResponse(url);

        Element animeBox = isolate(responseBody);

        if (!hasAnime(animeBox)) {
            throw OnErrorThrowable.from(new Throwable("Failed to retrieve anime."));
        }

        Anime anime = new Anime();

        animeBox = animeBox.select("div.amin_box2").first();
        anime.setTitle(parseForTitle(animeBox));

        animeBox = animeBox.select("div.desc_box_mid").first();
        anime.setImageUrl(parseForImageUrl(animeBox));


        anime = parseForEpisodes(anime, animeBox);

        return anime;
    }


    private Element isolate (String document) {
        return Jsoup
                .parse(document, "http://www.animerush.tv/")
                .select("div#left-column")
                .first();
    }

    private boolean hasAnime (Element element) {
        if (!element.select("div.errormessage").isEmpty()) {
            return false;
        }
        return true;
    }

    private String parseForTitle (Element element) {
        return element.select("h1").text();
    }

    private String parseForImageUrl (Element element) {
        return element.select("div.cat_image > object").first().attr("data");
    }

    private Anime parseForInformation(Anime anime, Element element) {
        element = element.select("div.cat_box_desc").first();
        String catBoxDesc = element.getAllElements().text();

        String[] currentSplit = catBoxDesc.split("Status: ")[1].split("Alternative Titles: ");
        anime.setStatus(currentSplit[0]);

        currentSplit = currentSplit[1].split("Year: ");
        anime.setAlternateTitle(currentSplit[0]);

        currentSplit = currentSplit[1].split("Genres: ");
        anime.setDate(currentSplit[0]);

        currentSplit = currentSplit[1].split("Description: ");
        anime.setDesc(currentSplit[1]);

        anime.setGenres(currentSplit[0].split(", "));
        return anime;
    }

    private Anime parseForEpisodes(Anime anime, Element element) {
        Elements episodeElements = element.select("div.episode_list");
        List<Episode> episodes = new ArrayList<>(episodeElements.size());

        FakeEpisodesProvider fakeEpisodesProvider = new FakeEpisodesProvider();

        for (Element e : episodeElements) {
            Episode episode = new Episode();
            episode.setTitle(e.select("a.fixedLinkColor").text().replace("- ", "").replace("Watch", "").replace("now", "").trim());
            episode.setUrl(e.select("a.fixedLinkColor").attr("href"));
            episodes.add(episode);
            episode.setSources(fakeEpisodesProvider.fakeSources());
        }

        return anime.setEpisodes(episodes);
    }
}
