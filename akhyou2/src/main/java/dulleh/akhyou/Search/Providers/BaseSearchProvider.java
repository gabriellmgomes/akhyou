package dulleh.akhyou.Search.Providers;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import rx.Observable;
import rx.exceptions.OnErrorThrowable;

public class BaseSearchProvider implements SearchProvider{
    public static String BASE_URL;

    @Override
    public List<Anime> searchFor(String searchTerm){
        return null;
    }

    @Override
    public String encodeURL(String s) {
        try {
            return BASE_URL + URLEncoder.encode(s, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return BASE_URL + s.replace(":", "%3A")
                    .replace("/", "%2F")
                    .replace("#", "%23")
                    .replace("?", "%3F")
                    .replace("&", "%24")
                    .replace("@", "%40")
                    .replace("%", "%25")
                    .replace("+", "%2B")
                    .replace(" ", "+")
                    .replace(";","%3B")
                    .replace("=", "%3D")
                    .replace("$", "%26")
                    .replace(",", "%2C")
                    .replace("<", "%3C")
                    .replace(">", "%3E")
                    .replace("~", "%25")
                    .replace("^", "%5E")
                    .replace("`", "%60")
                    .replace("\\", "%5C")
                    .replace("[", "%5B")
                    .replace("]", "%5D")
                    .replace("{", "%7B")
                    .replace("|", "%7C")
                    .replace("\"", "%22");
        }
    }

    @Override
    public String getResponse(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw OnErrorThrowable.from(new Throwable("Failed to connect."));
        }
    }

    @Override
    public Element isolate(String document) {
        return null;
    }

}
