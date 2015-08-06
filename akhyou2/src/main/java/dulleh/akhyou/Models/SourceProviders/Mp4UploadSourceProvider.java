package dulleh.akhyou.Models.SourceProviders;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class Mp4UploadSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = Jsoup.parse(body).select("div#player_code > script").first().html();

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, elementHtml.substring(elementHtml.indexOf("'file': ") + 9, elementHtml.indexOf(".mp4'") + 4)));

        return videos;
    }

}
