package dulleh.akhyou.Models.SourceProviders;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class VkSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) throws OnErrorThrowable {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = Jsoup.parse(body).select("div.progress").first().nextElementSibling().html();

        String onlyJson = elementHtml.substring(elementHtml.indexOf("var vars = ") + 11, elementHtml.indexOf("var fixed_player_size"));

        List<Video> videos = new ArrayList<>(1);

        try {
            String regex = "url\\d+";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            JsonParser jsonParser = new JsonFactory().createParser(onlyJson);

            while (!jsonParser.isClosed()) {
                JsonToken jsonToken = jsonParser.nextToken();
                String currentName = jsonParser.getCurrentName();

                if (jsonToken != null && currentName != null) {
                    Matcher matcher = pattern.matcher(currentName);

                    if (matcher.find()) {
                        String nextValue = jsonParser.nextTextValue();

                        if (nextValue != null) {
                            videos.add(new Video(currentName.substring(3) + "p", nextValue));
                        }

                    }

                }

            }

        } catch (IOException io) {
            throw OnErrorThrowable.from(new Throwable("Vk video retrieval failed.", io));
        }

        return videos;

    }

}
