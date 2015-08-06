package dulleh.akhyou.Models.SourceProviders;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeBamSourceProvider implements SourceProvider{
    @Override
    public List<Video> fetchSource(String embedPageUrl) {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = GeneralUtils.jwPlayerIsolate(body);

        String notVideoUrl = elementHtml.substring(elementHtml.indexOf("file: \"") + 7, elementHtml.indexOf("\","));

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .addHeader("Referer", "http://www.animebam.net/embed/17302")
                .addHeader("X-Requested-With", "ShockwaveFlash/18.0.0.209")
                .url(notVideoUrl)
                .build();

        List<Video> videos = new ArrayList<>(1);

        try {
            String response = okHttpClient.newCall(request).execute().toString();
            videos.add(new Video(null, response.substring(response.indexOf("url=") + 4, response.lastIndexOf(".mp4") + 4)));
        }catch (IOException io) {
            throw OnErrorThrowable.from(new Throwable(io.getMessage()));
        }

        return videos;
    }
}
