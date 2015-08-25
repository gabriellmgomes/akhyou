package dulleh.akhyou.Models.SearchProviders;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;

public class FakeSearchProvider {

    public List<Anime> searchFor(String searchTerm) {
        return fakeAnimes();
    }

    public Element isolate(String document) {
        return null;
    }

    private List<Anime> fakeAnimes() {
        Anime anime = new Anime()
                .setTitle("Hyouka")
                .setUrl("http://www.animerush.tv/anime/hyouka")
                .setDesc("Oreki Hotaro is a high school boy, who always acts passively. One day, he enters \"Koten Bu (Classic Literature Club)\" as recommended by his elder sister. There he meets Chitanda Eru, Fukube Satoshi, and Ibara Mayaka. Chitanda is a calm beautiful girl but she turns into an embodiment of curiosity once she says, \"I'm interested in it (Watashi, Kininarimasu)\". Fukube is a smiling boy with a fantastic memory, he never deduces anything on his own. Ibara is a short girl and is strict with others and herself. She loves Fukube, but he always dodges her approaches. They begin to investigate a case that occurred 33 years ago. Hints of the mystery are buried in an old collection of works of the former members of Koten Club. The collection is titled \"Hyouka\".")
                .setImageUrl("http://www.animerush.tv/anime-images-big/hyouka.jpg");
        List<Anime> fakeAnime = new ArrayList<>(0);
        fakeAnime.add(anime);
        fakeAnime.add(anime);
        fakeAnime.add(anime);
        fakeAnime.add(anime);
        fakeAnime.add(anime);
        return fakeAnime;
    }

}
