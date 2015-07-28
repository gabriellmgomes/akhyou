package dulleh.akhyou.Models.SourceProviders;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class YourUploadSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPage) throws OnErrorThrowable {

        String body = GeneralUtils.getWebPage(embedPage);

        String elementHtml = Jsoup.parse(body).select("div#player").first().nextElementSibling().html();

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, elementHtml.substring(elementHtml.indexOf("file: '") + 7, elementHtml.indexOf("',"))));

        return videos;
    }

}
