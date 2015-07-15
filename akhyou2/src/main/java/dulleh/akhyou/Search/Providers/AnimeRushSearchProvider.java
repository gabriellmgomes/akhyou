package dulleh.akhyou.Search.Providers;

import android.support.design.widget.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;


import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.SnackbarEvent;
import rx.exceptions.OnErrorThrowable;

public class AnimeRushSearchProvider extends BaseSearchProvider{

    @Override
    public Anime[] searchFor(String searchTerm) throws OnErrorThrowable {
        BASE_URL = "http://www.animerush.tv/search.php?searchquery=";
        String url = encodeURL(searchTerm);

        String responseBody = getResponse(url);
        System.out.println(responseBody);

        //Elements elements = Jsoup.parse(responseBody, "http://www.animerush.tv/").select()

        Anime[] animes = new FakeSearchProvider().searchFor(searchTerm);
        Anime anime = animes[0];
        anime.setDesc(responseBody);
        animes[0] = anime;
        return animes;
    }

}
