package dulleh.akhyou.Models.AnimeProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.SourceProviders.SourceProvider;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeRamAnimeProvider implements AnimeProvider {

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        Element animeBox = isolate(body);

        Elements infoAndEpisodes = animeBox.select("tr > td > div.content");

        if (!hasAnime(infoAndEpisodes)) {
            throw OnErrorThrowable.from(new Throwable("Failed to retrieve anime."));
        }

        Anime anime = new Anime()
                .setProviderType(Anime.ANIME_RAM)
                .setUrl(url)
                .setImageUrl(parseForImageUrl(animeBox));

        anime = parseForInfo(infoAndEpisodes, anime);

        anime.setEpisodes(parseForEpisodes(infoAndEpisodes));

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
    public List<Source> fetchSources(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        return parseForSources(isolate(body).select("tr > td > div > div > ul").last());
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(source.getPageUrl());

        source.setEmbedUrl(parseForEmbedUrl(isolate(body)));

        source.setVideos(source.getSourceProvider().fetchSource(source.getEmbedUrl()));

        return source;
    }

    private Element isolate (String body) {
        return Jsoup.parse(body).select("div.overeverything > table > tbody > tr > td > table > tbody").first();
    }

    private boolean hasAnime (Elements elements) {
        return !elements.toString().toLowerCase().contains("page not found");
    }

    private String parseForImageUrl (Element animeBox) {
        return animeBox.select("td > img").attr("src");
    }

    private Anime parseForInfo (Elements infoAndEpisodes, Anime anime) {
        Elements infoElements = infoAndEpisodes.first().select("table > tbody > tr > td > table > tbody > tr > td.content1");

        return anime
                .setTitle(infoElements.get(0).text())
                .setAlternateTitle(infoElements.get(1).text())
                .setGenresString(infoElements.get(2).text())
                .setDate(infoElements.get(4).text())
                .setStatus(infoElements.get(5).text())
                .setDesc(infoElements.last().text());
    }

    private List<Episode> parseForEpisodes (Elements infoAndEpisodes) {
        Elements episodesElement = infoAndEpisodes.last().select("ul > li > div");

        List<Episode> episodes = new ArrayList<>(episodesElement.size());

        for (Element episodeElement : episodesElement) {
            episodes.add(new Episode()
                    .setTitle(episodeElement.text())
                    .setUrl(episodeElement.select("a[href").attr("href")));
        }

        return episodes;
    }

    private List<Source> parseForSources (Element sourcesBox) throws OnErrorThrowable{
        Elements sourceElements = sourcesBox.select("li > a[href]");

        List<Source> sources = new ArrayList<>(sourceElements.size());

        for (Element sourceElement : sourceElements) {
            String title = sourceElement.text();
            SourceProvider sourceProvider = determineSourceProvider(title.toLowerCase());
            if (sourceProvider != null) {
                sources.add(new Source()
                                .setPageUrl(sourceElement.attr("href"))
                                .setTitle(title)
                                .setSourceProvider(sourceProvider)
                );
            }
        }

        return sources;
    }

    private SourceProvider determineSourceProvider (String lowerCaseTitle) {
        for (String sourceName : Source.sourceMap.keySet()) {
            if (lowerCaseTitle.contains(sourceName)) {
                return Source.sourceMap.get(sourceName);
            }
        }
        return null;
    }

    private String parseForEmbedUrl (Element embedPageIsolated) {
        return embedPageIsolated.select("tr > td > div.content > div > div > iframe[src]").last().attr("src");
    }

}
