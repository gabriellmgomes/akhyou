package dulleh.akhyou.Models.SourceProviders;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class EngineSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) throws OnErrorThrowable {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml;
        try {
            elementHtml = Jsoup.parse(body).select("div#ret").first().nextElementSibling().html();
        } catch (Exception e) {
            throw OnErrorThrowable.from(new Throwable("Engine video retrieval failed.", e));
        }

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, elementHtml.substring(18, elementHtml.indexOf("';"))));

        return videos;
    }

}
