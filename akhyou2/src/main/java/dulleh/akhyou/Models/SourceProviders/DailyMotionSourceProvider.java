package dulleh.akhyou.Models.SourceProviders;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class DailyMotionSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPage) {

        String body = GeneralUtils.getWebPage(embedPage);

        String elementHtml = Jsoup.parse(body).select("div.ad_box").first().nextElementSibling().html();

        elementHtml = elementHtml.substring(elementHtml.indexOf("var info = ") + 11, elementHtml.indexOf("fields")).trim();
        elementHtml = elementHtml.substring(0, elementHtml.lastIndexOf(","));

        List<Video> videos = new ArrayList<>(1);

        try {
            JsonParser jsonParser = new JsonFactory().createParser(elementHtml);

            while (!jsonParser.isClosed()) {
                JsonToken jsonToken = jsonParser.nextToken();
                String currentName = jsonParser.getCurrentName();
                if (jsonToken != null && currentName != null && currentName.contains("stream") && currentName.contains("h264")) {
                    String nextValue = jsonParser.nextTextValue();
                    if (nextValue != null) {
                        videos.add(new Video(nextValue.substring(40, 43) + "p", nextValue));
                    }
                }
            }

        } catch (Exception e) {
            throw OnErrorThrowable.from(new Throwable("DailyMotion video retrieval failed.", e));
        }

        return videos;
    }

}
