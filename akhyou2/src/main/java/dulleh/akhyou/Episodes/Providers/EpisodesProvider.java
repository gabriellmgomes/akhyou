package dulleh.akhyou.Episodes.Providers;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;

public interface EpisodesProvider {

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

}
