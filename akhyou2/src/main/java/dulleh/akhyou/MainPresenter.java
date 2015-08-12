package dulleh.akhyou;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.HummingbirdApi;
import dulleh.akhyou.Utils.Events.FavouriteEvent;
import dulleh.akhyou.Utils.Events.LastAnimeEvent;
import dulleh.akhyou.Utils.Events.OpenAnimeEvent;
import dulleh.akhyou.Utils.Events.SearchEvent;
import dulleh.akhyou.Utils.Events.SearchSubmittedEvent;
import dulleh.akhyou.Utils.Events.SettingsItemSelectedEvent;
import dulleh.akhyou.Utils.Events.SnackbarEvent;
import dulleh.akhyou.Utils.GeneralUtils;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainPresenter extends RxPresenter<MainActivity>{
    private SharedPreferences sharedPreferences;
    private static final String LAST_ANIME_TITLE_PREF = "last_anime_title_preference";
    private static final String LAST_ANIME_URL_PREF = "last_anime_url_preference";
    private static final String FAVOURITES_PREF = "favourites_preference";

    public List<Anime> favouritesList;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
    }

    public void setSharedPreferences (SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void launchFromHbLink (String url) {

        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(HummingbirdApi.getTitle(url));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.deliver())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String title) {
                        EventBus.getDefault().postSticky(new SearchEvent(title));
                    }

                    @Override
                    public void onCompleted() {
                        getView().requestFragment(MainActivity.SEARCH_FRAGMENT);
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showSnackBar(new SnackbarEvent(GeneralUtils.formatError(e)));
                        this.unsubscribe();
                    }
                });

    }

    public void refreshFavouritesList () {
        Set<String> favourites = new HashSet<>(sharedPreferences.getStringSet(FAVOURITES_PREF, new HashSet<>()));

        favouritesList = new ArrayList<>(favourites.size());
        for (String favourite : favourites) {
            Anime anime = GeneralUtils.deserializeFavourite(favourite);
            if (anime != null) {
                favouritesList.add(anime);
            }
        }

    }

    public void onFreshStart (MainActivity mainActivity) {
        String lastAnimeTitle = sharedPreferences.getString(LAST_ANIME_TITLE_PREF, null);
        String lastAnimeUrl = sharedPreferences.getString(LAST_ANIME_URL_PREF, null);

        if (lastAnimeTitle != null && lastAnimeUrl != null) {
            EventBus.getDefault().postSticky(new OpenAnimeEvent(new Anime().setTitle(lastAnimeTitle).setUrl(lastAnimeUrl)));
            mainActivity.requestFragment(MainActivity.ANIME_FRAGMENT);
        } else {
            EventBus.getDefault().postSticky(new SearchEvent("Hyouka"));
            mainActivity.requestFragment(MainActivity.SEARCH_FRAGMENT);
        }
    }

    public boolean isInFavourites (Anime anime) {
        if (new HashSet<>(sharedPreferences.getStringSet(FAVOURITES_PREF, new HashSet<>()))
                .contains(GeneralUtils.serializeFavourite(anime))) {
            return true;
        }
        return false;
    }

    public void onEvent (FavouriteEvent event) {
        Set<String> favourites = new HashSet<>(sharedPreferences.getStringSet(FAVOURITES_PREF, new HashSet<>()));
        String serializedAnime = GeneralUtils.serializeFavourite(event.anime);
        if (serializedAnime != null) {
            if (event.addToFavourites) {
                favourites.add(serializedAnime);
            } else if (favourites.contains(serializedAnime)) {
                favourites.remove(serializedAnime);
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(FAVOURITES_PREF, favourites);
        editor.apply();
        refreshFavouritesList();
        if (getView() != null) {
            getView().favouritesChanged();
        }
    }

    public void onEvent (LastAnimeEvent event) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_ANIME_TITLE_PREF, event.anime.getTitle());
            editor.putString(LAST_ANIME_URL_PREF, event.anime.getUrl());
            editor.apply();
        }
    }


    public void onEvent (SearchSubmittedEvent event) {
        EventBus.getDefault().postSticky(new SearchEvent(event.searchTerm));
        if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.ANIME_FRAGMENT) != null) {
            getView().getSupportFragmentManager().popBackStack();
        }
        if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.SEARCH_FRAGMENT) == null) {
            getView().requestFragment(MainActivity.SEARCH_FRAGMENT);
        }
    }

    public void onEvent (SettingsItemSelectedEvent event) {
        if (getView().getSupportFragmentManager().findFragmentByTag(MainActivity.SETTINGS_FRAGMENT) == null) {
            getView().requestFragment(MainActivity.SETTINGS_FRAGMENT);
        }
        getView().closeDrawer();
    }

    public void onEvent (SnackbarEvent event) {
        getView().showSnackBar(event);
    }

}
