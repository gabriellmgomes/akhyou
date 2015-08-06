package dulleh.akhyou.Models;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class HummingbirdApi{

    public static String getTitle (String url) {

        String body = GeneralUtils.getWebPage(url.replace("anime/", "api/v1/anime/"));

        String title = null;

        try {

            JsonParser jsonParser = new JsonFactory().createParser(body);

            while (!jsonParser.isClosed()) {
                jsonParser.nextToken();

                if (jsonParser.getCurrentName() != null && jsonParser.getCurrentName().equals("title")) {
                    title = jsonParser.nextTextValue();
                    jsonParser.close();
                }

            }

        } catch (Exception e) {
            throw OnErrorThrowable.from(new Throwable("Hummingbird retrieval failed.", e));
        }

        return title;
    }

}
