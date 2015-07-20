package dulleh.akhyou.Episodes.Providers;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import dulleh.akhyou.Models.Anime;
import rx.exceptions.OnErrorThrowable;

public class BaseEpisodesProvider implements EpisodesProvider{
    @Override
    public Anime fetchAnime(String url) {
        return null;
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

}
