package dulleh.akhyou.Models.SourceProviders;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class GoSourceProvider implements SourceProvider {

    @Override
    public List<Video> fetchSource(String embedPageUrl) throws OnErrorThrowable {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = Jsoup.parse(body).select("div#flowplayer").first().nextElementSibling().html();

        List<Video> videos = new ArrayList<>(1);

        videos.add(new Video(null, elementHtml.substring(elementHtml.indexOf("url: '") + 6, elementHtml.lastIndexOf("'"))));

        return videos;
    }

}
