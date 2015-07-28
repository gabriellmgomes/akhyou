package dulleh.akhyou.Anime.Providers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.SourceProviders.DailyMotionSourceProvider;
import dulleh.akhyou.Models.SourceProviders.EngineSourceProvider;
import dulleh.akhyou.Models.SourceProviders.Mp4UploadSourceProvider;
import dulleh.akhyou.Models.SourceProviders.YourUploadSourceProvider;
import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeRushAnimeProvider implements AnimeProvider {
    private static final String MP4UPLOAD = "mp4upload";
    private static final String DAILYMOTION = "dailymotion";
    private static final String ENGINE = "engine";
    private static final String YOURUPLOAD = "yourupload";

    @Override
    public Anime fetchAnime(String url) {
        String body = GeneralUtils.getWebPage(url);

        Element animeBox = isolate(body);

        if (!hasAnime(animeBox)) {
            throw OnErrorThrowable.from(new Throwable("Failed to retrieve anime."));
        }

        Anime anime = new Anime();

        animeBox = animeBox.select("div.amin_box2").first();
        anime.setTitle(parseForTitle(animeBox));

        animeBox = animeBox.select("div.desc_box_mid").first();
        anime.setImageUrl(parseForImageUrl(animeBox));

        anime = parseForInformation(anime, animeBox);

        anime = parseForEpisodes(anime, animeBox);

        return anime;
    }

    @Override
    public List<Source> fetchSources(String url) {
        String body = GeneralUtils.getWebPage(url);

        Element sourcesBox = isolate(body)
                .select("div#episodes")
                .first();

        return parseForSources(sourcesBox);
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        String pageBody = GeneralUtils.getWebPage(source.getPageUrl());

        Element videoBox = isolate(pageBody)
                .select("div.player-area")
                .first();

        source.setEmbedUrl(parseForEmbedUrl(videoBox));

        if (source.getEmbedUrl().isEmpty()) {
            throw OnErrorThrowable.from(new Throwable("Video removed."));
        }

        source.setVideos(parseForVideoUrl(source));

        if (source.getVideos() == null) {
            throw OnErrorThrowable.from(new Throwable("Unsupported source."));
        }

        if (source.getVideos().isEmpty()) {
            throw OnErrorThrowable.from(new Throwable("Video retrieval failed."));
        }

        return source;
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

        FakeAnimeProvider fakeEpisodesProvider = new FakeAnimeProvider();

        for (Element e : episodeElements) {
            Episode episode = new Episode();
            episode.setTitle(e.select("a.fixedLinkColor").text().replace("- ", "").replace("Watch", "").replace("now", "").trim());
            episode.setUrl(e.select("a.fixedLinkColor").attr("href"));
            episodes.add(episode);
            //episode.setSources(fakeEpisodesProvider.fakeSources());
        }

        return anime.setEpisodes(episodes);
    }

    private List<Source> parseForSources (Element element) {
        Elements sourceElements = element.select("div.episode1");
        List<Source> sources = new ArrayList<>(sourceElements.size());

        for (Element e : sourceElements ) {
            StringBuilder titleBuilder = new StringBuilder();
            Source source = new Source();

            Element titleAndUrlElement = e.select("h3 > a").first();

            titleBuilder.append(titleAndUrlElement.text().replace(" Video", ""));
            source.setPageUrl(titleAndUrlElement.attr("href"));

            if (!e.select("div.hdlogo").isEmpty() && !titleBuilder.toString().toLowerCase().contains("hd")) {
                titleBuilder.append(" HD");
            }

            if (!e.select("span.mirror-sub.subbed").isEmpty()) {
                titleBuilder.append(" SUBBED");
            } else {
                titleBuilder.append(" DUBBED");
            }

            source.setTitle(titleBuilder.toString());

/*
 * To check for if we're already on the source's episode page
 * currently no way to use this information so I'm commenting it out

            if (!element.select("div.episode_on").isEmpty()) {
                // it's on this page
            } else {
                // it's not on this page
            }

*/

            sources.add(source);
        }

        return sources;
    }

    private String parseForEmbedUrl (Element element) {
        return element.select("div.player-area > div > div > iframe")
                .first()
                .attr("src");
    }

    private List<Video> parseForVideoUrl (Source source) {
        String lowerCaseSourceTitle = source.getTitle().toLowerCase();

        if (lowerCaseSourceTitle.contains(MP4UPLOAD)) {
            return new Mp4UploadSourceProvider().fetchSource(source.getEmbedUrl());
        } else if (lowerCaseSourceTitle.contains(DAILYMOTION)) {
            return new DailyMotionSourceProvider().fetchSource(source.getEmbedUrl());
        } else if (lowerCaseSourceTitle.contains(ENGINE)) {
            return new EngineSourceProvider().fetchSource(source.getEmbedUrl());
        } else if (lowerCaseSourceTitle.contains(YOURUPLOAD)) {
            return new YourUploadSourceProvider().fetchSource(source.getEmbedUrl());
        }

        return null;
    }

}
