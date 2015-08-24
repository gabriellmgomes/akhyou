package dulleh.akhyou.Models.AnimeProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.SourceProviders.DailyMotionSourceProvider;
import dulleh.akhyou.Models.SourceProviders.EngineSourceProvider;
import dulleh.akhyou.Models.SourceProviders.GoSourceProvider;
import dulleh.akhyou.Models.SourceProviders.Mp4UploadSourceProvider;
import dulleh.akhyou.Models.SourceProviders.SourceProvider;
import dulleh.akhyou.Models.SourceProviders.VkSourceProvider;
import dulleh.akhyou.Models.SourceProviders.YourUploadSourceProvider;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeRushAnimeProvider implements AnimeProvider {
    private Element animeBox;

    private static final String[] sourceList = {
            "mp4upload",
            "dailymotion",
            "engine",
            "youruplaod",
            "vk",
            "go"
    };

    @Override
    public Anime fetchAnime(String url) {
        String body = GeneralUtils.getWebPage(url);

        animeBox = isolate(body);

        if (!hasAnime(animeBox)) {
            throw OnErrorThrowable.from(new Throwable("Failed to retrieve anime."));
        }

        Anime anime = new Anime().setUrl(url);

        animeBox = animeBox.select("div.amin_box2").first();
        anime.setTitle(parseForTitle());

        animeBox = animeBox.select("div.desc_box_mid").first();
        anime.setImageUrl(parseForImageUrl());

        anime = parseForInformation(anime);

        anime.setEpisodes(parseForEpisodes());

        return anime;
    }

    @Override
    public Anime updateCachedAnime(Anime cachedAnime) throws OnErrorThrowable {
        Anime updatedAnime = fetchAnime(cachedAnime.getUrl());

        updatedAnime.inheritWatchedFrom(cachedAnime.getEpisodes());

        updatedAnime.setMajorColour(cachedAnime.getMajorColour());

        return updatedAnime;
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

        source.setVideos(source.getSourceProvider().fetchSource(source.getEmbedUrl()));

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
        return element.select("div.errormessage").isEmpty();
    }

    private String parseForTitle () {
        return animeBox.select("h1").text();
    }

    private String parseForImageUrl () {
        return animeBox.select("div.cat_image > object").first().attr("data");
    }

    private Anime parseForInformation (Anime anime) {
        Element element = animeBox.select("div.cat_box_desc").first();
        String catBoxDesc = element.getAllElements().text();

        String[] currentSplit = catBoxDesc.split("Status: ")[1].split("Alternative Titles: ");
        anime.setStatus(currentSplit[0]);

        currentSplit = currentSplit[1].split("Year: ");
        anime.setAlternateTitle(currentSplit[0]);

        currentSplit = currentSplit[1].split("Genres: ");
        anime.setDate(currentSplit[0]);

        currentSplit = currentSplit[1].split("Description: ");
        anime.setDesc(currentSplit[1]);

        String[] genres = currentSplit[0].split(", ");
        anime.setGenres(genres);
        anime.setGenresString(GeneralUtils.formattedGeneres(genres));

        return anime;
    }

    private List<Episode> parseForEpisodes () {
        Elements episodeElements = animeBox.select("div.episode_list");
        List<Episode> episodes = new ArrayList<>(episodeElements.size());

        for (Element e : episodeElements) {
            Episode episode = new Episode();
            episode.setTitle(e.select("a.fixedLinkColor").text().replace("- ", "").replace("Watch", "").replace("now", "").trim());
            episode.setUrl(e.select("a.fixedLinkColor").attr("href"));
            episodes.add(episode);
        }

        return episodes;
    }

    private List<Source> parseForSources (Element element) {
        Elements sourceElements = element.select("div.episode1");
        List<Source> sources = new ArrayList<>(sourceElements.size());

        for (Element e : sourceElements ) {
            StringBuilder titleBuilder = new StringBuilder();
            Source source = new Source();

            Element titleAndUrlElement = e.select("h3 > a").first();

            titleBuilder.append(titleAndUrlElement.text().replace(" Video", ""));

            String lowerCaseSourceTitle = titleBuilder.toString().toLowerCase();

            for (int i = 0; i < sourceList.length; i++) {
                if (lowerCaseSourceTitle.contains(sourceList[i])) {

                    if (i == 0) {

                        sources.add(addSourceTitleUrlProvider(e, titleAndUrlElement, source, new Mp4UploadSourceProvider(), titleBuilder));
                        break;

                    } else if (i == 1) {

                        sources.add(addSourceTitleUrlProvider(e, titleAndUrlElement, source, new DailyMotionSourceProvider(), titleBuilder));
                        break;

                    } else if (i == 2) {

                        sources.add(addSourceTitleUrlProvider(e, titleAndUrlElement, source, new EngineSourceProvider(), titleBuilder));
                        break;

                    } else if (i == 3) {

                        sources.add(addSourceTitleUrlProvider(e, titleAndUrlElement, source, new YourUploadSourceProvider(), titleBuilder));
                        break;

                    } else if (i == 4) {

                        sources.add(addSourceTitleUrlProvider(e, titleAndUrlElement, source, new VkSourceProvider(), titleBuilder));
                        break;

                    } else if (i == 5) {

                        sources.add(addSourceTitleUrlProvider(e, titleAndUrlElement, source, new GoSourceProvider(), titleBuilder));
                        break;

                    }

                }
            }


/*
 * To check for if we're already on the source's episode page
 * currently no way to use this information so I'm commenting it out

            if (!element.select("div.episode_on").isEmpty()) {
                // it's on this page
            } else {
                // it's not on this page
            }

*/
                //}
            //}
        }

        return sources;
    }

    private Source addSourceTitleUrlProvider (Element e, Element titleAndUrlElement, Source source, SourceProvider sourceProvider, StringBuilder titleBuilder) {

        source.setPageUrl(titleAndUrlElement.attr("href"));

        if (!e.select("div.hdlogo").isEmpty() && !titleBuilder.toString().toLowerCase().contains("hd")) {
            titleBuilder.append(" HD");
        }

        if (!e.select("span.mirror-sub.subbed").isEmpty()) {
            titleBuilder.append(" SUBBED");
        } else {
            titleBuilder.append(" DUBBED");
        }

        source.setSourceProvider(sourceProvider);

        source.setTitle(titleBuilder.toString());

        return source;
    }

    private String parseForEmbedUrl (Element element) {
        return element.select("div.player-area > div > div > iframe")
                .first()
                .attr("src");
    }

}
