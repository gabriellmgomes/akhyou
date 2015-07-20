package dulleh.akhyou.Episodes.Providers;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;

public class FakeEpisodesProvider extends BaseEpisodesProvider{

    @Override
    public Anime fetchAnime(String url) {
        return fakeAnime();
    }


    private Anime fakeAnime() {
        return new Anime()
                .setTitle("Hyouka")
                .setUrl("http://www.animerush.tv/anime/hyouka")
                .setDesc("Oreki Hotaro is a high school boy, who always acts passively. One day, he enters \"Koten Bu (Classic Literature Club)\" as recommended by his elder sister. There he meets Chitanda Eru, Fukube Satoshi, and Ibara Mayaka. Chitanda is a calm beautiful girl but she turns into an embodiment of curiosity once she says, \"I'm interested in it (Watashi, Kininarimasu)\". Fukube is a smiling boy with a fantastic memory, he never deduces anything on his own. Ibara is a short girl and is strict with others and herself. She loves Fukube, but he always dodges her approaches. They begin to investigate a case that occurred 33 years ago. Hints of the mystery are buried in an old collection of works of the former members of Koten Club. The collection is titled \"Hyouka\".")
                .setImageUrl("http://www.animerush.tv/anime-images-big/hyouka.jpg")
                .setEpisodes(fakeEpisodes())
                .setGenres(fakeGenres());
    }

    private List<Episode> fakeEpisodes() {
        Episode episode = new Episode()
                .setTitle("Episode 1")
                .setUrl("http://www.animerush.tv/hyouka-episode-1/mirror-283153/")
                .setSources(fakeSources());
        List<Episode> episodes = new ArrayList<>(12);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        episodes.add(episode);
        return episodes;
    }

    public List<Source> fakeSources() {
        Source source = new Source()
                .setTitle("Mp4Upload SUBBED")
                .setUrl("http://www6.mp4upload.com:182/d/sgxt5lsdz3b4quuolsuaypswicaihpuuhvjaxciv2zeqxpiig7zm6tgq/video.mp4");
        List<Source> sources = new ArrayList<>(4);
        sources.add(source);
        sources.add(source);
        sources.add(source);
        sources.add(source);
        return  sources;
    }

    private String[] fakeGenres() {
        return new String[] {"Mystery", "School"};
    }

}
