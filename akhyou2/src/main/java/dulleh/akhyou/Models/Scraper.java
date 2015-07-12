package dulleh.akhyou.Models;

public interface Scraper {

    Anime[] searchFor(String searchTerm);

    String fetchUrl(String url);

    Anime fetchAnime(String url);

    String fetchTitle(String url);

    String fetchDesc(String url);

    String fetchImageUrl(String url);

    String[] fetchGenres(String url);

    String fetchGenre(String url);

    Episode[] fetchEpisodes(String url);

    Episode fetchEpisode(String url);

    Episode fetchEpisodeTitle(String url);

    String fetchEpisodeLink(String url);

    Source[] fetchSources(String url);

    Source fetchSource(String url);

    String fetchSourceTitle(String url);

    String fetchSourceUrl(String url);

}