package dulleh.akhyou.Models.SourceProviders;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class YourUploadSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) throws OnErrorThrowable {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = GeneralUtils.jwPlayerIsolate(body);

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, elementHtml.substring(elementHtml.indexOf("file: '") + 7, elementHtml.indexOf("',"))));

        return videos;
    }

}
